package by.limitalltheir.keepersystem.productStorage

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.limitalltheir.keepersystem.product.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class StorageViewModel : ViewModel() {

    private val productStoreCollections = Firebase.firestore.collection("products")
    private val productCurrentList: MutableLiveData<ArrayList<Product>> = MutableLiveData()
    private val productNamesCurrentList: MutableLiveData<Array<String>> = MutableLiveData()

    init {
        val productList = ArrayList<Product>()
        var productNamesList = emptyArray<String>()
        productStoreCollections.addSnapshotListener { querySnapshot, error ->
            error?.let {
                Toast.makeText(Application(), it.message, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            querySnapshot?.let {
                for (document in it) {
                    val product = document.toObject<Product>()
                    productList.add(product)
                    productNamesList += product.name
                }
                productList.sortBy {
                    selector(it)
                }
                productCurrentList.postValue(productList)
                productNamesCurrentList.postValue(productNamesList)
            }
        }
    }

    fun getProductList() = productCurrentList
    fun getProductNamesList() = productNamesCurrentList
    private fun selector(p: Product): String = p.group
}