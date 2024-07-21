package com.soebes.gatherer.lecture;

import java.math.BigInteger;

import static java.lang.System.out;

public class FirstClass {

  private final String name;
  private final String sureName;

  public FirstClass(String name, String sureName) {
    this.name = name;
    this.sureName = sureName;
  }

  public String getName() {
    return name;
  }

  public String getSureName() {
    return sureName;
  }

  public void x() {
    out.println("x");
    var bigInteger = BigInteger.valueOf(200L);
    out.println("bigInteger = " + bigInteger);
  }

  public static void main() {
    out.println("FirstClass.main without parameters");
  }
  public static void main(String[] args) {
    out.println("FirstClass.main");
  }
  public static void Main(String[] args) {
    out.println("FirstClass.Main");
  }

}
