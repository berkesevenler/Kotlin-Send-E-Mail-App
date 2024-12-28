package com.example.sendemail

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    // Replace with your backend URL
    private val backendUrl = "http://10.0.2.2:5000/send-email" // Use the actual deployed backend URL for production

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find views by ID
        val recipientEmailInput = findViewById<EditText>(R.id.recipientEmailInput)
        val emailSubjectInput = findViewById<EditText>(R.id.emailSubjectInput)
        val emailMessageInput = findViewById<EditText>(R.id.emailMessageInput)
        val sendEmailButton = findViewById<Button>(R.id.sendEmailButton)

        // Set click listener for the send button
        sendEmailButton.setOnClickListener {
            val recipientEmail = recipientEmailInput.text.toString().trim()
            val emailSubject = emailSubjectInput.text.toString().trim()
            val emailMessage = emailMessageInput.text.toString().trim()

            if (recipientEmail.isNotEmpty() && emailSubject.isNotEmpty() && emailMessage.isNotEmpty()) {
                sendEmail(recipientEmail, emailSubject, emailMessage)
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendEmail(recipient: String, subject: String, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val json = JSONObject()
                json.put("recipient", recipient)
                json.put("subject", subject)
                json.put("message", message)

                val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
                val request = Request.Builder()
                    .url(backendUrl)
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(this@MainActivity, "Email Sent Successfully!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(
                            this@MainActivity,
                            "Failed to send email: $responseBody",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
