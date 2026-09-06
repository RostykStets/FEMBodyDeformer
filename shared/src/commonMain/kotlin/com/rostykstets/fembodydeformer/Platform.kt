package com.rostykstets.fembodydeformer

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform