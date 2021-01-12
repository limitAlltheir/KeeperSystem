package by.limitalltheir.keepersystem.productOrder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import by.limitalltheir.keepersystem.product.Product
import by.limitalltheir.keepersystem.R
import by.limitalltheir.keepersystem.auth.AuthorizationActivity
import by.limitalltheir.keepersystem.productStorage.StorageActivity
import by.limitalltheir.keepersystem.productStorage.StorageViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_with_recycler.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private val orderStoreCollections = Firebase.firestore.collection("orders")
    private lateinit var toggle: ActionBarDrawerToggle
    private val orderAdapter =
        ProductOrderAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var namesList = emptyArray<String>()
        val orderList = arrayListOf<Product>()

        recycler_view_order_container.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = orderAdapter
            hasFixedSize()
        }

        toggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.store -> {
                    val intent = Intent(this, StorageActivity::class.java)
                    startActivity(intent)
                }
                R.id.report -> Toast.makeText(this, "Report", Toast.LENGTH_SHORT).show()
                R.id.logout -> {
                    val mAuth = FirebaseAuth.getInstance()
                    mAuth.signOut()
                    val intent = Intent(this, AuthorizationActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        val storageViewModel = ViewModelProvider(this).get(StorageViewModel::class.java)
        val orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)

        storageViewModel.getProductNamesList().observe(this, Observer { names ->
            namesList = names
        })

        orderViewModel.getOrderList().observe(this, Observer {
            orderAdapter.setList(it)
        })

        add_product_to_order_button.setOnClickListener {
            orderList.clear()
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.choose_product))
                .setNeutralButton(getString(R.string.cancelButton)) { dialogInterface, _ ->
                    dialogInterface.cancel()
                }
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    saveOrder(orderList)
                    dialog.dismiss()
                    updateActivity()
                }
                .setMultiChoiceItems(namesList, null) { _, which, isChecked ->
                    if (isChecked) {
                        storageViewModel.getProductList()
                            .observe(this, Observer { list ->
                                orderList += list[which]
                            })
                    }
                }
                .show()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun saveOrder(products: ArrayList<Product>) = CoroutineScope(Dispatchers.IO).launch {
        try {
            for (product in products) {
                orderStoreCollections.add(product).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Successful", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateActivity() {
        val intent = intent
        overridePendingTransition(0, 0)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
    }
}