package com.bca.adminfoodorderingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bca.adminfoodorderingapp.databinding.ActivityMainBinding
import com.bca.food_ordering_app.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var completedOrderedReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        binding.addMenu.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }
        binding.allItemMenu.setOnClickListener {
            val intent = Intent(this, AllItemActivity::class.java)
            startActivity(intent)
        }

        binding.outForDelivery.setOnClickListener {
            val intent = Intent(this, OutForDeliveyActivity::class.java)
            startActivity(intent)
        }

        binding.adminProfile.setOnClickListener {
            val intent = Intent(this, AdminProfileActivity::class.java)
            startActivity(intent)
        }

        binding.createNewUser.setOnClickListener {
            val intent = Intent(this, CreateUserActivity::class.java)
            startActivity(intent)
        }
        binding.pendingOrderText.setOnClickListener {
            val intent = Intent(this, PendingOrdersActivity::class.java)
            startActivity(intent)
        }
        binding.logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logout Succesfull", Toast.LENGTH_SHORT).show()
           startActivity( Intent(this, LoginActivity::class.java))
               finish()
        }


        pendingOrders()
        ordersCompleted()
        totalEarning()


    }

    private fun totalEarning() {
        var listOfTotalPay = mutableListOf<Int>()
        completedOrderedReference = FirebaseDatabase.getInstance().reference.child("DispatchedOrders")
        completedOrderedReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(orderSnapshot in snapshot.children){
                    var completeOrder = orderSnapshot.getValue(OrderDetails::class.java)
                    completeOrder?.totalAmount?.replace("Rs.", "")?.toIntOrNull()
                        ?.let{ i ->
                            listOfTotalPay.add(i)

                        }
                }
                binding.totalEarning.text =   "Rs." + listOfTotalPay.sum().toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error!!TryAgain", Toast.LENGTH_SHORT).show()

            }
        })

    }

    private fun ordersCompleted() {
        var completedOrderReference = database.reference.child("DispatchedOrders")
        var completedOrdersCount = 0
        completedOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                completedOrdersCount = snapshot.childrenCount.toInt()
                binding.completedOrders.text = completedOrdersCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error!!TryAgain", Toast.LENGTH_SHORT).show()

            }
        })

    }

    private fun pendingOrders() {


        var pendingOrderReference = database.reference.child("OrderDetails")
        var pendingOrdersCount = 0
        pendingOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pendingOrdersCount = snapshot.childrenCount.toInt()
                binding.pendingOrders.text = pendingOrdersCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error!!TryAgain", Toast.LENGTH_SHORT).show()
            }
        })

    }
}
