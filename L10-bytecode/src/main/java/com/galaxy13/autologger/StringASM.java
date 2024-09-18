package com.galaxy13.autologger;

import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class StringASM {

    private static final Handle CONCAT_HANDLE = new Handle(
            Opcodes.H_INVOKESTATIC,
            "java/lang/invoke/StringConcatFactory",
            "makeConcatWithConstants",
            MethodType.methodType(
                    CallSite.class,
                    MethodHandles.Lookup.class,
                    String.class,
                    MethodType.class,
                    String.class,
                    Object[].class).toMethodDescriptorString(),
            false);

    private final MethodVisitor mv;

    private StringASM(MethodVisitor mv) {
        this.mv = mv;
    }

    public static StringASM initEmpty(MethodVisitor mv) {
        mv.visitLdcInsn("");
        return new StringASM(mv);
    }

    private String toStringDescriptor(Type type) {
        String appendArgument = "(Ljava/lang/Object;)";
        switch (type.getSort()) {
            case Type.INT:
                appendArgument = "(I)";
                break;
            case Type.LONG:
                appendArgument = "(J)";
                break;
            case Type.FLOAT:
                appendArgument = "(F)";
                break;
            case Type.DOUBLE:
                appendArgument = "(D)";
                break;
            case Type.BYTE:
                appendArgument = "(B)";
                break;
            case Type.BOOLEAN:
                appendArgument = "(Z)";
                break;
            case Type.SHORT:
                appendArgument = "(S)";
                break;
            case Type.CHAR:
                appendArgument = "(C)";
                break;
            default:
        }
        return appendArgument + "Ljava/lang/String;";
    }

    public void appendParameterInfo(Type type, int index) {
        appendString(type.getClassName() + " -> ");
        appendParameterValue(type, index);
        appendString("\r\n");
    }

    public void out() {
        mv.visitFieldInsn(Opcodes.GETSTATIC,
                "java/lang/System",
                "out",
                Type.getObjectType("java/io/PrintStream").getDescriptor());
        mv.visitInsn(Opcodes.SWAP);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V",
                false);
    }

    public void appendString(String s) {
        mv.visitLdcInsn(s);
        concatStrings();
    }

    private void appendParameterValue(Type type, int index) {
        mv.visitVarInsn(type.getOpcode(Opcodes.ILOAD), index);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                "java/lang/String",
                "valueOf",
                toStringDescriptor(type),
                false);
        concatStrings();
    }

    private void concatStrings() {
        mv.visitInvokeDynamicInsn("makeConcatWithConstants",
                "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
                CONCAT_HANDLE,
                "\u0001\u0001");
    }
}
