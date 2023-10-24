package org.example;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.spi.AttachProvider;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class AgentLoader {

  public static void main(String[] args)
      throws AttachNotSupportedException,
          IOException,
          AgentLoadException,
          AgentInitializationException {
    AttachProvider provider =
        AttachProvider.providers().stream()
            .filter(
                p -> {
                  System.out.println(p.type());
                  System.out.println(p.listVirtualMachines());
                  return p.type().equals("windows");
                })
            .findFirst()
            .orElseThrow(() -> new AttachNotSupportedException("No suitable provider found"));

    VirtualMachine virtualMachine = provider.attachVirtualMachine("4016");
    virtualMachine.loadAgent("D:\\instrumentation-1.0-SNAPSHOT.jar");
    virtualMachine.detach();
  }

  public static void main1(String[] args) {
    System.out.println("args: " + Arrays.toString(args));
    String agentFilePath = "D:\\instrumentation-1.0-SNAPSHOT.jar";
    String applicationName = "MetaCRM6AppServerdemo80";

    // iterate all jvms and get the first one that matches our application name
    Optional<String> jvmProcessOpt =
        Optional.ofNullable(
            VirtualMachine.list().stream()
                .filter(
                    jvm -> {
                      System.out.println("jvm:" + jvm.displayName());
                      return jvm.displayName().contains(applicationName);
                    })
                .findFirst()
                .get()
                .id());

    if (!jvmProcessOpt.isPresent()) {
      System.out.println("Target Application not found");
      return;
    }

    File agentFile = new File(agentFilePath);
    try {
      String jvmPid = jvmProcessOpt.get();
      System.out.println("Attaching to target JVM with PID: " + jvmPid);
      VirtualMachine jvm = VirtualMachine.attach(jvmPid);
      jvm.loadAgent(agentFile.getAbsolutePath());
      jvm.detach();
      System.out.println("Attached to target JVM and loaded Java agent successfully");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
