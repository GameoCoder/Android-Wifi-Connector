package com.gameocoder.wificonnector

internal object LoginResultEvaluator {
    fun isConnected(responseCode: Int, body: String?, finalUrl: String?): Boolean {
        val normalizedBody = body.orEmpty().lowercase()
        val normalizedUrl = finalUrl.orEmpty().lowercase()

        val successFromBody = listOf(
            "success",
            "connected",
            "logout",
            "welcome"
        ).any { normalizedBody.contains(it) }

        val likelyStillOnPortal = listOf(
            "login",
            "captive",
            "sophos"
        ).any { normalizedUrl.contains(it) }

        return (responseCode in 200..399) && (successFromBody || !likelyStillOnPortal)
    }
}
