package org.example;

import com.alibaba.fastjson.JSONObject;

public class A {

  private static JSONObject au;

  public static JSONObject readAu() {
    au = new JSONObject();
    au.put("hello", "world");
    System.out.println("Hello World!");
    System.out.println(au.toJSONString());
    au.put("aaaa", "bbbb");
    return au;
  }

  public static String get(String key) {
    return au.getString(key);
  }
}
