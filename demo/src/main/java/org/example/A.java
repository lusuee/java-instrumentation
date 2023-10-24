package org.example;

import com.alibaba.fastjson.JSONObject;

public class A {

  public static JSONObject au;

  public static int CHECK_LICENSE_RESULT;

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

  public static void validLicense() {
    CHECK_LICENSE_RESULT = 1;
    //    au.put("1", "1");
    System.out.println(CHECK_LICENSE_RESULT);
    CHECK_LICENSE_RESULT = 1000;
    System.out.println(CHECK_LICENSE_RESULT);
  }

  public static String get(String key) {
    return au.getString(key);
  }
}
