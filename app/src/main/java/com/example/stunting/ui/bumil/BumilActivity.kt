package com.example.stunting.ui.bumil

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.stunting.database.no_api.bumil.BumilDao
import com.example.stunting.database.no_api.bumil.BumilEntity
import com.example.stunting.database.no_api.DatabaseApp
import com.example.stunting.databinding.ActivityBumilBinding
import com.example.stunting.databinding.DialogBottomSheetAllBinding
import com.example.stunting.databinding.DialogCustomDeleteBinding
import com.example.stunting.databinding.DialogCustomExportDataBinding
import com.example.stunting.utils.Functions.calculateAge
import com.example.stunting.utils.Functions.getDatePickerDialogTglLahir
import com.example.stunting.utils.Functions.getDateTimePrimaryKey
import com.example.stunting.utils.Functions.linkToDirectory
import com.example.stunting.utils.Functions.setCalendarTglLahir
import com.example.stunting.utils.Functions.showCustomeInfoDialog
import com.example.stunting.utils.Functions.toastInfo
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToastStyle
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

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
        setToolBar()
        collapsedHandlerToolbar()

        _bindingBumilBottomSheetDialog = DialogBottomSheetAllBinding.inflate(layoutInflater)
        bindingBumilBottomSheetDialog.tvListData.text = getString(R.string.list_data_bumil)

        _bumilDao = (application as DatabaseApp).dbApp.bumilDao()

        setCalendarTglLahir(binding.etTglLahirBumil)
        setCalendarHariPertamaHaidTerakhir(binding.etTglHariPertamaHaidTerakhirBumil)
        setCalendarTglPerkiraanLahir(binding.etTglPerkiraanLahirBumil)

        getRadioButtonValue(R.id.rg_bumil)

        binding.etTglLahirBumil.setOnClickListener(this)
        binding.etTglHariPertamaHaidTerakhirBumil.setOnClickListener(this)
        binding.etTglPerkiraanLahirBumil.setOnClickListener(this)
        binding.rgBumil.setOnClickListener(this)
        binding.btnSubmitBumil.setOnClickListener(this)
        binding.btnTampilDataBumil.setOnClickListener(this)

        getAll(bumilDao)
        setTglLahirBumil()
        setInputTextTanggalPerkiraanLahir()
        setInputTextUmurKehamilan()
    }

    private fun setInputTextUmurKehamilan() {
        // Ketika tiap sentuh inputText, inputText umur kehamilan akan terupdate
        binding.etTglHariPertamaHaidTerakhirBumil.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                val hpht = s.toString()
                // Log format untuk debugging
//                Log.e("TEST FORMAT", hpht)

                // Pastikan hpht tidak kosong sebelum mencoba menghitung umur
                if (hpht.isNotEmpty()) {
                    val umurKehamilan = calculateGestationalAgeInMonths(hpht)
                    binding.etUmurKehamilanBumil.setText(umurKehamilan)
                } else {
                    // Clear umur kehamilan jika input tanggal kosong
                    binding.etUmurKehamilanBumil.setText("")
                }
            }
        })
    }

    private fun calculateGestationalAgeInMonths(hpht: String): String {
        // Format tanggal input
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val firstDayOfLastPeriod = LocalDate.parse(hpht, formatter)

        // Tanggal hari ini
        val today = LocalDate.now()

        // Hitung selisih hari
        val period = Period.between(firstDayOfLastPeriod, today)
//        Log.e("TEST RESULT PERIOD ", period.toString())

        val totalDays = period.days + period.months * 30 + period.years * 365

        // Hitung minggu kehamilan
        val weeksPregnant = totalDays / 7
        val monthsPregnant = weeksPregnant / 4 // 1 bulan kehamilan = 4 minggu

        return "$monthsPregnant bulan (${weeksPregnant} minggu)"
    }

    private fun setInputTextTanggalPerkiraanLahir() {
        // Ketika tiap sentuh inputText, inputText tgl perkiraan lahir akan terupdate
        binding.etTglHariPertamaHaidTerakhirBumil.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                val hpht = s.toString()
                // Log format untuk debugging
//                Log.e("TEST FORMAT", hpht)

                // Pastikan hpht tidak kosong sebelum mencoba menghitung umur
                if (hpht.isNotEmpty()) {
                    val umur = calculateTanggalPerkiraanLahir(hpht)
                    binding.etTglPerkiraanLahirBumil.setText(umur)
                } else {
                    // Clear tgl perkiraan lahir jika input tanggal kosong
                    binding.etTglPerkiraanLahirBumil.setText("")
                }
            }
        })
    }

    private fun calculateTanggalPerkiraanLahir(hpht: String): String {
        // Format tanggal input
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val firstDayOfLastPeriod = LocalDate.parse(hpht, formatter)

        // Tambahkan 7 hari, kurangi 3 bulan, dan tambahkan 1 tahun jika diperlukan
        val dueDate = firstDayOfLastPeriod.plusDays(7).minusMonths(3).let {
            if (it.isBefore(firstDayOfLastPeriod)) it.plusYears(1) else it
        }
        return dueDate.format(formatter)
    }


    private fun setTglLahirBumil() {
        binding.etTglLahirBumil.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                val tglLahir = s.toString()
//                Log.e("TEST FORMAT", tglLahir)
                if (tglLahir.isNotEmpty()) {
                    val umur = calculateAge(tglLahir)
                    binding.etUmurBumil.setText(umur)
                } else {
                    binding.etUmurBumil.setText("")
                }
            }
        })
    }

    private fun collapsedHandlerToolbar() {
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            if (abs(verticalOffset.toDouble()) >= totalScrollRange) {
                // Jika CollapsingToolbarLayout sudah collapsed, tampilkan judul
                binding.collapsingToolbarLayout.title = getString(R.string.app_name_layanan_bumil)
            } else {
                // Jika masih expanded, sembunyikan judul
                binding.collapsingToolbarLayout.title = ""
            }
        }
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
        supportActionBar!!.title = getString(R.string.app_name_layanan_bumil)
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
            R.id.et_tgl_hari_pertama_haid_terakhir_bumil -> getDatePickerDialogHariPertamaHaidTerakhir()
            R.id.et_tgl_perkiraan_lahir_bumil -> getDatePickerDialogTglPerkiraanLahir()
            R.id.btn_submit_bumil -> {
                val nama = binding.etNamaBumil.text.toString()
                val nik = binding.etNikBumil.text.toString()
                val tglLahir = binding.etTglLahirBumil.text.toString()
                val umur = binding.etUmurBumil.text.toString()
                val hariPertamaHaidTerakhir = binding.etTglHariPertamaHaidTerakhirBumil.text.toString()
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
        binding.etTglHariPertamaHaidTerakhirBumil.text!!.clear()
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
        exportDialog.setCanceledOnTouchOutside(false)
        exportDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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
                listOf("tanggal", "Nama Bumil", "Tgl Lahir Bumil", "Umur Bumil (tahun)",
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
        deleteDialog.setCanceledOnTouchOutside(false)
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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