package com.sustech.sus_community

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform