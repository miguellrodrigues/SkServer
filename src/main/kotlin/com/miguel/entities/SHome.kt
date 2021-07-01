package com.miguel.entities

import java.util.*

class SHome(
    val id: Int = 0,
    val name: String,
    val owner: UUID = UUID.randomUUID(),
    val location: SLocation = SLocation(0, "world", .0, .0, .0)
)