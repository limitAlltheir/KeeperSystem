package by.limitalltheir.keepersystem.report

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.limitalltheir.keepersystem.R
import kotlinx.android.synthetic.main.report_item.view.*

class ReportAdapter(

) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    private var mapAdapter: Map<String, Int> = mutableMapOf()

    class ReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(map: Map.Entry<String, Int>) {
            with(itemView) {
                name_tv.text = map.key
                quantity_tv.text = map.value.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.report_item, parent, false)
        return ReportViewHolder(
            view
        )
    }

    override fun getItemCount() = mapAdapter.size

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        for (m in mapAdapter) {
            holder.bind(m)
        }
    }

    fun setList(map: Map<String, Int>) {
        mapAdapter = map
        notifyDataSetChanged()
    }
}