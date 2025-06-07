package com.example.stunting.utils.table_view.holder

import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.example.stunting.R
import com.example.stunting.utils.table_view.model.Cell


class CellViewHolder(itemView: View) : AbstractViewHolder(itemView) {
    private val tvCellData: TextView = itemView.findViewById(R.id.tv_cell_data)

    fun bind(cell: Cell, isSelected: Boolean, rowPosition: Int) {
        tvCellData.text = cell.getData().toString()

        if (isSelected) {
            tvCellData.setTypeface(null, Typeface.BOLD)
        } else {
            tvCellData.setTypeface(null, Typeface.NORMAL)
        }

        val bgColorRes = if (rowPosition % 2 == 0) R.color.gray else R.color.white
        tvCellData.setBackgroundColor(ContextCompat.getColor(itemView.context, bgColorRes))
    }
}
