package org.example;

import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;

public class CheckAuTransformer implements ClassFileTransformer {

  private final String targetClassName;

  public CheckAuTransformer(String targetClassName) {
    this.targetClassName = targetClassName;
  }

  @Override
  public byte[] transform(
      ClassLoader loader,
      String className,
      Class<?> classBeingRedefined,
      ProtectionDomain protectionDomain,
      byte[] classfileBuffer)
      throws IllegalClassFormatException {

    String finalTargetClassName = this.targetClassName.replaceAll("\\.", "/");
    if (!finalTargetClassName.equals(className)) {
      return classfileBuffer;
    }

    try {
      ClassReader cr = new ClassReader(classfileBuffer);
      ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

      ClassVisitor cv =
          new ClassVisitor(ASM5, cw) {
            @Override
            public MethodVisitor visitMethod(
                int access, String name, String desc, String signature, String[] exceptions) {

              MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

              if ("validLicense".equals(name)) {
                mv =
                    new AdviceAdapter(ASM5, mv, access, name, desc) {
                      @Override
                      protected void onMethodExit(int opcode) {
                        if (opcode == Opcodes.RETURN) {
                          visitFieldInsn(
                              Opcodes.GETSTATIC,
                              "java/lang/System",
                              "out",
                              "Ljava/io/PrintStream;");
                          visitFieldInsn(
                              Opcodes.GETSTATIC,
                              "com/metasoft/framework/auth/ControllerService",
                              "CHECK_LICENSE_RESULT",
                              "Ljava/lang/Integer;");
                          visitMethodInsn(
                              Opcodes.INVOKEVIRTUAL,
                              "java/io/PrintStream",
                              "println",
                              "(Ljava/lang/Integer;)V",
                              false);

                          visitLdcInsn(0);
                          visitFieldInsn(
                              Opcodes.PUTSTATIC,
                              "com/metasoft/framework/auth/ControllerService",
                              "CHECK_LICENSE_RESULT",
                              "Ljava/lang/Integer;");

                          visitFieldInsn(
                              Opcodes.GETSTATIC,
                              "java/lang/System",
                              "out",
                              "Ljava/io/PrintStream;");
                          visitFieldInsn(
                              Opcodes.GETSTATIC,
                              "com/metasoft/framework/auth/ControllerService",
                              "CHECK_LICENSE_RESULT",
                              "Ljava/lang/Integer;");
                          visitMethodInsn(
                              Opcodes.INVOKEVIRTUAL,
                              "java/io/PrintStream",
                              "println",
                              "(Ljava/lang/Integer;)V",
                              false);
                        }
                      }
                    };
              }

              return mv;
            }
          };

      System.out.println("valid license modify end...");
      cr.accept(cv, ClassReader.EXPAND_FRAMES);

      byte[] byteArray = cw.toByteArray();
      try (FileOutputStream fos = new FileOutputStream("A11024.class")) {
        fos.write(byteArray);
        fos.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return byteArray;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return classfileBuffer;
  }
}
