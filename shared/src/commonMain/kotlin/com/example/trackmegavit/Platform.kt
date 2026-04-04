package com.example.trackmegavit

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform