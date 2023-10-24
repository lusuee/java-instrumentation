package org.example;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter;

public class MyTransformer implements ClassFileTransformer {

  private final String targetClassName;

  private final ClassLoader targetClassLoader;

  public MyTransformer(String targetClassName, ClassLoader targetClassLoader) {
    this.targetClassName = targetClassName;
    this.targetClassLoader = targetClassLoader;
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

    System.out.println("before write file...");
    try {
      File file = new File("A11024-1.class");
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(classfileBuffer);
      fos.flush();
      fos.close();
      System.out.println(file.getAbsolutePath());
    } catch (Exception e) {
      e.printStackTrace();
    }

    //    if (loader.equals(targetClassLoader)) {

    System.out.println("[Agent] Transforming class: " + className);

    ClassReader classReader = new ClassReader(classfileBuffer);
    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

    //    String owner = "org/example/A";
    String owner = "com/metasoft/framework/auth/AuthService";

    ClassVisitor classVisitor =
        new ClassVisitor(Opcodes.ASM5, classWriter) {

          @Override
          public MethodVisitor visitMethod(
              int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            // 检查是否访问的是 readAu 方法
            if ("readAu".equals(name) && "()Lcom/alibaba/fastjson/JSONObject;".equals(desc)) {
              System.out.println(name);
              mv =
                  new AdviceAdapter(Opcodes.ASM5, mv, access, name, desc) {

                    @Override
                    protected void onMethodExit(int opcode) {
                      if (opcode == Opcodes.ARETURN) {
                        visitLdcInsn("authnum");
                        visitLdcInsn("1500");
                        visitMethodInsn(
                            Opcodes.INVOKEVIRTUAL,
                            "com/alibaba/fastjson/JSONObject",
                            "put",
                            "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;",
                            false);
                        visitInsn(Opcodes.POP);
                        visitFieldInsn(
                            Opcodes.GETSTATIC, owner, "au", "Lcom/alibaba/fastjson/JSONObject;");

                        visitLdcInsn("authdead");
                        visitLdcInsn("2023-12-31");
                        visitMethodInsn(
                            Opcodes.INVOKEVIRTUAL,
                            "com/alibaba/fastjson/JSONObject",
                            "put",
                            "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;",
                            false);
                        visitInsn(Opcodes.POP);
                        visitFieldInsn(
                            Opcodes.GETSTATIC, owner, "au", "Lcom/alibaba/fastjson/JSONObject;");
                      }
                    }
                  };

              System.out.println("modify end...");
            }
            return mv;
          }
        };

    System.out.println("start accept...");
    try {
      classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
      byte[] byteArray = classWriter.toByteArray();

      System.out.println("write other file...");
      File file = new File("A11024.class");
      System.out.println(file.getAbsolutePath());
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(byteArray);
      fos.flush();
      fos.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return classWriter.toByteArray();
  }
}
