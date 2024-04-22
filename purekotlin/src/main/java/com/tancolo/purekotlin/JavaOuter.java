package com.tancolo.purekotlin;

/**
 * To show Java outer class and inner class, and static inner class.
 */
public class JavaOuter {
    // Class member variables and function
    private String name;
    private int age;

    public void printInfo() {
        System.out.println("name = " + name + ", age = " + age);
    }

    // Class lass static properties and method
    private static String staticName;
    private static int staticAge;

    static void printStaticInfo() {
        System.out.println("static name = " + staticName + ", static age = " + staticAge);
    }

    // Non-static inner class
    class InnerClass {
        public void testInnerClass() {
            name = "tancolo";
            age = 35;
            printInfo();
        }
    }

    // Static inner class
    static class StaticInnerClass {
        public void testInnerClass() {
            staticName = "tancolo";
            staticAge = 35;
            printStaticInfo();
        }
    }
}
