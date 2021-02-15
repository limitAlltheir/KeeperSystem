package by.limitalltheir.keepersystem.productStorage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import by.limitalltheir.keepersystem.R
import by.limitalltheir.keepersystem.product.Product
import by.limitalltheir.keepersystem.productOrder.OnItemClick
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_details_product.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

private const val KEY = "key"
private const val KEY1 = "key1"
private const val KEY2 = "key2"
private const val KEY3 = "key3"

class DetailsProductActivity : AppCompatActivity(), OnItemClick {

    private val productAdapter =
        ProductStorageAdapter(this)
    private val productStoreCollections = Firebase.firestore.collection("products")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_product)

        val details = intent.getIntExtra(KEY, 0)
        var nameForEdit = ""
        var priceForEdit = ""
        var groupForEdit = ""
        var oldProduct = Product()

        details_recycler_container.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(context)
            hasFixedSize()
        }

        val myViewModel = ViewModelProvider(this).get(StorageViewModel::class.java)

        myViewModel.getProductList().observe(this, Observer {
            val detailsList = arrayListOf(it[details])
            productAdapter.setList(detailsList)
            nameForEdit = it[details].name
            priceForEdit = it[details].price.toString()
            groupForEdit = it[details].group
            oldProduct = it[details]
        })

        edit_button.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra(KEY1, nameForEdit)
            intent.putExtra(KEY2, priceForEdit)
            intent.putExtra(KEY3, groupForEdit)
            startActivity(intent)
        }

        delete_button.setOnClickListener {
            deleteProduct(oldProduct)
        }
    }

    private fun deleteProduct(product: Product) =
        CoroutineScope(Dispatchers.IO).launch {
            val productQuery = productStoreCollections
                .whereEqualTo("name", product.name)
                .whereEqualTo("price", product.price)
                .whereEqualTo("group", product.group)
                .get()
                .await()
            if (productQuery.documents.isNotEmpty()) {
                for (document in productQuery) {
                    try {
                        productStoreCollections.document(document.id).delete().await()
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@DetailsProductActivity,
                                "No product matched",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DetailsProductActivity,
                        "No product matched",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }

    override fun onItemClick(position: Int) {

    }
}