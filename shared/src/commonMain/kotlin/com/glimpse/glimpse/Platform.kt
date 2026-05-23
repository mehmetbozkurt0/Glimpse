package com.glimpse.glimpse

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform