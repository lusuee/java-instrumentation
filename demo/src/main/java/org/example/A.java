package org.example;

import com.alibaba.fastjson.JSONObject;

public class A {

  public static JSONObject au;

  public static JSONObject readAu() {
    au = new JSONObject();
    au.put("hello", "world");
    au.put("authnum", "500");
    System.out.println("Hello World!");
    System.out.println(au.toJSONString());
    au.put("aaaa", "bbbb");
    au.put("cccc", "dddd");
    return au;
  }

  public static String get(String key) {
    return au.getString(key);
  }
}
