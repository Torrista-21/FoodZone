package com.bca.adminfoodorderingapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bca.adminfoodorderingapp.adapter.DeliveryAdapter
import com.bca.adminfoodorderingapp.databinding.ActivityOutForDeliveyBinding
import com.bca.food_ordering_app.model.OrderDetails
import com.bumptech.glide.disklrucache.DiskLruCache
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OutForDeliveyActivity : AppCompatActivity() {
    private val binding : ActivityOutForDeliveyBinding by lazy {
        ActivityOutForDeliveyBinding.inflate(layoutInflater)
    }
    private lateinit var database: FirebaseDatabase
    private  var listOfCompleteOrderList : MutableList<OrderDetails> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.backButton.setOnClickListener {
            finish()
        }

        retrieveCompleteOrderDetails()

    setContentView(binding.root)



    }

    private fun retrieveCompleteOrderDetails() {
        database = FirebaseDatabase.getInstance()
        val completeOrderreference = database.reference.child("DispatchedOrders").orderByChild("currentTime")
        completeOrderreference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfCompleteOrderList.clear()

                for(orderSnapshot in snapshot.children){
                    val completeOrder = orderSnapshot.getValue(OrderDetails::class.java)
                    completeOrder?.let {
                        listOfCompleteOrderList.add(it)
                    }
                }
                listOfCompleteOrderList.reverse()
                setDataIntoRecyclerView()
            }


            override fun onCancelled(error: DatabaseError) {
               }

        })
    }
    private fun setDataIntoRecyclerView() {
                val customerName = mutableListOf<String>()
        val moneyStatus = mutableListOf<Boolean>()

        for(order in listOfCompleteOrderList){
            order.userName?.let{
                customerName.add(it)
            }
            moneyStatus.add(order.paymentReceived)
        }

        val adapter = DeliveryAdapter(customerName,moneyStatus)
        binding.deliveryRecyclerView.adapter = adapter
        binding.deliveryRecyclerView.layoutManager = LinearLayoutManager(this)

    }

}