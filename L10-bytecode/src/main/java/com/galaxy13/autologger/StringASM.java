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

    private StringASM() {
        // default implementation prohibited
        throw new UnsupportedOperationException("Utility class");
    }

    private static String toStringDescriptor(Type type) {
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

    public static void appendParameterInfo(MethodVisitor mv, Type type, int index) {
        appendString(mv, type.getClassName() + " -> ");
        appendParameterValue(mv, type, index);
        appendString(mv, "\r\n");
    }

    public static void outputString(MethodVisitor mv) {
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

    public static void appendString(MethodVisitor mv, String s) {
        mv.visitLdcInsn(s);
        concatStrings(mv);
    }

    private static void appendParameterValue(MethodVisitor mv, Type type, int index) {
        mv.visitVarInsn(type.getOpcode(Opcodes.ILOAD), index);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                "java/lang/String",
                "valueOf",
                toStringDescriptor(type),
                false);
        concatStrings(mv);
    }

    private static void concatStrings(MethodVisitor mv) {
        mv.visitInvokeDynamicInsn("makeConcatWithConstants",
                "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
                CONCAT_HANDLE,
                "\u0001\u0001");
    }
}
