package com.example.stunting.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stunting.R
import com.example.stunting.adapter.BumilAdapter
import com.example.stunting.database.bumil.BumilDao
import com.example.stunting.database.bumil.BumilEntity
import com.example.stunting.database.DatabaseApp
import com.example.stunting.databinding.ActivityBumilBinding
import com.example.stunting.databinding.DialogBottomSheetAllBinding
import com.example.stunting.databinding.DialogCustomDeleteBinding
import com.example.stunting.databinding.DialogCustomExportDataBinding
import com.example.stunting.functions_helper.Functions.getDatePickerDialogTglLahir
import com.example.stunting.functions_helper.Functions.getDateTimePrimaryKey
import com.example.stunting.functions_helper.Functions.linkToDirectory
import com.example.stunting.functions_helper.Functions.setCalendarTglLahir
import com.example.stunting.functions_helper.Functions.showCustomeInfoDialog
import com.example.stunting.functions_helper.Functions.toastInfo
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToastStyle
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BumilActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityBumilBinding? = null
    private val binding get() = _binding!!
    private var _bumilDao: BumilDao? = null
    private val bumilDao get() = _bumilDao!!
    private var _bindingBumilBottomSheetDialog: DialogBottomSheetAllBinding? = null
    private val bindingBumilBottomSheetDialog get() = _bindingBumilBottomSheetDialog!!
    private var _cal: Calendar? = null
    private val cal get() = _cal!!
    private var _dataSetListenerHariPertamaHaidTerakhir: DatePickerDialog.OnDateSetListener? = null
    private val dataSetListenerHariPertamaHaidTerakhir get() = _dataSetListenerHariPertamaHaidTerakhir!!
    private var _dataSetListenerTglPerkiraanLahir: DatePickerDialog.OnDateSetListener? = null
    private val dataSetListenerTglPerkiraanLahir get() = _dataSetListenerTglPerkiraanLahir!!

    var countItem = 0
    var statusGiziRadioButton = "YA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityBumilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Toolbar
        setToolBar()

        // Binding BumilBottomSheetDialog for retrieve xml id
        _bindingBumilBottomSheetDialog = DialogBottomSheetAllBinding.inflate(layoutInflater)
        bindingBumilBottomSheetDialog.tvListData.text = getString(R.string.list_data_bumil)

        // Call database
        _bumilDao = (application as DatabaseApp).dbBumilDatabase.bumilDao()

        // Set caledar and update in view result
        setCalendarTglLahir(binding.etTglLahirBumil)
        setCalendarHariPertamaHaidTerakhir(binding.etHariPertamaHaidTerakhirBumil)
        setCalendarTglPerkiraanLahir(binding.etTglPerkiraanLahirBumil)

        // getRadioButtomValue
        getRadioButtonValue(R.id.rg_bumil)

        binding.etTglLahirBumil.setOnClickListener(this)
        binding.etHariPertamaHaidTerakhirBumil.setOnClickListener(this)
        binding.etTglPerkiraanLahirBumil.setOnClickListener(this)
        binding.rgBumil.setOnClickListener(this)
        binding.btnSubmitBumil.setOnClickListener(this)
        binding.btnTampilDataBumil.setOnClickListener(this)

        // Get all items
        getAll(bumilDao)
    }

    private fun getRadioButtonValue(bindingRadioGroup: Int) {
        val radioGroup = findViewById<RadioGroup>(bindingRadioGroup)
        // Get all the RadioButtons within the RadioGroup
        val radioButtons = radioGroup.childCount

        // Set a listener for each RadioButton
        for (i in 0 until radioButtons) {
            val radioButton = radioGroup.getChildAt(i) as RadioButton
            radioButton.setOnCheckedChangeListener { button, isChecked ->
                if (isChecked) {
                    // Handle the selected RadioButton
                    val selectedRadioButtonText = button.text.toString()
                    if (selectedRadioButtonText == "YA") {
                        statusGiziRadioButton = selectedRadioButtonText
                    } else {
                        statusGiziRadioButton = selectedRadioButtonText
                    }
//                    Log.e("Selected RadioButton:", selectedRadioButtonText)
                }
            }
        }
    }

    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbBumil)
        supportActionBar!!.title = getString(R.string.app_name_ibu_hamil)
        // Change font style text
        binding.tbBumil.setTitleTextAppearance(this, R.style.Theme_Stunting)

        // Enable back button if you're in a child activity
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.tbBumil.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_tgl_lahir_bumil -> getDatePickerDialogTglLahir(this@BumilActivity)
            R.id.et_hari_pertama_haid_terakhir_bumil -> getDatePickerDialogHariPertamaHaidTerakhir()
            R.id.et_tgl_perkiraan_lahir_bumil -> getDatePickerDialogTglPerkiraanLahir()
            R.id.btn_submit_bumil -> {
                val nama = binding.etNamaBumil.text.toString()
                val nik = binding.etNikBumil.text.toString()
                val tglLahir = binding.etTglLahirBumil.text.toString()
                val umur = binding.etUmurBumil.text.toString()
                val hariPertamaHaidTerakhir = binding.etHariPertamaHaidTerakhirBumil.text.toString()
                val tanggalPerkiraanLahir = binding.etTglPerkiraanLahirBumil.text.toString()
                val umurKehamilan = binding.etUmurKehamilanBumil.text.toString()
                val statusGiziKesehatan = statusGiziRadioButton

                if (nama.isNotEmpty() && nik.isNotEmpty() && tglLahir.isNotEmpty() &&
                    umur.isNotEmpty() && hariPertamaHaidTerakhir.isNotEmpty() && tanggalPerkiraanLahir.isNotEmpty() &&
                    umurKehamilan.isNotEmpty() && statusGiziKesehatan.isNotEmpty()) {
                    addRecord(bumilDao, nama, nik, tglLahir, umur, hariPertamaHaidTerakhir, tanggalPerkiraanLahir, umurKehamilan, statusGiziKesehatan)
                } else {
                    toastInfo(
                        this@BumilActivity, getString(R.string.title_input_failed),
                        getString(R.string.description_input_failed), MotionToastStyle.ERROR
                    )
                }
            }
            R.id.btn_tampil_data_bumil -> {
                // Data not empty
//                Log.e("CEK DATANA", countItem.toString())
                if (countItem != 0) showBottomSheetDialog() else
                    toastInfo(
                        this@BumilActivity, getString(R.string.title_show_data_failed),
                        getString(R.string.description_show_data_failed), MotionToastStyle.ERROR
                    )
            }
        }
    }

    private fun setupListOfDataIntoRecyclerView(bumilList: ArrayList<BumilEntity>) {
        if (bumilList.isNotEmpty()) {
            val allAdapter = BumilAdapter(bumilList)
            // Count item list
            countItem = bumilList.size
            bindingBumilBottomSheetDialog.tvTotalData.text = getString(R.string.setup_recycler_view_sum_data, countItem.toString())
            bindingBumilBottomSheetDialog.rvBottomSheet.layoutManager = LinearLayoutManager(this)
            bindingBumilBottomSheetDialog.rvBottomSheet.adapter = allAdapter
            // To scrolling automatic when data entered
            bindingBumilBottomSheetDialog.rvBottomSheet.smoothScrollToPosition(countItem - 1)

            // When input data automatically to last index
            bindingBumilBottomSheetDialog.rvBottomSheet
                .layoutManager!!.smoothScrollToPosition(bindingBumilBottomSheetDialog
                    .rvBottomSheet, null, countItem - 1)
        }
//        Log.e("HASILNA", bumilList.toString())
    }

    private fun getAll(bumilDao: BumilDao) {
        lifecycleScope.launch {
            bumilDao.fetchAllBumil().collect {
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list)
            }
        }
    }

    private fun addRecord(bumilDao: BumilDao, nama: String, nik: String, tglLahir: String,
                          umur: String, hariPertamaHaidTerakhir: String, tanggalPerkiraanLahir: String,
                          umurKehamilan: String, statusGiziKesehatan: String) {

        val date = getDateTimePrimaryKey()

        lifecycleScope.launch {
            bumilDao.insert(
                BumilEntity(
                    tanggal = date, namaBumil = nama, nikBumil = nik, tglLahirBumil = tglLahir,
                    umurBumil = umur, hariPertamaHaidTerakhirBumil = hariPertamaHaidTerakhir,
                    tanggalPerkiraanLahirBumil = tanggalPerkiraanLahir, umurKehamilanBumil = umurKehamilan,
                    statusGiziKesehatanBumil = statusGiziKesehatan
                )
            )
        }
        toastInfo(
            this@BumilActivity, getString(R.string.title_saved_data),
            getString(R.string.description_saved_data), MotionToastStyle.SUCCESS
        )

        // Clear the text when data saved !!! (success)
        binding.etNamaBumil.text!!.clear()
        binding.etNikBumil.text!!.clear()
        binding.etTglLahirBumil.text!!.clear()
        binding.etUmurBumil.text!!.clear()
        binding.etHariPertamaHaidTerakhirBumil.text!!.clear()
        binding.etTglPerkiraanLahirBumil.text!!.clear()
        binding.etUmurKehamilanBumil.text!!.clear()
    }

    private fun setCalendarTglPerkiraanLahir(etTanggal: EditText) {
        _cal = Calendar.getInstance()
        _dataSetListenerTglPerkiraanLahir = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInViewTglPerkiraanLahir(etTanggal)
        }
        updateDateInViewTglPerkiraanLahir(etTanggal)
    }

    fun updateDateInViewTglPerkiraanLahir(etTanggal: EditText) {
        val myFormat = "yyyy/MM/dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        etTanggal.setText(sdf.format(cal.time).toString())
    }

    private fun setCalendarHariPertamaHaidTerakhir(etTanggal: EditText) {
        _cal = Calendar.getInstance()
        _dataSetListenerHariPertamaHaidTerakhir = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInViewHariPertamaHaidTerakhir(etTanggal)
        }
        updateDateInViewHariPertamaHaidTerakhir(etTanggal)
    }

    private fun updateDateInViewHariPertamaHaidTerakhir(etTanggal: EditText) {
        val myFormat = "yyyy/MM/dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        etTanggal.setText(sdf.format(cal.time).toString())
    }

    private fun getDatePickerDialogTglPerkiraanLahir() {
        DatePickerDialog(this@BumilActivity, dataSetListenerTglPerkiraanLahir, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun getDatePickerDialogHariPertamaHaidTerakhir() {
        DatePickerDialog(this@BumilActivity, dataSetListenerHariPertamaHaidTerakhir, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showBottomSheetDialog() {
        // Check if the view already has a parent
        val viewBottomSheetDialog: View = bindingBumilBottomSheetDialog.root

        if (viewBottomSheetDialog.parent != null) {
            val parentViewGroup = viewBottomSheetDialog.parent as ViewGroup
            parentViewGroup.removeView(viewBottomSheetDialog)
        }
        // Now set the view as content for the BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(viewBottomSheetDialog)
        bottomSheetDialog.show()

        bindingBumilBottomSheetDialog.apply {
            ivDelete.setOnClickListener { showCustomeDeleteDialog() }
            ivExportToXlsx.setOnClickListener { showCustomeExportDataDialog() }
            ivArrow.setOnClickListener { showCustomeInfoDialog(this@BumilActivity, layoutInflater) }
            tvInfo.setOnClickListener { showCustomeInfoDialog(this@BumilActivity, layoutInflater) }
        }
    }

    private fun deleteAllDates(bumilDao: BumilDao) {
        lifecycleScope.launch {
            bumilDao.deleteAll()
        }
        toastInfo(
            this@BumilActivity, getString(R.string.title_history_delete),
            getString(R.string.description_history_delete), MotionToastStyle.SUCCESS
        )
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun showCustomeExportDataDialog() {
        val viewExport = DialogCustomExportDataBinding.inflate(layoutInflater)
        val exportDialog = Dialog(this)
        exportDialog.setContentView(viewExport.root)

        val result = "$NAME(tahunbulantanggal_jammenitdetik)"
        val simpleDateFormat = SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())
        val resultName = "$NAME${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())}"
        viewExport.tvDescription.text = getString(R.string.description_export_dialog, simpleDateFormat, result, resultName)

        viewExport.tvYes.setOnClickListener {
            // Export to CSV
            lifecycleScope.launch {
                toastInfo(
                    this@BumilActivity, getString(R.string.title_export_success),
                    getString(R.string.description_export_success), MotionToastStyle.SUCCESS
                )
                exportDatabaseToCSV(bumilDao)
            }
            exportDialog.dismiss()
            // Goto link directory download
            linkToDirectory(this@BumilActivity)
            // Destroying when exported successfully
            finish()
        }
        viewExport.tvNo.setOnClickListener {
            exportDialog.dismiss()
        }
        exportDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private suspend fun exportDatabaseToCSV(bumilDao: BumilDao) {
        // Output variabel fileDir is => /storage/emulated/0/Android/data/com.example.stunting/files/Download
        // val fileDir = "${getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/"

        var dataResult: ArrayList<List<String>> = ArrayList()
        val fileName = "$NAME${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())}.csv"
        val fileDir = "/storage/emulated/0/Download/"
        try {
            val file = File(fileDir, fileName)
            dataResult.add(
                listOf("tanggal", "Nama Bumil", "Tgl Lahir Bumil", "Umur Bumil",
                    "Hari Pertama Haid Terakhir", "Tanggal Perkiraan Lahir Bumil", "Umur Kehamilan Bumil", "Status Gizi Kesehatan (YA/TIDAK)")
            )
            bumilDao.fetchAllBumil().collect {
                for (item in it) {
                    dataResult.add(
                        listOf(
                            item.tanggal, item.namaBumil, item.tglLahirBumil,
                            item.umurBumil, item.hariPertamaHaidTerakhirBumil,
                            item.tanggalPerkiraanLahirBumil, item.umurKehamilanBumil, item.statusGiziKesehatanBumil
                        )
                    )
                    csvWriter().writeAll(dataResult, file.outputStream())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            toastInfo(
                this@BumilActivity, getString(R.string.title_export_failed),
                getString(R.string.description_export_dialog), MotionToastStyle.ERROR
            )
        }
    }

    private fun showCustomeDeleteDialog() {
        val viewDelete = DialogCustomDeleteBinding.inflate(layoutInflater)
        val deleteDialog = Dialog(this)
        deleteDialog.setContentView(viewDelete.root)

        viewDelete.tvDescription.text = getString(R.string.description_delete_dialog)
        viewDelete.tvYes.setOnClickListener {
            deleteAllDates(bumilDao)
            // We will destroy activity
            finish()
            deleteDialog.dismiss()
        }
        viewDelete.tvNo.setOnClickListener {
            deleteDialog.dismiss()
        }
        deleteDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _cal = null
        _bumilDao = null
        _bindingBumilBottomSheetDialog = null
        _dataSetListenerTglPerkiraanLahir = null
        _dataSetListenerHariPertamaHaidTerakhir = null
    }

    companion object {
        private const val NAME = "bumil_data_"
    }
}