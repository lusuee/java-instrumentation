package org.example;

/** Hello world! */
public class App {
  public static void main(String[] args) {
    A.readAu();
    System.out.println(A.get("aaaa"));
    System.out.println(A.au.toJSONString());
  }
}
