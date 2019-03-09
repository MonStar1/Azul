package com.monstar.azul.domain.util

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

fun ViewGroup.findViewInDept(predicate: (View) -> Boolean): MutableList<View> {
    val resultView = mutableListOf<View>()

    children.forEach {
        if (predicate(it)) {
            resultView.add(it)
        } else if (it is ViewGroup) {
            resultView.addAll(it.findViewInDept(predicate))
        }
    }

    return resultView
}