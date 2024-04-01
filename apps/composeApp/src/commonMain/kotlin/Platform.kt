interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun shareText(content: String)

expect fun log(msg: String)