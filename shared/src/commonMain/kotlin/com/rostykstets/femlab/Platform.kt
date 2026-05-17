package com.rostykstets.femlab

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform