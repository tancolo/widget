package com.tancolo.purekotlin

/**
 * To demonstrate Kotlin inner / nest class
 * inner class <===> non-static inner class (for Java)
 * nest class <===> static inner class (for Java)
 */

// To static variable & function with global variable and function in Kotlin.
private const val staticName = ""
private const val staticAge = 0
fun printStaticName() {
    println("staticName = $staticName, staticAge = $staticAge")
}

class KotlinOuter {
    private var name: String = ""
    private var age: Int = 0

    fun printName() {
        println("name = $name, age = $age")
    }

    // Inner class (non-static inner class)
    inner class RealInnerClass {
        fun testInner() {
            name = "tancolo"
            age = 35
            printName()
        }
    }

    // Nest inner class, the same with static inner class of Java
    class NestClass {
        fun testInner() {
            printStaticName()
        }
    }
}

fun main() {
    // Construct an object of real inner class
    val innerClass = KotlinOuter().RealInnerClass()
    innerClass.testInner()

    // Construct an object of nest inner class
    val nestClass = KotlinOuter.NestClass()
    nestClass.testInner()
}