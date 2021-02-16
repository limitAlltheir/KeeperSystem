package by.limitalltheir.keepersystem.productStorage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import by.limitalltheir.keepersystem.R
import by.limitalltheir.keepersystem.product.Product
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

private const val KEY1 = "key1"
private const val KEY2 = "key2"
private const val KEY3 = "key3"

class EditActivity : AppCompatActivity() {

    private val productStoreCollections = Firebase.firestore.collection("products")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        name_edit_et.setText(intent.getStringExtra(KEY1))
        price_edit_et.setText(intent.getStringExtra(KEY2))
        group_edit_et.setText(intent.getStringExtra(KEY3))

        upgrade_button.setOnClickListener {
            val oldProduct = getOldProduct()
            val newProductMap = getNewPersonMap()
            updateProduct(oldProduct, newProductMap)
            Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getOldProduct(): Product {
        val nameText = intent.getStringExtra(KEY1)
        val priceText = intent.getStringExtra(KEY2)
        val groupText = intent.getStringExtra(KEY3)
        return Product(
            nameText.toString(),
            priceText.toString().toDouble(),
            groupText.toString()
        )
    }

    private fun getNewPersonMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        if (name_edit_et.toString().isNotEmpty()) {
            map["name"] = name_edit_et.text.toString()
        }
        if (price_edit_et.toString().isNotEmpty()) {
            map["price"] = price_edit_et.text.toString().toDouble()
        }
        if (group_edit_et.toString().isNotEmpty()) {
            map["group"] = group_edit_et.text.toString()
        }
        return map
    }

    private fun updateProduct(product: Product, newProductMap: Map<String, Any>) =
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
                        productStoreCollections.document(document.id).set(
                            newProductMap,
                            SetOptions.merge()
                        ).await()
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@EditActivity,
                                "No product matched",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditActivity, "No product matched", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
}