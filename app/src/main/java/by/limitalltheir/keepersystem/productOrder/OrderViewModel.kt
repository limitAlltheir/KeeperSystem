package by.limitalltheir.keepersystem.productOrder

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.limitalltheir.keepersystem.product.Product
import by.limitalltheir.keepersystem.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class OrderViewModel : ViewModel() {

    private val orderStoreCollections =
        Firebase.firestore
            .collection(USERS_COLLECTIONS)
            .document("$USER_ID")
            .collection(ORDERS_COLLECTIONS)
    private val orderProductStoreCollections =
        Firebase.firestore
            .collection(USERS_COLLECTIONS)
            .document("$USER_ID")
            .collection(ORDERS_COLLECTIONS).document().collection(PRODUCTS_COLLECTIONS)
    private val orderCurrentList: MutableLiveData<ArrayList<Order>> = MutableLiveData()
    private val productCurrentList: MutableLiveData<ArrayList<Product>> = MutableLiveData()

    init {
        val orderList = ArrayList<Order>()
        val productList = ArrayList<Product>()
        orderStoreCollections.addSnapshotListener { querySnapshot, error ->
            error?.let {
                Toast.makeText(Application(), it.message, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            orderList.clear()
            querySnapshot?.let { it ->
                for (document in it) {
                    val docId = document.id
                    val db = FirebaseFirestore.getInstance().collection(USERS_COLLECTIONS)
                        .document("$USER_ID").collection(
                            ORDERS_COLLECTIONS
                        ).document(docId)
                    db.get().addOnCompleteListener {
                        if (it.isSuccessful) {
                            val data = it.result
                            if (data!!.exists()) {
                                val order = data.toObject<Order>()
                                if (order != null) {
                                    orderList.add(order)
                                    productList.addAll(order.products)
                                    orderCurrentList.postValue(orderList)
                                    productCurrentList.postValue(productList)
                                }
                            }
                        } else {
                            Log.d(TAG, "ERROR")
                        }
                    }
                }
//                orderCurrentList.postValue(orderList)
            }
        }
    }

    fun getOrderList() = orderCurrentList
    fun getProductList() = productCurrentList
}