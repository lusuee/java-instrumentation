package org.example;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class SimpleAgent {

  public static void premain(String agentArgs, Instrumentation instrumentation)
      throws UnmodifiableClassException, ClassNotFoundException {
    doTest(agentArgs, instrumentation);

    String className = "";
    transformClass(className, instrumentation);
  }

  private static void transformClass(String className, Instrumentation instrumentation) {
    Class<?> targetClass;
    try {
      targetClass = Class.forName(className);
      transform(targetClass, targetClass.getClassLoader(), instrumentation);
    } catch (Exception e) {
      throw new RuntimeException("Class not found: " + className + " with Class.forName.");
    }

    for (Class<?> loadedClass : instrumentation.getAllLoadedClasses()) {
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
