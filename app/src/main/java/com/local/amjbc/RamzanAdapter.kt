package com.local.amjbc

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.local.amjbc.model.RamzanDayUiModel

class RamzanAdapter(
    private var items: List<RamzanDayUiModel> = emptyList()
) : RecyclerView.Adapter<RamzanAdapter.RamzanViewHolder>() {

    fun submitList(newList: List<RamzanDayUiModel>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RamzanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ramzan_day, parent, false)
        return RamzanViewHolder(view)
    }

    override fun onBindViewHolder(holder: RamzanViewHolder, position: Int) {
        holder.bind(items[position], position == items.size - 1)
    }

    override fun getItemCount(): Int = items.size

    class RamzanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val clRoot: View = itemView.findViewById(R.id.cl_item_root)
        private val tvDayPill: TextView = itemView.findViewById(R.id.tv_day_pill)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val tvSehr: TextView = itemView.findViewById(R.id.tv_sehr)
        private val tvIftar: TextView = itemView.findViewById(R.id.tv_iftar)
        private val divider: View = itemView.findViewById(R.id.divider)

        fun bind(item: RamzanDayUiModel, isLast: Boolean) {
            tvDayPill.text = item.dayNumber.toString()
            tvDate.text = item.formattedDate
            tvSehr.text = item.sehriTime
            tvIftar.text = item.iftariTime

            if (item.isToday) {
                tvDayPill.setBackgroundResource(R.drawable.bg_day_circle_today)
                tvDayPill.setTextColor(Color.WHITE)
                clRoot.setBackgroundColor(Color.parseColor("#E6F4ED"))
            } else {
                tvDayPill.setBackgroundResource(R.drawable.bg_day_circle)
                tvDayPill.setTextColor(Color.parseColor("#0F4E36"))
                clRoot.setBackgroundColor(Color.TRANSPARENT)
            }

            divider.visibility = if (isLast) View.GONE else View.VISIBLE
        }
    }
}