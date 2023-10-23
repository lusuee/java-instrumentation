package org.example;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;

public class SimpleAgent {

  public static void premain(String agentArgs, Instrumentation instrumentation)
      throws UnmodifiableClassException, ClassNotFoundException {
    //    doTest(agentArgs, instrumentation);
    System.out.println("premain start ...");
    //    String className = "com.metasoft.framework.auth.AuthService";
    String className = "org.example.A";
    transformClass(className, instrumentation);
  }

  public static void agentmain(String agentArgs, Instrumentation instrumentation) {
    String className = "com.metasoft.framework.auth.AuthService";
    try {
      System.out.println("agentmain start ...");
      transformClass(className, instrumentation);
    } catch (Exception e) {
      e.printStackTrace();
    }

    /*
        MyTransformer transformer = new MyTransformer();
        Class<?> target = null;
        try {
          target = Thread.currentThread().getContextClassLoader().loadClass(className);
          instrumentation.addTransformer(transformer, true);
          instrumentation.retransformClasses(target);
        } catch (ClassNotFoundException | UnmodifiableClassException e) {
          e.printStackTrace();
        } finally {
          instrumentation.removeTransformer(transformer);
        }
    */
  }

  private static void transformClass(String className, Instrumentation instrumentation) {
    Class<?> targetClass = null;
    try {
      targetClass = Class.forName(className);
      transform(targetClass, targetClass.getClassLoader(), instrumentation);
    } catch (Exception e) {
      /*      throw new RuntimeException("Class not found: " + className + " with Class.forName.");*/
      System.out.println("Class not found: " + className + " with Class.forName.");
    }

    for (Class<?> loadedClass : instrumentation.getAllLoadedClasses()) {
      System.out.println(loadedClass.getName());
      if (loadedClass.getName().equals(className)) {
        targetClass = loadedClass;
        transform(targetClass, targetClass.getClassLoader(), instrumentation);
        return;
      }
    }
    throw new RuntimeException("Failed to find class: " + className);
  }

  private static void transform(
      Class<?> targetClass, ClassLoader classLoader, Instrumentation instrumentation) {
    MyTransformer myTransformer = new MyTransformer(targetClass.getName(), classLoader);

    instrumentation.addTransformer(myTransformer, true);
    try {
      instrumentation.retransformClasses(targetClass);
    } catch (UnmodifiableClassException e) {
      throw new RuntimeException(e);
    } finally {
      instrumentation.removeTransformer(myTransformer);
    }
  }

  private static void doTest(String agentArgs, Instrumentation instrumentation)
      throws UnmodifiableClassException, ClassNotFoundException {
    JavassistTransformer transformer = new JavassistTransformer();
    Class<?> target = Thread.currentThread().getContextClassLoader().loadClass("org.example.A");
    instrumentation.addTransformer(transformer, true);
    try {
      instrumentation.retransformClasses(target);
    } finally {
      instrumentation.removeTransformer(transformer);
    }
  }
}
