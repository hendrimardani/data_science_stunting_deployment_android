package com.example.stunting.ui.bumil_patient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.evrencoskun.tableview.listener.ITableViewListener
import com.example.stunting.R
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.entities.checks.ChecksRelation
import com.example.stunting.databinding.ActivityBumilPatientBinding
import com.example.stunting.ui.MainActivity
import com.example.stunting.ui.MainActivity.Companion.EXTRA_FRAGMENT_TO_MAIN_ACTIVITY
import com.example.stunting.ui.ViewModelFactory
import com.example.stunting.utils.table_view.TableViewAdapter
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
    private val viewModel by viewModels<BumilPatientViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var userPatientId: Int? = null
    private var categryServiceId: Int? = null
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

        userPatientId = intent?.getIntExtra(EXTRA_USER_PATIENT_ID_TO_BUMIL_PATIENT_ACTIVITY, 0)
        categryServiceId = intent?.getIntExtra(EXTRA_CATEGORY_SERVICE_ID_TO_BUMIL_PATIENT_ACTIVITY, 0)
        getChecksFromApi()
    }

    private fun getChecksRelationByCategoryServiceId(userPatientId: Int, categryServiceId: Int) {
        viewModel.getChecksRelationByUserPatientIdCategoryServiceId(userPatientId, categryServiceId)
            .observe(this) { checksRelationList ->
                Log.d(TAG, "onBumilPatientActivity : ${checksRelationList}")
                initializeTableView(checksRelationList)
        }
    }

    private fun getChecksFromApi() {
        viewModel.getChecksFromApiResult.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {  }
                    is ResultState.Error -> {
//                        Log.d(TAG, "onBumilPatientActivity from LoginFragment getChecksFromApi : ${result.error}")
                    }
                    is ResultState.Success -> {
//                        Log.d(TAG, "onBumilPatientActivity from LoginFragment getChecksFromApi : ${result.data}")
                        getChecksRelationByCategoryServiceId(userPatientId!!, categryServiceId!!)
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun initializeTableView(checksRelationList: List<ChecksRelation>) {
        val people = listOf(
            Person(1, "Alice", 25, "Happy", "Female"),
            Person(2, "Bob", 30, "Sad", "Male"),
            Person(3, "Carol", 28, "Happy", "Female")
        )

        val columnHeaderList = listOf(
            ColumnHeader("namaBumil", "Nama Bumil"),
            ColumnHeader("nikBumil", "NIK Bumil"),
            ColumnHeader("pemeriksa", "Pemeriksa"),
            ColumnHeader("cabang", "Cabang")
        )

        val rowHeaderList = checksRelationList.mapIndexed { index, _ ->
            RowHeader(index.toString(), "${index + 1}")
        }

        val cellList: List<List<Cell>> = checksRelationList.mapIndexed { rowIndex, checksRelation ->
//            val branchEntity = checksRelation.
            val userProfileEntity = checksRelation.userProfileEntity

            listOf(
                Cell("0-$rowIndex", userProfileEntity.id),
                Cell("1-$rowIndex", userProfileEntity.nama),
                Cell("2-$rowIndex", userProfileEntity.branchId),
                Cell("3-$rowIndex", userProfileEntity.alamat)
//                Cell("4-$rowIndex", branchEntity.namaCabang)
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

    companion object {
        private val TAG = BumilPatientActivity::class.java.simpleName
        const val EXTRA_USER_PATIENT_ID_TO_BUMIL_PATIENT_ACTIVITY = "extra_user_patient_id_to_bumil_patient_activity"
        const val EXTRA_CATEGORY_SERVICE_ID_TO_BUMIL_PATIENT_ACTIVITY = "extra_category_service_id_to_bumil_patient_activity"
    }
}