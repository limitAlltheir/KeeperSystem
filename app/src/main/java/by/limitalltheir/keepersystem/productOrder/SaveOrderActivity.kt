package by.limitalltheir.keepersystem.productOrder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.limitalltheir.keepersystem.R
import by.limitalltheir.keepersystem.interfaces.OnItemClick
import by.limitalltheir.keepersystem.interfaces.SwipeToDelete
import by.limitalltheir.keepersystem.product.Product
import by.limitalltheir.keepersystem.productStorage.StorageViewModel
import by.limitalltheir.keepersystem.utils.ORDERS_COLLECTIONS
import by.limitalltheir.keepersystem.utils.USERS_COLLECTIONS
import by.limitalltheir.keepersystem.utils.USER_ID
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main_with_recycler.*
import kotlinx.android.synthetic.main.activity_save_order.*
import kotlinx.android.synthetic.main.item_order_product.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SaveOrderActivity : AppCompatActivity(), OnItemClick {

    private val orderStoreCollections =
        Firebase.firestore
            .collection(USERS_COLLECTIONS)
            .document("$USER_ID")
            .collection(ORDERS_COLLECTIONS)
    private val orderList = arrayListOf<Product>()
    private var namesList = emptyArray<String>()
    private var orderListForDelete = arrayListOf<Product>()
    private val orderAdapter =
        ProductOrderAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_order)


        // Swipe to delete
        val item = object : SwipeToDelete(this, 0, ItemTouchHelper.LEFT) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteOrder(orderListForDelete[viewHolder.adapterPosition])
            }
        }
        val itemTouchHelper = ItemTouchHelper(item)
        itemTouchHelper.attachToRecyclerView(recycler_view_save_order_container)

        // RecyclerView
        recycler_view_save_order_container.apply {
            layoutManager = LinearLayoutManager(this@SaveOrderActivity)
            adapter = orderAdapter
            hasFixedSize()
        }

        // ViewModel
        val storageViewModel = ViewModelProvider(this).get(StorageViewModel::class.java)
        storageViewModel.getProductNamesList().observe(this, Observer { names ->
            namesList = names
        })

        // ClickListener
        add_product_to_save_order_button.setOnClickListener {
            orderList.clear()
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.choose_product))
                .setNeutralButton(getString(R.string.cancelButton)) { dialogInterface, _ ->
                    orderAdapter.setList(orderList)
                    dialogInterface.cancel()
                }
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    orderAdapter.setList(orderList)
                    orderListForDelete.addAll(orderList)
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

        save_order_button.setOnClickListener {
            addOrder(getOrder(orderList))
        }

//        add_quantity.setOnClickListener {
//
//        }
    }

    private fun getOrder(products: ArrayList<Product>): Order {
        var sum = 0.0
        for (product in products) {
            sum += product.price
        }
        return Order(products, sum)
    }

    private fun addOrder(order: Order) = CoroutineScope(Dispatchers.IO).launch {
        orderStoreCollections.add(order).addOnCompleteListener { request ->
            if (request.isSuccessful) {
                Toast.makeText(this@SaveOrderActivity, "Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@SaveOrderActivity, "ERROR", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteOrder(product: Product) =
        MaterialAlertDialogBuilder(this)
            .setTitle("Вы уверены?")
            .setMessage("Удаление заказа.")
            .setNeutralButton(R.string.cancelButton) { dialogInterface, _ ->
                orderAdapter.setList(orderList)
                dialogInterface.cancel()
            }
            .setPositiveButton(R.string.ok) { dialog, _ ->
                orderList.remove(product)
                orderAdapter.setList(orderList)
                dialog.dismiss()
            }
            .show()

    override fun onItemClick(position: Int) {
        deleteOrder(orderListForDelete[position])
    }
}