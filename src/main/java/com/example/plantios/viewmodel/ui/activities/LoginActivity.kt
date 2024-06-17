package com.example.plantios.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plantios.R
import com.example.plantios.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.signupPromptTextView.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
        binding.loginButton.setOnClickListener {
            signIn()
        }
    }
    private fun signIn() {
        val emailAddress = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        mAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener {
                loginTask ->
            if (loginTask.isSuccessful) {
                Toast.makeText(this, "You are successfully logged in..", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
            } else {
                Toast.makeText(this, "Login Failed! Internal Error..", Toast.LENGTH_SHORT).show()
            }
        }
    }
}