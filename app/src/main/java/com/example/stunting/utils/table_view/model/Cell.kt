package com.example.stunting.utils.table_view.model

import com.evrencoskun.tableview.filter.IFilterableModel
import com.evrencoskun.tableview.sort.ISortableModel


open class Cell(private val mId: String, private val mData: Any?) : ISortableModel, IFilterableModel {

    private val mFilterKeyword: String = mData.toString()

    /**
     * This is necessary for sorting process.
     * See ISortableModel
     */
    override fun getId(): String = mId

    /**
     * This is necessary for sorting process.
     * See ISortableModel
     */
    override fun getContent(): Any? = mData

    fun getData(): Any? = mData

    override fun getFilterableKeyword(): String = mFilterKeyword
}
