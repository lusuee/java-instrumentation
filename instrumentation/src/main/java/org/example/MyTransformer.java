package org.example;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

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

    if (loader.equals(targetClassLoader)) {
      System.out.println("[Agent] Transforming class: " + className);

      try {
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = cp.get(targetClassName);
        CtMethod m = cc.getDeclaredMethod("readAu");
        // 修改授权数
        m.insertAfter("au.put(\"authnum\", \"999\");");
        // 修改
        m.insertAfter("au.put(\"auth\", \"\");");
        byteCode = cc.toBytecode();
        cc.detach();
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }

    return byteCode;
  }
}
