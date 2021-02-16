package by.limitalltheir.keepersystem.report

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.limitalltheir.keepersystem.product.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

private const val TAG = "tag"

class ReportViewModel : ViewModel() {

    private val orderStoreCollections = Firebase.firestore.collection("orders")
    private val productStoreCollections = Firebase.firestore.collection("products")
    private val quantityList: MutableLiveData<ArrayList<Int>> = MutableLiveData()
    private val namesList: MutableLiveData<ArrayList<String>> = MutableLiveData()
    private val mapMap: MutableLiveData<Map<String, Int>> = MutableLiveData()

    init {
        val counterList = ArrayList<Int>()
        val nameList = ArrayList<String>()
        productStoreCollections.addSnapshotListener { querySnapshot, error ->
            error?.let {
                Toast.makeText(Application(), it.message, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            querySnapshot?.let {
                for (document in it) {
                    val product = document.toObject<Product>()
                    nameList.add(product.name)
                    Log.d(TAG, "$nameList")
                    var counter: Int
                    orderStoreCollections
                        .whereEqualTo("name", product.name)
                        .whereEqualTo("price", product.price)
                        .whereEqualTo("group", product.group)
                        .get()
                        .addOnSuccessListener { orderQuery ->
                            counter = if (orderQuery.isEmpty) {
                                0
                            } else {
                                orderQuery.size()
                            }
                            counterList.add(counter)
                            Log.d(TAG, "counter $counter")
                        }
                    Log.d(TAG, "counterList $counterList")
                }
                Log.d(TAG, "counterList2 $counterList")
                Log.d(TAG, "$nameList")
            }
        }
        quantityList.postValue(counterList)
        namesList.postValue(nameList)
    }

    private val names = namesList.value
    private val values = quantityList.value

    fun getNamesWithQuantitiesMap(): MutableLiveData<Map<String, Int>> {
        val map = mutableMapOf<String, Int>()
        if (names != null && values != null) {
            for ((i, name) in names.withIndex()) {
                map[name] = values[i]
            }
        }
        mapMap.postValue(map)
        return mapMap
    }
}