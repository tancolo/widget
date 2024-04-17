package com.tancolo.customview

import android.content.Context

fun dip2px(context: Context, dpValue: Float): Int {
    val scale = context.resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

fun String.Companion.empty() = ""

fun String.appendTimeUnit(timeUnit: String) = "$this$timeUnit"

