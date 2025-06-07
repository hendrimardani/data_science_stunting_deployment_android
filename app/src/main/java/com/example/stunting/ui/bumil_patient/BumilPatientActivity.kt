package com.example.stunting.ui.bumil_patient

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.evrencoskun.tableview.listener.ITableViewListener
import com.example.stunting.R
import com.example.stunting.databinding.ActivityBumilPatientBinding
import com.example.stunting.utils.table_view.TableViewAdapter
import com.example.stunting.utils.table_view.TableViewModel
import com.example.stunting.utils.table_view.holder.CellViewHolder
import com.example.stunting.utils.table_view.model.Cell
import com.example.stunting.utils.table_view.model.ColumnHeader
import com.example.stunting.utils.table_view.model.RowHeader

data class Person(
    val id: Int,
    val name: String,
    val age: Int,
    val mood: String,
    val gender: String
)


class BumilPatientActivity : AppCompatActivity() {
    private var _binding: ActivityBumilPatientBinding? = null
    private val binding get() = _binding!!

    private var tableViewModel = TableViewModel()
    private var tableViewAdapter = TableViewAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityBumilPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeTableView()
    }

    private fun initializeTableView() {
        val people = listOf(
            Person(1, "Alice", 25, "Happy", "Female"),
            Person(2, "Bob", 30, "Sad", "Male"),
            Person(3, "Carol", 28, "Happy", "Female")
        )

        val columnHeaderList = listOf(
            ColumnHeader("id", "ID"),
            ColumnHeader("name", "Name"),
            ColumnHeader("age", "Age"),
            ColumnHeader("mood", "Mood"),
            ColumnHeader("gender", "Gender")
        )

        val rowHeaderList = people.mapIndexed { index, person ->
            RowHeader(index.toString(), "${index + 1}")
        }

        val cellList: List<List<Cell>> = people.mapIndexed { rowIndex, person ->
            listOf(
                Cell("0-$rowIndex", person.id),
                Cell("1-$rowIndex", person.name),
                Cell("2-$rowIndex", person.age),
                Cell("3-$rowIndex", person.mood),
                Cell("4-$rowIndex", person.gender)
            )
        }

        binding.tableview.setAdapter(tableViewAdapter)

        binding.tableview.setTableViewListener(object : ITableViewListener {
            override fun onCellClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {
                (binding.tableview.adapter as? TableViewAdapter)?.setSelectedPosition(column, row)

            }

            override fun onCellDoubleClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) { }

            override fun onCellLongPressed(cellView: RecyclerView.ViewHolder, column: Int, row: Int) { }

            override fun onColumnHeaderClicked(columnHeaderView: RecyclerView.ViewHolder, column: Int) { }

            override fun onColumnHeaderDoubleClicked(columnHeaderView: RecyclerView.ViewHolder, column: Int) { }

            override fun onColumnHeaderLongPressed(columnHeaderView: RecyclerView.ViewHolder, column: Int) { }

            override fun onRowHeaderClicked(rowHeaderView: RecyclerView.ViewHolder, row: Int) { }

            override fun onRowHeaderDoubleClicked(rowHeaderView: RecyclerView.ViewHolder, row: Int) { }

            override fun onRowHeaderLongPressed(rowHeaderView: RecyclerView.ViewHolder, row: Int) { }
        })
        tableViewAdapter.setAllItems(columnHeaderList, rowHeaderList, cellList)
    }
}