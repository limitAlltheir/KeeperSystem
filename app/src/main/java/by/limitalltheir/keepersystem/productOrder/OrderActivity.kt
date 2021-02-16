package by.limitalltheir.keepersystem.productOrder

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.limitalltheir.keepersystem.utils.ORDERS_COLLECTIONS
import by.limitalltheir.keepersystem.interfaces.OnItemClick
import by.limitalltheir.keepersystem.R
import by.limitalltheir.keepersystem.utils.USERS_COLLECTIONS
import by.limitalltheir.keepersystem.utils.USER_ID
import by.limitalltheir.keepersystem.auth.AuthorizationActivity
import by.limitalltheir.keepersystem.interfaces.SwipeToDelete
import by.limitalltheir.keepersystem.product.Product
import by.limitalltheir.keepersystem.productStorage.StorageActivity
import by.limitalltheir.keepersystem.productStorage.StorageViewModel
import by.limitalltheir.keepersystem.report.ReportActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_with_recycler.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OrderActivity : AppCompatActivity(), OnItemClick {

    private val orderStoreCollections =
        Firebase.firestore
            .collection(USERS_COLLECTIONS)
            .document("$USER_ID")
            .collection(ORDERS_COLLECTIONS)
    private lateinit var toggle: ActionBarDrawerToggle
    private val orderAdapter =
        ProductOrderAdapter(this)
    private val orderList = arrayListOf<Product>()
    private var orderListForDelete = arrayListOf<Product>()
    private var namesList = emptyArray<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // RecyclerView
        recycler_view_order_container.apply {
            layoutManager = LinearLayoutManager(this@OrderActivity)
            adapter = orderAdapter
            hasFixedSize()
        }

        // Swipe to delete
        val item = object : SwipeToDelete(this, 0, ItemTouchHelper.LEFT) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteOrder(orderListForDelete[viewHolder.adapterPosition])
            }
        }
        val itemTouchHelper = ItemTouchHelper(item)
        itemTouchHelper.attachToRecyclerView(recycler_view_order_container)


        // ActionBar menu
        toggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // ViewModel
        val storageViewModel = ViewModelProvider(this).get(StorageViewModel::class.java)
        val orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        storageViewModel.getProductNamesList().observe(this, Observer { names ->
            namesList = names
        })
        orderViewModel.getOrderList().observe(this, Observer {
            if (it.size == 0) {
                hint_tv.visibility = View.VISIBLE
                orderListForDelete = it
                orderAdapter.setList(it)
            } else {
                orderListForDelete = it
                orderAdapter.setList(it)
                hint_tv.visibility = View.GONE
            }
        })


        // ClickListener
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.store -> {
                    val intent = Intent(this, StorageActivity::class.java)
                    startActivity(intent)
                }
                R.id.report -> {
                    val intent = Intent(this, ReportActivity::class.java)
                    startActivity(intent)
                }
                R.id.logout -> {
                    val mAuth = FirebaseAuth.getInstance()
                    mAuth.signOut()
                    val intent = Intent(this, AuthorizationActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

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
        for (product in products) {
            orderStoreCollections.add(product).addOnCompleteListener { request ->
                if (request.isSuccessful) {
                    Toast.makeText(this@OrderActivity, "Successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@OrderActivity, "ERROR", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveOrder2(order: Order) = CoroutineScope(Dispatchers.IO).launch {
        orderStoreCollections.add(order).addOnCompleteListener { request ->
            if (request.isSuccessful) {
                Toast.makeText(this@OrderActivity, "Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@OrderActivity, "ERROR", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteOrder(product: Product) =
        MaterialAlertDialogBuilder(this)
            .setTitle("Вы уверены?")
            .setMessage("Удаление заказа.")
            .setNeutralButton(R.string.cancelButton) { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .setPositiveButton(R.string.ok) { dialog, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    val productQuery = orderStoreCollections
                        .whereEqualTo("name", product.name)
                        .whereEqualTo("price", product.price)
                        .whereEqualTo("group", product.group)
                        .limit(1)
                        .get()
                        .await()
                    if (productQuery.documents.isNotEmpty()) {
                        for (document in productQuery) {
                            orderStoreCollections.document(document.id).delete().await()
                        }
                    }
                }
                dialog.dismiss()
            }
            .show()

    override fun onItemClick(position: Int) {
        deleteOrder(orderListForDelete[position])
    }
}