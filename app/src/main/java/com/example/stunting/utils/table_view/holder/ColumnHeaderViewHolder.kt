package com.example.stunting.utils.table_view.holder

import android.view.View
import android.widget.TextView
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.example.stunting.R
import com.example.stunting.utils.table_view.model.ColumnHeader


class ColumnHeaderViewHolder(itemView: View) : AbstractViewHolder(itemView) {
    private val textView = itemView.findViewById<TextView>(R.id.tv_column_header)

    fun setColumnHeader(columnHeader: ColumnHeader) {
        textView.text = columnHeader.getData().toString()
    }
}
