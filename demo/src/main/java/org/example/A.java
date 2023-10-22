package org.example;

import com.alibaba.fastjson.JSONObject;

public class A {

  private static JSONObject object;

  public static void print() {
    object = new JSONObject();
    object.put("hello", "world");
    System.out.println("Hello World!");
    System.out.println(object.toJSONString());
  }

  public static String get(String key) {
    return object.getString(key);
  }
}
