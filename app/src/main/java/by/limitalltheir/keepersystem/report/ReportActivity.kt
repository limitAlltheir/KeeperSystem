package by.limitalltheir.keepersystem.report

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import by.limitalltheir.keepersystem.R
import by.limitalltheir.keepersystem.productStorage.StorageViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_report.*

class ReportActivity : AppCompatActivity() {

    private val reportAdapter = ReportAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val reportViewModel = ViewModelProvider(this).get(ReportViewModel::class.java)

        report_recycler_container.apply {
            adapter = reportAdapter
            layoutManager = LinearLayoutManager(this@ReportActivity)
            hasFixedSize()
        }

        x_report_button.setOnClickListener {
            reportViewModel.getNamesWithQuantitiesMap().observe(this, Observer { report ->
                reportAdapter.setList(report)
            })
        }
    }
}