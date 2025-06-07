package com.example.stunting.utils.table_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.example.stunting.R
import com.example.stunting.utils.table_view.holder.CellViewHolder
import com.example.stunting.utils.table_view.holder.ColumnHeaderViewHolder
import com.example.stunting.utils.table_view.holder.RowHeaderViewHolder
import com.example.stunting.utils.table_view.model.Cell
import com.example.stunting.utils.table_view.model.ColumnHeader
import com.example.stunting.utils.table_view.model.RowHeader


class TableViewAdapter(context: Context) : AbstractTableAdapter<ColumnHeader, RowHeader, Cell>() {
    private var selectedCellPosition: Pair<Int, Int>? = null

    fun setSelectedPosition(column: Int, row: Int) {
        selectedCellPosition = Pair(column, row)
        notifyDataSetChanged()
    }

    fun isSelected(column: Int, row: Int): Boolean {
        return selectedCellPosition == Pair(column, row)
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.table_view_cell_layout, parent, false)
        return CellViewHolder(view)
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: Cell?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        val cell = cellItemModel as Cell
        val cellViewHolder = holder as CellViewHolder
        val selected = isSelected(columnPosition, rowPosition)
        cellViewHolder.bind(cell, selected, rowPosition)
    }

    override fun onCreateColumnHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.table_view_column_header_layout, parent, false)
        return ColumnHeaderViewHolder(view)
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: ColumnHeader?,
        columnPosition: Int
    ) {
        val columnHeaderViewHolder = holder as ColumnHeaderViewHolder
        columnHeaderViewHolder.setColumnHeader(columnHeaderItemModel!!)
    }


    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.table_view_row_header_layout, parent, false)
        return RowHeaderViewHolder(view)
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: RowHeader?,
        rowPosition: Int
    ) {
        val rowHeaderViewHolder = holder as RowHeaderViewHolder
        rowHeaderViewHolder.setRowHeader(rowHeaderItemModel!!)
    }

    override fun onCreateCornerView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.table_view_corner_layout, parent, false)
    }
}
