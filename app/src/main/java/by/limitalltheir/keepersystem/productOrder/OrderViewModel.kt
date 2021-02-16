package by.limitalltheir.keepersystem.productOrder

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.limitalltheir.keepersystem.utils.ORDERS_COLLECTIONS
import by.limitalltheir.keepersystem.utils.USERS_COLLECTIONS
import by.limitalltheir.keepersystem.utils.USER_ID
import by.limitalltheir.keepersystem.product.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class OrderViewModel : ViewModel() {

    private val mAuth = FirebaseAuth.getInstance()

    private val orderStoreCollections =
        Firebase.firestore
            .collection(USERS_COLLECTIONS)
            .document("$USER_ID")
            .collection(ORDERS_COLLECTIONS)
    private val orderCurrentList: MutableLiveData<ArrayList<Product>> = MutableLiveData()

    init {
        val productList = ArrayList<Product>()
        orderStoreCollections.addSnapshotListener { querySnapshot, error ->
            error?.let {
                Toast.makeText(Application(), it.message, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            productList.clear()
            querySnapshot?.let { it ->
                for (document in it) {
                    val order = document.toObject<Product>()
                    productList.add(order)
                }
                orderCurrentList.postValue(productList)
            }
        }
    }

    fun getOrderList() = orderCurrentList
}