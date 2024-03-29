package com.miguel.packets

import java.lang.reflect.Field

object NMSUtil {

    fun getValue(obj: Any, name: String): Any? {
        try {
            val field: Field = obj.javaClass.getDeclaredField(name)
            field.isAccessible = true
            return field.get(obj)
        } catch (e: Exception) {
            throw Error(e.message)
        }
    }

    fun getField(obj: Any, name: String): Field? {
        try {
            return obj.javaClass.getDeclaredField(name)
        } catch (e: Exception) {
            throw Error(e.message)
        }
    }

    fun setValue(instance: Any, field: String, value: Any) {
        try {
            val f = instance.javaClass.getDeclaredField(field)
            f.isAccessible = true
            f.set(instance, value)
            f.isAccessible = false
        } catch (e: Exception) {
            throw Error(e.message)
        }
    }
}