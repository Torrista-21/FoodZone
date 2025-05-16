
package com.bca.adminfoodorderingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bca.adminfoodorderingapp.databinding.ActivityLoginBinding
import com.bca.adminfoodorderingapp.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class LoginActivity : AppCompatActivity() {


    private lateinit var email : String
    private lateinit var password: String
    private lateinit var auth : FirebaseAuth
    private lateinit var database : DatabaseReference


    private val binding: ActivityLoginBinding by lazy {

        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        database = Firebase.database.reference


        binding.loginButton.setOnClickListener {

            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()

            if(email.isBlank() || password.isBlank() ){
                Toast.makeText(this, "Sorry!! You aren't Registered With Us", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Logged In SuccessFully", Toast.LENGTH_SHORT).show()
                loginUser(email,password)
            }


        }
            binding.dontHaveAccount.setOnClickListener{
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
        }
        setContentView(binding.root)
    }

    private fun loginUser(email:String, password: String) {

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->

        if(task.isSuccessful){
            val user = auth.currentUser
            updateUi(user!!)
        }
            else{
            Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
            Log.d("Account","createUserAccount:Authentication Failed",task.exception)
        }

        }
    }

    private fun saveUserData() {
        email = binding.email.text.toString().trim()
        password = binding.password.text.toString().trim()

        val user = UserModel(
            email,password
        )

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            if (it != null) {
               database.child("AdminUser").child(it).setValue(user)
            }
        }
    }

    private fun updateUi(user: FirebaseUser) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()

    }
}