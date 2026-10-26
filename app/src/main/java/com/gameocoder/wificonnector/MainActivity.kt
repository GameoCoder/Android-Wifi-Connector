package com.gameocoder.wificonnector

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val executor = Executors.newSingleThreadExecutor()
    private val loginService = PortalLoginService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val portalUrlInput = findViewById<EditText>(R.id.portalUrlInput)
        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val statusText = findViewById<TextView>(R.id.statusText)

        loginButton.setOnClickListener {
            statusText.text = getString(R.string.status_connecting)

            val url = portalUrlInput.text.toString().trim()
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString()

            executor.execute {
                val connected = loginService.login(url, username, password)
                runOnUiThread {
                    statusText.text = getString(
                        if (connected) R.string.status_connected else R.string.status_not_connected
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdownNow()
    }
}
