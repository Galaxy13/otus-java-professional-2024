package com.galaxy13.autologger;

import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

@SuppressWarnings({"java:S1172", "java:S1192", "java:S125", "java:S1144"})
public class Agent {
    private Agent() {
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(
                    ClassLoader loader,
                    String className,
                    Class<?> classBeingRedefined,
                    ProtectionDomain protectionDomain,
                    byte[] classfileBuffer) {
                return modifyMethod(classfileBuffer);
            }
        });
    }

    private static byte[] modifyMethod(byte[] originalClass) {
        ClassReader cr = new ClassReader(originalClass);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int access,
                                             String name,
                                             String descriptor,
                                             String signature,
                                             String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM9, mv) {
                    private boolean isLogging = false;

                    @Override
                    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                        if (descriptor.equals("Lcom/galaxy13/autologger/Log;")) {
                            isLogging = true;
                        }
                        return super.visitAnnotation(descriptor, visible);
                    }

                    @Override
                    public void visitCode() {
                        if (isLogging) {
                            createBuilder(mv);
                            appendString(mv, "Executed method: <" + name + ">, parameters:\r\n");

                            Type[] types = Type.getArgumentTypes(descriptor);

                            int index = 0;
                            if ((access & Opcodes.ACC_STATIC) == 0) {
                                index++;
                            }

                            for (Type type : types) {
                                addParameterInfo(mv, type, index);
                                index += type.getSize();
                            }
                            outputBuilder(mv);
                        }
                        super.visitCode();
                    }
                };
            }
        };
        cr.accept(cv, 0);
        return cw.toByteArray();
    }

    private static void toObject(MethodVisitor mv, Type type) {
        switch (type.getSort()) {
            case Type.INT:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                break;
            case Type.LONG:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                break;
            case Type.FLOAT:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                break;
            case Type.DOUBLE:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                break;
            case Type.BYTE:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
                break;
            case Type.BOOLEAN:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
                break;
            case Type.SHORT:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
                break;
            case Type.CHAR:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
                break;
            default:
        }
    }

    private static void addParameterInfo(MethodVisitor mv, Type type, int index) {
        appendString(mv, type.getClassName() + " -> ");
        appendParameterValue(mv, type, index);
        appendString(mv, "\r\n");
    }

    private static void builderToString(MethodVisitor mv) {
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "toString",
                "()Ljava/lang/String;",
                false);
    }

    private static void outputBuilder(MethodVisitor mv) {
        builderToString(mv);
        mv.visitFieldInsn(Opcodes.GETSTATIC,
                "java/lang/System",
                "out",
                Type.getObjectType("java/io/PrintStream").getDescriptor());
        /*
        Своп здесь используется, так как для преобразования в String необходимо, чтобы StringBuilder лежал
        на вершине стека. После на вершину стека кладётся статик реф на класс PrintWriter, и для корректной
        инвокации метода <println> аргумент должен лежать на вершине стека, поэтому PrintStream и String свапаются
        и String оказывается на вершине стека. Вариант с другой очерёдностью операций также требует свопа,
        так как StringBuilder для преобразования в String должен лежать на вершине стека.
        mv.visitFieldInsn(Opcodes.GETSTATIC,
                "java/lang/System",
                "out",
                Type.getObjectType("java/io/PrintStream").getDescriptor());
        mv.visitInsn(Opcodes.SWAP);
        builderToString(mv);
        ...
         */
        mv.visitInsn(Opcodes.SWAP);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V",
                false);
    }

    private static void createBuilder(MethodVisitor mv) {
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
        /*
        Дупликация требуется, так как вызов конструктора "выдёргивает" (pop) последнее значение из стека.
        После дупликации StringBuilder остаётся последним в стеке и доступен для взаимодействия
         */
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                "java/lang/StringBuilder",
                "<init>",
                "()V",
                false);
    }

    private static void appendString(MethodVisitor mv, String s) {
        mv.visitLdcInsn(s);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                false);
    }

    private static void appendParameterValue(MethodVisitor mv, Type type, int index) {
        mv.visitVarInsn(type.getOpcode(Opcodes.ILOAD), index);
        toObject(mv, type);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/Object;)Ljava/lang/StringBuilder;",
                false);
    }
}