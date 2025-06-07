package com.example.stunting.utils.table_view.holder

import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.widget.TextView
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.example.stunting.R
import com.example.stunting.utils.table_view.model.Cell


class CellViewHolder(itemView: View) : AbstractViewHolder(itemView) {
    private val tvCellData: TextView = itemView.findViewById(R.id.tv_cell_data)

    fun bind(cell: Cell, isSelected: Boolean) {
        tvCellData.text = cell.getData().toString()

        if (isSelected) {
            tvCellData.setTypeface(null, Typeface.BOLD)
        } else {
            tvCellData.setTypeface(null, Typeface.NORMAL)
        }
    }
}
