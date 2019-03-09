package com.monstar.azul.domain.util

fun <T> MutableList<T>.getAllAndRemove(): MutableList<T> {
    val mutableList = mutableListOf<T>()

    mutableList.addAll(this)

    this.clear()

    return mutableList
}

fun <T> MutableList<T>.getFirstItemsAndRemove(count: Int): MutableList<T> {
    if (count > this.size) {
        throw Throwable("Count bigger than collection $count > $ ${this.size}")
    }

    val mutableList = mutableListOf<T>()

    repeat(count) {
        mutableList.add(this.removeAt(0))
    }

    return mutableList
}

inline fun <T> MutableList<T>.findAndRemove(predicate: (T) -> Boolean): MutableList<T> {
    val mutableList = mutableListOf<T>()

    val iterator = this.listIterator()

    while (iterator.hasNext()) {
        val next = iterator.next()
        if (predicate(next)) {
            mutableList.add(next)
            iterator.remove()
        }
    }

    return mutableList
}

inline fun <T> Array<T>.fillUnique(fromIndex: Int = 0, toIndex: Int = size, element: (Int) -> T) {
    for (i in fromIndex until toIndex)
        this[i] = element(i)
}

