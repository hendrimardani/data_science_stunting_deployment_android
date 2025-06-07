package com.example.stunting.utils.table_view


import com.example.stunting.utils.table_view.model.Cell
import com.example.stunting.utils.table_view.model.ColumnHeader
import com.example.stunting.utils.table_view.model.RowHeader
import java.util.Random


class TableViewModel {
//    private val mBoyDrawable: Int
//    private val mGirlDrawable: Int
//    private val mHappyDrawable: Int
//    private val mSadDrawable: Int

//    init {
//        // initialize drawables
//        mBoyDrawable = R.drawable.ic_male
//        mGirlDrawable = R.drawable.ic_female
//        mHappyDrawable = R.drawable.ic_happy
//        mSadDrawable = R.drawable.ic_sad
//    }

    private val simpleRowHeaderList: List<RowHeader>
        get() {
            val list = ArrayList<RowHeader>()
            for (i in 0 until ROW_SIZE) {
                val header = RowHeader(i.toString(), "row $i")
                list.add(header)
            }

            return list
        }

    private val randomColumnHeaderList: List<ColumnHeader>
        /**
         * This is a dummy model list test some cases.
         */
        get() {
            val list = ArrayList<ColumnHeader>()

            for (i in 0 until COLUMN_SIZE) {
                var title = "column $i"
                val nRandom: Int = Random().nextInt()
                if (nRandom % 4 == 0 || nRandom % 3 == 0 || nRandom == i) {
                    title = "large column $i"
                }

                val header = ColumnHeader(i.toString(), title)
                list.add(header)
            }

            return list
        }

    private val cellListForSortingTest: List<List<Cell>>
        /**
         * This is a dummy model list test some cases.
         */
        get() {
            val list: MutableList<List<Cell>> = ArrayList()
            for (i in 0 until ROW_SIZE) {
                val cellList: MutableList<Cell> = ArrayList()
                for (j in 0 until COLUMN_SIZE) {
                    var text: Any? = "cell $j $i"

                    val random: Int = Random().nextInt()
                    if (j == 0) {
                        text = i
                    } else if (j == 1) {
                        text = random
                    } else if (j == MOOD_COLUMN_INDEX) {
                        text = if (random % 2 == 0) HAPPY else SAD
                    } else if (j == GENDER_COLUMN_INDEX) {
                        text = if (random % 2 == 0) BOY else GIRL
                    }

                    // Create dummy id.
                    val id = "$j-$i"

                    var cell: Cell
                    if (j == 3) {
                        cell = Cell(id, text!!)
                    } else if (j == 4) {
                        // NOTE female and male keywords for filter will have conflict since "female"
                        // contains "male"
                        cell = Cell(id, text!!)
                    } else {
                        cell = Cell(id, text!!)
                    }
                    cellList.add(cell)
                }
                list.add(cellList)
            }

            return list
        }

//    @DrawableRes
//    fun getDrawable(value: Int, isGender: Boolean): Int {
//        return if (isGender) {
//            if (value == BOY) mBoyDrawable else mGirlDrawable
//        } else {
//            if (value == SAD) mSadDrawable else mHappyDrawable
//        }
//    }

    val cellList: List<List<Cell>>
        get() = cellListForSortingTest

    val rowHeaderList: List<RowHeader>
        get() = simpleRowHeaderList

    val columnHeaderList: List<ColumnHeader>
        get() = randomColumnHeaderList

    companion object {
        // Columns indexes
        const val MOOD_COLUMN_INDEX: Int = 3
        const val GENDER_COLUMN_INDEX: Int = 4

        // Constant values for icons
        const val SAD: Int = 1
        const val HAPPY: Int = 2
        const val BOY: Int = 1
        const val GIRL: Int = 2

        // Constant size for dummy data sets
        private const val COLUMN_SIZE = 500
        private const val ROW_SIZE = 500
    }
}