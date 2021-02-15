package by.limitalltheir.keepersystem.report

import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import by.limitalltheir.keepersystem.R
import com.afollestad.date.dayOfMonth
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onCancel
import com.afollestad.materialdialogs.datetime.datePicker
import kotlinx.android.synthetic.main.activity_report.*
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "tag"

class ReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val currentDate = Date()
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        var dateText = format.format(currentDate)
        date_tv.text = dateText

        date_tv.setOnClickListener {
            MaterialDialog(this)
                .show {
                    datePicker { dialog, date ->
                        dateText = format.format(date.time)
                        Log.d(TAG, dateText)
                        dialog.cancelOnTouchOutside
                    }
                }
                .positiveButton {
                    date_tv.text = dateText ?: "ERROR"
                }
        }

        z_report_button.setOnClickListener {

        }

        x_report_button.setOnClickListener {

        }
    }
}
