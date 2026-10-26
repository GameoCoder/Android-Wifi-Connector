package com.gameocoder.wificonnector

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginResultEvaluatorTest {

    @Test
    fun `connected when response suggests success`() {
        val result = LoginResultEvaluator.isConnected(
            responseCode = 200,
            body = "Welcome. You are now connected.",
            finalUrl = "https://example.com/home"
        )

        assertTrue(result)
    }

    @Test
    fun `not connected when still on captive login page`() {
        val result = LoginResultEvaluator.isConnected(
            responseCode = 200,
            body = "Please login",
            finalUrl = "https://sophos-portal/login"
        )

        assertFalse(result)
    }
}
