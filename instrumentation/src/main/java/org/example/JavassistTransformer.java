package org.example;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class JavassistTransformer implements ClassFileTransformer {

  @Override
  public byte[] transform(
      ClassLoader loader,
      String className,
      Class<?> classBeingRedefined,
      ProtectionDomain protectionDomain,
      byte[] classfileBuffer) {
    ClassPool cp = ClassPool.getDefault();
    System.out.println("cp: " + cp);
    try {
      System.out.println("name :" + classBeingRedefined.getName());
      CtClass ctClass = cp.get(classBeingRedefined.getName());
      CtMethod method = ctClass.getDeclaredMethod("print");
      System.out.println(method);
      method.insertBefore("System.out.println(\"Before...\");");
      method.insertAfter("System.out.println(\"After...\");");
      method.insertAfter("object.put(\"aaa\", \"bbb\");");
      method.insertAfter("System.out.println(object.toJSONString());");
      byte[] bytecode = ctClass.toBytecode();
      ctClass.detach();
      return bytecode;
    } catch (NotFoundException | CannotCompileException | IOException e) {
      System.out.println(e.getMessage());
    }
    return classfileBuffer;
  }
}
