package com.project.fft

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

data class MenuItem(
    val itemName: String = "",
    val image: String = "",
    val description: String = "",
    val price: Double = 0.0
) : Parcelable

class MenuActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var viewCartButton: Button
    private val menuItems = mutableListOf<MenuItem>()
    private val cartItems = mutableListOf<MenuItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)


        val menuCollection = intent.getStringExtra("menuCollection")

        menuRecyclerView = findViewById(R.id.menuRecyclerView)
        viewCartButton = findViewById(R.id.viewCartButton)
        menuRecyclerView.layoutManager = LinearLayoutManager(this)

        menuAdapter = MenuAdapter(menuItems) { menuItem ->
            // Handle the + button click to add to the cart
            Toast.makeText(this, "${menuItem.itemName} added to cart", Toast.LENGTH_SHORT).show()
            cartItems.add(menuItem)
        }

        menuRecyclerView.adapter = menuAdapter


        if (menuCollection != null) {
            fetchMenuItems(menuCollection)
        }

        viewCartButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.putParcelableArrayListExtra("cartItems", ArrayList(cartItems)) // Send the cart items to CartActivity
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchMenuItems(menuCollection: String) {
        db.collection(menuCollection)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val item = document.toObject(MenuItem::class.java)
                    menuItems.add(item)
                }

                menuAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("MenuActivity", "Error fetching menu items", exception)
                Toast.makeText(this, "Error loading menu", Toast.LENGTH_SHORT).show()
            }
    }
}