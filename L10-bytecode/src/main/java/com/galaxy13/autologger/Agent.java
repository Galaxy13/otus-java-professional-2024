package com.galaxy13.autologger;

import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

@SuppressWarnings({"java:S1172"})
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
                            var strASM = StringASM.initEmpty(mv);
                            strASM.appendString("Executed method: <" + name + ">, parameters:\r\n");

                            Type[] types = Type.getArgumentTypes(descriptor);

                            int index = 0;
                            if ((access & Opcodes.ACC_STATIC) == 0) {
                                index++;
                            }
                            for (Type type : types) {
                                strASM.appendParameterInfo(type, index);
                                index += type.getSize();
                            }
                            strASM.out();
                        }
                        super.visitCode();
                    }
                };
            }
        };
        cr.accept(cv, 0);
        return cw.toByteArray();
    }
}