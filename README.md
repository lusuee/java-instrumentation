# Java Instrumentation

## 参考文档

https://www.baeldung.com/java-instrumentation

https://github.com/eugenp/tutorials/blob/master/core-java-modules/core-java-jvm/src/main/java/com/baeldung/instrumentation/agent/MyInstrumentationAgent.java

### manifest

https://www.baeldung.com/java-jar-manifest


java.lang.reflect.InvocationTargetException
at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
at java.lang.reflect.Method.invoke(Method.java:497)
at sun.instrument.InstrumentationImpl.loadClassAndStartAgent(InstrumentationImpl.java:386)
at sun.instrument.InstrumentationImpl.loadClassAndCallPremain(InstrumentationImpl.java:401)
Caused by: java.lang.RuntimeException: Failed to find class: com.metasoft.framework.auth.AuthService
at org.example.SimpleAgent.transformClass(SimpleAgent.java:60)
at org.example.SimpleAgent.premain(SimpleAgent.java:15)
... 6 more


使用 javassist 因为没有字节码文件，所以无法获取到对应的类