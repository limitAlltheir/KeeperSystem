package by.limitalltheir.keepersystem.productStorage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import by.limitalltheir.keepersystem.R
import kotlinx.android.synthetic.main.activity_storage.*

class StorageActivity : AppCompatActivity() {

    private val productAdapter =
        ProductStorageAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)



        add_product_to_store_button.setOnClickListener {
            val intent = Intent(this, AddToStorageActivity::class.java)
            startActivity(intent)
        }

        recycler_view_store_container.apply {
            layoutManager = LinearLayoutManager(this@StorageActivity)
            adapter = productAdapter
            hasFixedSize()
        }

        val myViewModel = ViewModelProvider(this).get(StorageViewModel::class.java)
        myViewModel.getProductList().observe(this, androidx.lifecycle.Observer {
            productAdapter.setList(it)
        })
    }
}

