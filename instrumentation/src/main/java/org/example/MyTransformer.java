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
    byte[] byteCode = classfileBuffer;

    String finalTargetClassName = this.targetClassName.replaceAll("\\.", "/");
    if (!finalTargetClassName.equals(className)) {
      return byteCode;
    }

    //    if (loader.equals(targetClassLoader)) {

    System.out.println("[Agent] Transforming class: " + className);

    ClassReader classReader = new ClassReader(classfileBuffer);
    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

    /*
        ClassNode node = new ClassNode(327680);
        classReader.accept(node, 0);

        for (MethodNode method : node.methods) {
          if (method.name.equals("readAu")) {
            System.out.println(method.instructions);

            ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
            while (iterator.hasNext()) {
              AbstractInsnNode insnNode = iterator.next();
            }
          }
        }
    */

    String owner = "org/example/A";
    //    String owner = "com/metasoft/framework/auth/AuthService";

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
                        //                        visitFieldInsn(
                        //                            Opcodes.GETSTATIC, owner, "au",
                        // "Lcom/alibaba/fastjson/JSONObject;");
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

                        visitLdcInsn("fullname");
                        visitLdcInsn("fudanwei");
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
            }
            return mv;
          }
        };
    System.out.println("after mv");

    classReader.accept(classVisitor, 0);
    byte[] byteArray = classWriter.toByteArray();

    try {
      FileOutputStream fos = new FileOutputStream(new File("A1.class"));
      fos.write(byteArray);
      fos.flush();
      fos.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return classWriter.toByteArray();
    //    return byteCode;

    /*
        try {
          loader.loadClass(classBeingRedefined.getName());

          ClassPool cp = ClassPool.getDefault();
          System.out.println("being defined: " + classBeingRedefined.getName());
          CtClass cc = cp.get(classBeingRedefined.getName());
          System.out.println("cc: " + cc);
          CtMethod m = cc.getDeclaredMethod("readAu");
          System.out.println("[method]" + m);

          // 修改授权数
          String after =
              "System.out.println(\"hello world! \" + au.toJSONString());"
                  + "au.put(\"authnum\", \"1999\");"
                  + "System.out.println(au.toJSONString());";
          System.out.println("after: " + after);

          m.insertAfter(after);
          byteCode = cc.toBytecode();
          cc.detach();
        } catch (Exception e) {
          e.printStackTrace();
        }
    */
    //    }

    //    return byteCode;
  }
}
