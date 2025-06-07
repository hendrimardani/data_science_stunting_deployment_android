package com.example.stunting.utils.table_view.holder

import android.view.View
import android.widget.TextView
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.example.stunting.R
import com.example.stunting.utils.table_view.model.RowHeader


class RowHeaderViewHolder(itemView: View) : AbstractViewHolder(itemView) {
    private val textView = itemView.findViewById<TextView>(R.id.tv_row_header)

    fun setRowHeader(rowHeader: RowHeader) {
        textView.text = rowHeader.data
    }
}
