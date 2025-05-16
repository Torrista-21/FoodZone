package com.bca.adminfoodorderingapp

import android.location.Address
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bca.adminfoodorderingapp.databinding.ActivityAdminProfileBinding
import com.bca.adminfoodorderingapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminProfileActivity : AppCompatActivity() {
    private val binding: ActivityAdminProfileBinding by lazy {
        ActivityAdminProfileBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var adminReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        adminReference = database.reference.child("AdminUser")
        binding.backButton.setOnClickListener {
            finish()

        }
        binding.saveInformation.setOnClickListener {
            updateUserData()
        }
        binding.name.isEnabled = false
        binding.password.isEnabled = false
        binding.email.isEnabled = false
        binding.phoneNumber.isEnabled = false
        binding.address.isEnabled = false
        binding.saveInformation.isEnabled = false

        var isEnable = false
        binding.clickableEditButton.setOnClickListener {
            isEnable = !isEnable
            binding.name.isEnabled = isEnable
            binding.password.isEnabled = isEnable
            binding.phoneNumber.isEnabled = isEnable
            binding.address.isEnabled = isEnable
            if (isEnable) {
                binding.name.requestFocus()
            }
            binding.saveInformation.isEnabled = isEnable
        }
        getAdminUserData()
    }

    private fun updateUserData() {
        var updateName = binding.name.text.toString()
        var updateAddress = binding.address.text.toString()
        var updatePhone = binding.phoneNumber.text.toString()
        var updatePassword = binding.password.text.toString()


        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid != null) {
            val userReference = adminReference.child(currentUserUid)
            userReference.child("userName").setValue(updateName)
            userReference.child("address").setValue(updateAddress)
            userReference.child("password").setValue(updatePassword)
            userReference.child("phones").setValue(updatePhone)



            auth.currentUser?.updatePassword(updatePassword)
            Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(this, "Profile Updating Failed", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getAdminUserData() {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid != null) {
            val userReference = adminReference.child(currentUserUid)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var adminName = snapshot.child("userName").getValue(String::class.java)
                        var adminEmail = snapshot.child("email").getValue(String::class.java)
                        var adminPassword = snapshot.child("password").getValue(String::class.java)
                        var adminAddress = snapshot.child("address").getValue(String::class.java)
                        var adminPhone = snapshot.child("phones").getValue(String::class.java)
                        setDataToTextView(
                            adminName,
                            adminEmail,
                            adminAddress,
                            adminPhone,
                            adminPassword
                        )

                    }
                }


                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@AdminProfileActivity,
                        "Cannot Update Information Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
        }

    }

    private fun setDataToTextView(
        adminName: Any?,
        adminEmail: Any?,
        adminAddress: Any?,
        adminPhone: Any?,
        adminPassword: Any?
    ) {
        binding.name.setText(adminName.toString())
        binding.email.setText(adminEmail.toString())
        binding.phoneNumber.setText(adminPhone.toString())
        binding.password.setText(adminPassword.toString())
        binding.address.setText(adminAddress.toString())
    }

}