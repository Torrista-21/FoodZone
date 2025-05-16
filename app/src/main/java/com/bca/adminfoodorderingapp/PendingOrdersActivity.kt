package com.bca.adminfoodorderingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bca.adminfoodorderingapp.adapter.PendingOrderAdapter
import com.bca.adminfoodorderingapp.databinding.ActivityPendingOrdersBinding
import com.bca.food_ordering_app.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingOrdersActivity : AppCompatActivity(), PendingOrderAdapter.OnItemClicked {
    private lateinit var binding: ActivityPendingOrdersBinding
    private var listNames: MutableList<String> = mutableListOf()
    private var listOfTotalPrice: MutableList<String> = mutableListOf()
    private var listOrderItems: ArrayList<OrderDetails> = arrayListOf()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseOrderDetails: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        databaseOrderDetails = database.reference.child("OrderDetails")
        getOrderDetails()

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun getOrderDetails() {
        databaseOrderDetails.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("PendingOrders", "Fetched ${snapshot.childrenCount} orders")
                listOrderItems.clear()
                for (orderSnapshot in snapshot.children) {
                    try {
                        val orderDetails = orderSnapshot.getValue(OrderDetails::class.java)
                        orderDetails?.let {
                            listOrderItems.add(it)
                        } ?: Log.w("PendingOrders", "Null OrderDetails for snapshot: ${orderSnapshot.key}")
                    } catch (e: Exception) {
                        Log.e("PendingOrders", "Error parsing OrderDetails: ${e.message}")
                    }
                }
                addDataToListForRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PendingOrders", "Firebase error: ${error.message}")
                Toast.makeText(
                    this@PendingOrdersActivity,
                    "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun addDataToListForRecyclerView() {
        listNames.clear()
        listOfTotalPrice.clear()
        for (orderItem in listOrderItems) {
            orderItem.userName?.let { listNames.add(it) }
            orderItem.totalAmount?.let { listOfTotalPrice.add(it) }
        }
        setAdapter()
    }

    private fun setAdapter() {
        binding.pendingOrderRecycylerView.layoutManager = LinearLayoutManager(this)
        val adapter = PendingOrderAdapter(this, listNames, listOfTotalPrice, this)
        binding.pendingOrderRecycylerView.adapter = adapter
    }

    override fun onItemClickListener(position: Int) {
        if (position < 0 || position >= listOrderItems.size) {
            Toast.makeText(this, "Invalid order selected", Toast.LENGTH_SHORT).show()
            return
        }
        val userOrderDetails = listOrderItems[position]
        val intent = Intent(this, OrderDetailsActivity::class.java)
        intent.putExtra("UserOrderDetails", userOrderDetails)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("PendingOrders", "Failed to open order details: ${e.message}")
            Toast.makeText(this, "Failed to open order details: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemAcceptClickListener(position: Int) {
        if (position < 0 || position >= listOrderItems.size) {
            Toast.makeText(this, "Invalid order selected", Toast.LENGTH_SHORT).show()
            return
        }
        val childItemPushKey = listOrderItems[position].itemPushKey
        if (childItemPushKey.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid order ID", Toast.LENGTH_SHORT).show()
            return
        }
        val clickItemOrderReference = database.reference.child("OrderDetails").child(childItemPushKey)
        clickItemOrderReference.child("orderAccepted").setValue(true)
            .addOnSuccessListener {
                updateOrderAcceptedStatus(position)
            }
            .addOnFailureListener { error ->
                Log.e("PendingOrders", "Failed to accept order: ${error.message}")
                Toast.makeText(this, "Failed to accept order: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onItemDispatchClickListener(position: Int) {
        if (position < 0 || position >= listOrderItems.size) {
            Toast.makeText(this, "Invalid order selected", Toast.LENGTH_SHORT).show()
            return
        }
        val dispatchedItemPushKey = listOrderItems[position].itemPushKey
        if (dispatchedItemPushKey.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid order ID", Toast.LENGTH_SHORT).show()
            return
        }
        // Fetch latest OrderDetails to ensure paymentReceived and orderAccepted are up-to-date
        val orderReference = database.reference.child("OrderDetails").child(dispatchedItemPushKey)
        orderReference.get().addOnSuccessListener { snapshot ->
            val orderDetails = snapshot.getValue(OrderDetails::class.java)
            if (orderDetails == null) {
                Log.e("PendingOrders", "Order not found: $dispatchedItemPushKey")
                Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }
            // Check DispatchedOrders for existing paymentReceived
            val dispatchReference = database.reference.child("DispatchedOrders").child(dispatchedItemPushKey)
            dispatchReference.get().addOnSuccessListener { dispatchSnapshot ->
                val dispatchedOrder = dispatchSnapshot.getValue(OrderDetails::class.java)
                // Merge paymentReceived and orderAccepted
                val updatedOrder = orderDetails.copy(
                    paymentReceived = dispatchedOrder?.paymentReceived ?: orderDetails.paymentReceived,
                    orderAccepted = orderDetails.orderAccepted // Already true from onItemAcceptClickListener
                )
                // Write to DispatchedOrders
                dispatchReference.setValue(updatedOrder)
                    .addOnSuccessListener {
                        deleteThisItemFromOrderDetails(dispatchedItemPushKey)
                    }
                    .addOnFailureListener { error ->
                        Log.e("PendingOrders", "Failed to dispatch order: ${error.message}")
                        Toast.makeText(this, "Failed to dispatch order: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener { error ->
                Log.e("PendingOrders", "Failed to fetch dispatched order: ${error.message}")
                // Proceed with OrderDetails if DispatchedOrders doesn't exist
                dispatchReference.setValue(orderDetails)
                    .addOnSuccessListener {
                        deleteThisItemFromOrderDetails(dispatchedItemPushKey)
                    }
                    .addOnFailureListener { dispatchError ->
                        Log.e("PendingOrders", "Failed to dispatch order: ${dispatchError.message}")
                        Toast.makeText(this, "Failed to dispatch order: ${dispatchError.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener { error ->
            Log.e("PendingOrders", "Failed to fetch order: ${error.message}")
            Toast.makeText(this, "Failed to fetch order: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteThisItemFromOrderDetails(dispatchedItemPushKey: String) {
        val orderDetailsItemsReference = database.reference
            .child("OrderDetails")
            .child(dispatchedItemPushKey)
        orderDetailsItemsReference.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Order is Dispatched", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Log.e("PendingOrders", "Failed to delete order: ${error.message}")
                Toast.makeText(this, "Failed to delete order: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateOrderAcceptedStatus(position: Int) {
        if (position < 0 || position >= listOrderItems.size) {
            Toast.makeText(this, "Invalid order selected", Toast.LENGTH_SHORT).show()
            return
        }
        val userIdOfClickedItem = listOrderItems[position].userId
        val pushKeyOfClickedItem = listOrderItems[position].itemPushKey
        if (userIdOfClickedItem.isNullOrEmpty() || pushKeyOfClickedItem.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid user or order ID", Toast.LENGTH_SHORT).show()
            return
        }
        val buyHistoryReference = database.reference
            .child("UsersInformation")
            .child(userIdOfClickedItem)
            .child("History")
            .child(pushKeyOfClickedItem)
        buyHistoryReference.child("orderAccepted").setValue(true)
            .addOnSuccessListener {
                databaseOrderDetails.child(pushKeyOfClickedItem).child("orderAccepted").setValue(true)
            }
            .addOnFailureListener { error ->
                Log.e("PendingOrders", "Failed to update history: ${error.message}")
                Toast.makeText(this, "Failed to update history: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }
}