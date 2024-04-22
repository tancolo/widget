package com.tancolo.purekotlin;

/**
 * Used to demonstrate Java inner / outer classes.
 */
public class JavaDemo {
    public static void main(String[] args) {
        // Create an object of normal inner class
        JavaOuter.InnerClass innerClass = new JavaOuter().new InnerClass();
        innerClass.testInnerClass();

        // Create an object of static inner class
        JavaOuter.StaticInnerClass staticInnerClass = new JavaOuter.StaticInnerClass();
        staticInnerClass.testInnerClass();
    }
}
