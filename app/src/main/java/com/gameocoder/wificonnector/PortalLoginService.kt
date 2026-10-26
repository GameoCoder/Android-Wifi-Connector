package com.gameocoder.wificonnector

import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

internal class PortalLoginService {

    fun login(url: String, username: String, password: String): Boolean {
        return runCatching {
            val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                instanceFollowRedirects = true
                connectTimeout = 10_000
                readTimeout = 10_000
                doOutput = true
                setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                setRequestProperty("Accept", "*/*")
            }

            if (connection is HttpsURLConnection) {
                connection.sslSocketFactory = insecureSocketFactory()
                connection.hostnameVerifier = HostnameVerifier { _, _ -> true }
            }

            val body = buildFormBody(username, password)
            OutputStreamWriter(connection.outputStream).use { it.write(body) }

            val responseBody = BufferedReader(connection.inputStream.reader()).use { reader ->
                reader.readText()
            }

            LoginResultEvaluator.isConnected(
                responseCode = connection.responseCode,
                body = responseBody,
                finalUrl = connection.url?.toString()
            )
        }.getOrElse { false }
    }

    private fun buildFormBody(username: String, password: String): String {
        val encodedUsername = URLEncoder.encode(username, Charsets.UTF_8.name())
        val encodedPassword = URLEncoder.encode(password, Charsets.UTF_8.name())

        return listOf(
            "username" to encodedUsername,
            "password" to encodedPassword,
            "submit" to "Login"
        ).joinToString("&") { (key, value) -> "$key=$value" }
    }

    private fun insecureSocketFactory(): SSLSocketFactory {
        val trustManager = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) = Unit
            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = emptyArray()
        }

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), java.security.SecureRandom())
        return sslContext.socketFactory
    }
}
