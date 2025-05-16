package com.bca.adminfoodorderingapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bca.adminfoodorderingapp.databinding.ActivitySignUpBinding
import com.bca.adminfoodorderingapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Setup location dropdown
        val locationList = arrayOf("Bhaktapur", "Kathmandu", "Lalitpur", "Butwal", "Nepalgunj")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, locationList)
        binding.listOfLocation.setAdapter(adapter)
        binding.listOfLocation.threshold = 1


        binding.signupButton.setOnClickListener {
            if (validateInputs()) {
                val email = binding.email.text.toString().trim()
                val password = binding.password.text.toString().trim()
                createAccount(email, password)
            }
        }


        binding.alreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }


        setupRealTimeValidation()
    }

    private fun validateInputs(): Boolean {
        val name = binding.name.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()
        val phoneNumber = binding.phoneNumber.text.toString().trim()
        val address = binding.listOfLocation.text.toString().trim()


        if (name.isEmpty()) {
            binding.name.error = "Name is required"
            return false
        } else {
            binding.name.error = null
        }

        // Email validation
        if (email.isEmpty()) {
            binding.email.error = "Email is required"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.error = "Invalid email format"
            return false
        } else {
            binding.email.error = null
        }


        val passwordPattern = Pattern.compile("^(?=.*[A-Z])(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$")
        if (password.isEmpty()) {
            binding.password.error = "Password is required"
            return false
        } else if (!passwordPattern.matcher(password).matches()) {
            binding.password.error = "Password must be 8+ characters, with 1 uppercase and 1 special character"
            return false
        } else {
            binding.password.error = null
        }

        // Phone number validation
        if (phoneNumber.isEmpty()) {
            binding.phoneNumber.error = "Phone number is required"
            return false
        } else if (!phoneNumber.matches("\\d{10}".toRegex())) {
            binding.phoneNumber.error = "Phone number must be exactly 10 digits"
            return false
        } else {
            binding.phoneNumber.error = null
        }

        // Address (location) validation
        if (address.isEmpty()) {
            binding.listOfLocation.error = "Please select a location"
            return false
        } else {
            binding.listOfLocation.error = null
        }

        return true
    }

    private fun setupRealTimeValidation() {
        binding.name.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateInputs()
        }
        binding.email.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateInputs()
        }
        binding.password.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateInputs()
        }
        binding.phoneNumber.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateInputs()
        }
        binding.listOfLocation.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateInputs()
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                saveUserData()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                val errorMessage = task.exception?.message ?: "Unknown error"
                Toast.makeText(this, "Account Creation Failed:", Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun saveUserData() {
        val userId = auth.currentUser?.uid ?: return
        val user = UserModel(
            userName = binding.name.text.toString().trim(),
            email = binding.email.text.toString().trim(),
            phones = binding.phoneNumber.text.toString().trim(),
            address = binding.listOfLocation.text.toString().trim(),
            password = binding.password.text.toString().trim()



        )
        database.child("AdminUser").child(userId).setValue(user)
            .addOnSuccessListener {

            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Failed to save data", Toast.LENGTH_LONG).show()

            }
    }
}