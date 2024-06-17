package com.example.plantios.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.plantios.R
import com.example.plantios.data.model.UserData
import com.example.plantios.databinding.ActivitySignupBinding
import com.example.plantios.viewmodel.UserDataViewModel
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userDataViewModel: UserDataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()

        userDataViewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.loginBackTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        binding.createAccountButton.setOnClickListener {
            createNewAccount()
        }
    }

    private fun createNewAccount() {
        val emailAddress = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val cfmPassword = binding.repeatPasswordEditText.text.toString().trim()

        if (emailAddress.isNotEmpty() && password.isNotEmpty() && cfmPassword.isNotEmpty()) {
            if (password == cfmPassword) {
                mAuth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener { signupTask ->
                    if (signupTask.isSuccessful) {
                        userDataViewModel.saveUserData(getUserData())
                        Toast.makeText(this, "Account Successfully Created!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Account Creation Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please fill in all the details first!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUserData(): UserData {
        val fullName = "this"
        val email = binding.emailEditText.text.toString().trim()
        val phone = binding.phoneEditText.text.toString().trim()
        val oldImageName = "this"
        val oldImageUrl = "this"
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        return UserData(
            userId = userId,
            fullName = fullName,
            emailAddress = email,
            phone = phone,
            imageName = oldImageName,
            imageUrl = oldImageUrl
        )
    }
}