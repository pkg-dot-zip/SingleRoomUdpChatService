package com.pkg_dot_zip.lib

class Event<T> {
    private val handlers = mutableListOf<T>()

    //<editor-fold desc="Operator Overloading">
    operator fun plusAssign(handler: T) {
        handlers.add(handler)
    }

    operator fun minusAssign(handler: T) {
        handlers.remove(handler)
    }
    //</editor-fold>

    fun invoke(action: (T) -> Unit) {
        for (handler in handlers) action(handler)
    }
}
