package com.bca.adminfoodorderingapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bca.adminfoodorderingapp.adapter.OrderDetailsAdapter
import com.bca.adminfoodorderingapp.databinding.ActivityOrderDetailsBinding
import com.bca.food_ordering_app.model.OrderDetails

class OrderDetailsActivity : AppCompatActivity() {
    private val binding: ActivityOrderDetailsBinding by lazy {
        ActivityOrderDetailsBinding.inflate(layoutInflater)
    }
    private var userName: String? = null
    private var userAddress: String? = null
    private var userPhone: String? = null
    private var totalPrice: String? = null
    private var foodName: ArrayList<String> = arrayListOf()
    private var foodQuantity: ArrayList<Int> = arrayListOf()
    private var foodPrice: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }

        getDataFromIntent()
    }

    private fun getDataFromIntent() {

        val receivedOrderDetails = intent.getSerializableExtra("UserOrderDetails") as? OrderDetails
        if (receivedOrderDetails == null) {

            Toast.makeText(this, "No order details received", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        try {
            userName = receivedOrderDetails.userName
            foodName = receivedOrderDetails.foodName?.filterNotNull()?.let { ArrayList(it) } ?: arrayListOf()
            foodQuantity = receivedOrderDetails.foodQuantities?.filterNotNull()?.let { ArrayList(it) } ?: arrayListOf()
            foodPrice = receivedOrderDetails.foodPrice?.filterNotNull()?.let { ArrayList(it) } ?: arrayListOf()
            userAddress = receivedOrderDetails.address
            userPhone = receivedOrderDetails.phoneNumber
            totalPrice = receivedOrderDetails.totalAmount

            setUserDetails()
            setAdapter()
        } catch (e: Exception) {

            Toast.makeText(this, "Invalid order details", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setAdapter() {
        binding.orderDetailsRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = OrderDetailsAdapter(this, foodName, foodPrice, foodQuantity)
        binding.orderDetailsRecyclerView.adapter = adapter
    }

    private fun setUserDetails() {
        binding.name.text = userName ?: "Unknown"
        binding.address.text = userAddress ?: "N/A"
        binding.phone.text = userPhone ?: "N/A"
        binding.totalAmount.text = totalPrice ?: "0.00"
    }
}