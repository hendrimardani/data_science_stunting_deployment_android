package com.example.stunting.ui.bumil_patient

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.evrencoskun.tableview.listener.ITableViewListener
import com.example.stunting.R
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.entities.checks.ChecksRelation
import com.example.stunting.database.with_api.entities.checks.MonthlyTransactionCount
import com.example.stunting.databinding.ActivityBumilPatientBinding
import com.example.stunting.ui.ContainerMainActivity
import com.example.stunting.ui.ContainerMainActivity.Companion.EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY
import com.example.stunting.ui.ViewModelFactory
import com.example.stunting.utils.NetworkLiveData
import com.example.stunting.utils.table_view.TableViewAdapter
import com.example.stunting.utils.table_view.model.Cell
import com.example.stunting.utils.table_view.model.ColumnHeader
import com.example.stunting.utils.table_view.model.RowHeader
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class BumilPatientActivity : AppCompatActivity() {
    private var _binding: ActivityBumilPatientBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<BumilPatientViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var networkLiveData: NetworkLiveData

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

        isConnected()
        getChecksRelationByUserPatientIdCategoryServiceIdWithSearchBumil(userPatientId!!, categryServiceId!!, "")
        getTransactionCountByMonth()
        setupSearch()
    }

    private fun setupSearch() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                getChecksRelationByUserPatientIdCategoryServiceIdWithSearchBumil(userPatientId!!, categryServiceId!!, s.toString())
            }
        }
        binding.tietCari.addTextChangedListener(textWatcher)
    }

    private fun isConnected() {
        networkLiveData = NetworkLiveData(this.application)
        networkLiveData.observe(this) { isConnected ->
            if (isConnected) {
                getChecksFromApi()
                getChecksRelationByUserPatientIdCategoryServiceIdWithSearchBumil(userPatientId!!, categryServiceId!!, "")
            }
        }
    }

    private fun getTransactionCountByMonth() {
        viewModel.getTransactionCountByMonth().observe(this) { monthlyTransactionCountList ->
            if (monthlyTransactionCountList.isNotEmpty()) {
                displayDataKunjunganInBarChart(monthlyTransactionCountList)
            } else {
                binding.barChart.clear() // Bersihkan chart jika tidak ada data
                binding.barChart.invalidate()
            }
        }
    }

    private fun displayDataKunjunganInBarChart(monthlyTransactionCountList: List<MonthlyTransactionCount>) {
        val barEntries = ArrayList<BarEntry>()
        val xAxisLabels = ArrayList<String>()

        monthlyTransactionCountList.forEachIndexed { index, monthlyCount ->
            barEntries.add(BarEntry(index.toFloat(), monthlyCount.count.toFloat()))
            val monthLabel = mapMonth(monthlyCount.month)
            val yearLabel = "'${monthlyCount.year.takeLast(2)}"
            xAxisLabels.add("$monthLabel $yearLabel")
        }

        val dataSet = BarDataSet(barEntries, "Jumlah Kunjungan")
        dataSet.setColors(resources.getColor(R.color.bluePrimary))
        dataSet.valueTextSize = 10f
        val barData = BarData(dataSet)

        val xAxis = binding.barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
        xAxis.labelRotationAngle = -45f // Miringkan label jika terlalu padat

        binding.barChart.description.isEnabled = false
        binding.barChart.axisRight.isEnabled = false
        binding.barChart.data = barData
        binding.barChart.setFitBars(true)

        binding.barChart.isDragEnabled = true // Aktifkan geser horizontal
        binding.barChart.setVisibleXRangeMaximum(12f) // Tampilkan maksimal 12 bar sekaligus
        binding.barChart.moveViewToX(monthlyTransactionCountList.size.toFloat()) // Mulai dari data paling kanan (terbaru)

        binding.barChart.animateY(1000)
        binding.barChart.invalidate()
    }

    // Fungsi bantuan untuk mengubah "01" menjadi "Jan"
    private fun mapMonth(monthNumber: String): String {
        return when (monthNumber) {
            "01" -> "Jan"
            "02" -> "Feb"
            "03" -> "Mar"
            "04" -> "Apr"
            "05" -> "Mei"
            "06" -> "Jun"
            "07" -> "Jul"
            "08" -> "Agu"
            "09" -> "Sep"
            "10" -> "Okt"
            "11" -> "Nov"
            "12" -> "Des"
            else -> ""
        }
    }

    private fun getChecksRelationByUserPatientIdCategoryServiceIdWithSearchBumil(
        userPatientId: Int, categryServiceId: Int, querySearch: String
    ) {
        viewModel.getChecksRelationByUserPatientIdCategoryServiceIdWithSearchBumil(
            userPatientId, categryServiceId, querySearch).observe(this) { checksRelationList ->
                if (checksRelationList.isNotEmpty()) {
                    binding.tableView.visibility = View.VISIBLE
                    binding.lavNoResultData.visibility = View.GONE
                    setupTableView(checksRelationList)
                } else {
                    binding.tableView.visibility = View.GONE
                    binding.lavNoResultData.visibility = View.VISIBLE
                }
        }
    }

    private fun getChecksFromApi() {
        val progressBar = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        viewModel.getChecksFromApiResult.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> progressBar.show()
                    is ResultState.Error -> {
                        progressBar.dismiss()
//                        Log.d(TAG, "onBumilPatientActivity from LoginFragment getChecksFromApi : ${result.error}")
                    }
                    is ResultState.Success -> {
                        viewModel.getPregnantMomServiceFromApi()
                        progressBar.dismiss()
//                        Log.d(TAG, "onBumilPatientActivity from LoginFragment getChecksFromApi : ${result.data}")
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(this, ContainerMainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun setupTableView(checksRelationList: List<ChecksRelation>) {
        val columnHeaderList = listOf(
            ColumnHeader("namaBumil", "Nama Bumil"),
            ColumnHeader("nikBumil", "NIK Bumil"),
            ColumnHeader("statusGiziKesehatan", "Status Gizi Kesehatan"),
            ColumnHeader("pemeriksa", "Pemeriksa"),
            ColumnHeader("cabang", "Cabang"),
            ColumnHeader("catatan", "Catatan"),
            ColumnHeader("tglPemeriksaan", "Tanggal Pemeriksaan")
        )

        val rowHeaderList = checksRelationList.mapIndexed { index, _ ->
            RowHeader(index.toString(), "${index + 1}")
        }

        val cellList: List<List<Cell>> = checksRelationList.mapIndexed { rowIndex, checksRelation ->
            val branchEntity = checksRelation.branchEntity
            val userProfileEntity = checksRelation.userProfileEntity
            val userProfilePatientEntity = checksRelation.userProfilePatientEntity
            val pregnantMomServiceEntity = checksRelation.pregnantMomServiceEntity
            val checksEntity = checksRelation.checksEntity

            listOf(
                Cell(rowIndex.toString(), userProfilePatientEntity.namaBumil),
                Cell(rowIndex.toString(), userProfilePatientEntity.nikBumil),
                Cell(rowIndex.toString(), pregnantMomServiceEntity?.statusGiziKesehatan),
                Cell(rowIndex.toString(), userProfileEntity.nama),
                Cell(rowIndex.toString(), branchEntity.namaCabang),
                Cell(rowIndex.toString(), checksEntity.catatan),
                Cell(rowIndex.toString(), checksEntity.tglPemeriksaan)
            )
        }

        binding.tableView.setAdapter(tableViewAdapter)

        binding.tableView.setTableViewListener(object : ITableViewListener {
            override fun onCellClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {
                (binding.tableView.adapter as? TableViewAdapter)?.setSelectedPosition(column, row)
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