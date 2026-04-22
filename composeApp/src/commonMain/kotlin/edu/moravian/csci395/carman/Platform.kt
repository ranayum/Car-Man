package edu.moravian.csci395.carman

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
