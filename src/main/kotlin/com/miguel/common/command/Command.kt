package com.miguel.common.command

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class Command(
    val aliases: Array<String>,
    val usage: String = "",
    val description: String,
    val min: Int = 0,
    val max: Int = -1,
    val hidden: Boolean = false,
    val player: Boolean = true,
    val console: Boolean = true,
    val permission: Permission
)