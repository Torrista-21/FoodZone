package com.bca.adminfoodorderingapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bca.adminfoodorderingapp.adapter.MenuItemAdapter
import com.bca.adminfoodorderingapp.databinding.ActivityAllItemBinding
import com.bca.adminfoodorderingapp.model.AllMenu
import com.google.android.material.animation.Positioning
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllItemActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private var menuItems: ArrayList<AllMenu> = ArrayList()
    private val binding: ActivityAllItemBinding by lazy {
        ActivityAllItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        databaseReference = FirebaseDatabase.getInstance().reference
        retrieveMenuItem()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun retrieveMenuItem() {
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("menu")

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                menuItems.clear()

                for (foodSnapShot in snapshot.children) {
                    val menuItem = foodSnapShot.getValue(AllMenu::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "DatabaseError:${error.message}")
            }


        })
    }
    private fun setAdapter() {
        val adapter = MenuItemAdapter(this@AllItemActivity,menuItems,databaseReference){position ->
            deleteMenuItems(position)

        }
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.menuRecyclerView.adapter = adapter

    }

    private fun deleteMenuItems(position:Int) {
        val menuItemDelete = menuItems[position]
        val menuItemKey = menuItemDelete.key
        val foodMenuReference = database.reference.child("menu").child(menuItemKey!!)
        foodMenuReference.removeValue().addOnCompleteListener { task ->
            if(task.isSuccessful){
                menuItems.removeAt(position)
                binding.menuRecyclerView.adapter?.notifyItemRemoved(position)
                Toast.makeText(this, "Item deleted Successfully", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Item cannot be deleted", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
