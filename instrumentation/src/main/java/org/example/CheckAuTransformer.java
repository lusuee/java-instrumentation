package org.example;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter;

/** 修改 validLicense 方法 */
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
      byte[] classfileBuffer) {

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
                        System.out.println(opcode);
                        if (opcode == Opcodes.RETURN) {
                          visitInsn(Opcodes.ICONST_0);
                          visitFieldInsn(Opcodes.PUTSTATIC, className, "CHECK_LICENSE_RESULT", "I");

                          visitFieldInsn(
                              Opcodes.GETSTATIC,
                              "java/lang/System",
                              "out",
                              "Ljava/io/PrintStream;");
                          visitFieldInsn(Opcodes.GETSTATIC, className, "CHECK_LICENSE_RESULT", "I");
                          visitMethodInsn(
                              Opcodes.INVOKEVIRTUAL,
                              "java/io/PrintStream",
                              "println",
                              "(I)V",
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

      return cw.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return classfileBuffer;
  }
}
