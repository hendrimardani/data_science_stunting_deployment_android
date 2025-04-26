package com.example.stunting.ui.calon_pengantin

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.example.stunting.adapter.CalonPengantinAdapter
import com.example.stunting.database.no_api.calon_pengantin.CalonPengantinDao
import com.example.stunting.database.no_api.calon_pengantin.CalonPengantinEntity
import com.example.stunting.database.no_api.DatabaseApp
import com.example.stunting.databinding.ActivityCalonPengantinBinding
import com.example.stunting.databinding.DialogBottomSheetAllBinding
import com.example.stunting.databinding.DialogCustomDeleteBinding
import com.example.stunting.databinding.DialogCustomExportDataBinding
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

class CalonPengantinActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityCalonPengantinBinding? = null
    private val binding get() = _binding!!
    private var _bindingCalonPengantinBottomSheetDialog: DialogBottomSheetAllBinding? = null
    private val bindingCalonPengantinBottomSheetDialog get() = _bindingCalonPengantinBottomSheetDialog!!
    private var _calonPengantinDao: CalonPengantinDao? = null
    private val calonPengantinDao get() = _calonPengantinDao!!
    private var _cal: Calendar? = null
    private val cal get() = _cal!!
    private var _dataSetListenerPerkiraanTglPernikahan: DatePickerDialog.OnDateSetListener? = null
    private val dataSetListenerPerkiraanTglPernikahan get() = _dataSetListenerPerkiraanTglPernikahan!!

    var countItem = 0
    var periksaKesehatanRadioButton = "YA"
    var bimbinganPerkawinanRadioButton = "YA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityCalonPengantinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Toolbar
        setToolBar()

        // Collapsed Toolbar
        collapsedHandlerToolbar()

        // Binding BumilBottomSheetDialog for retrieve xml id
        _bindingCalonPengantinBottomSheetDialog = DialogBottomSheetAllBinding.inflate(layoutInflater)
        bindingCalonPengantinBottomSheetDialog.tvListData.text = getString(R.string.list_data_catin)

        // Call database
        _calonPengantinDao = (application as DatabaseApp).dbApp.calonPengantinDao()

        // Set caledar and update in view result
        setCalendarTglLahir(binding.etTglLahirCalonPengantin)
        setCalendarPerkiraanTanggalPernikahan(binding.etPerkiraanTanggalPernikahanCalonPengantin)

        getRadioButtonValue(R.id.rg_periksa_kesehatan_calon_pengantin, PERIKSA_KESEHATAN)
        getRadioButtonValue(R.id.rg_bimbingan_perkawinan_calon_pengantin, BIMBINGAN_PERKAWINAN)

        binding.etTglLahirCalonPengantin.setOnClickListener(this)
        binding.etPerkiraanTanggalPernikahanCalonPengantin.setOnClickListener(this)

        binding.btnSubmitCalonPengantin.setOnClickListener(this)
        binding.btnTampilDataCalonPengantin.setOnClickListener(this)

        // Get all items
        getAll(calonPengantinDao)

        // Set inputText umur from calculate tgl lahir
        setInputTextUmur()
    }

    private fun setInputTextUmur() {
        // Ketika tiap sentuh inputText, inputText umur akan terupdate
        binding.etTglLahirCalonPengantin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                val tglLahir = s.toString()
                // Log format untuk debugging
//                Log.e("TEST FORMAT", tglLahir)

                // Pastikan tglLahir tidak kosong sebelum mencoba menghitung umur
                if (tglLahir.isNotEmpty()) {
                    val umur = calculateAge(tglLahir)
                    binding.etUmurCalonPengantin.setText(umur)
                } else {
                    // Clear tglLahir jika input tanggal kosong
                    binding.etUmurCalonPengantin.setText("")
                }
            }
        })
    }

    private fun calculateAge(birthDateString: String): String {
        // Format tanggal (ubah format jika perlu)
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val birthDate = LocalDate.parse(birthDateString, formatter)

        // Tanggal hari ini
        val today = LocalDate.now()

        // Hitung umur
        val period = Period.between(birthDate, today)
//        Log.e("TEST RESULT PERIOD ", period.toString())

        return "${period.years} tahun, ${period.months} bulan"
    }

    private fun collapsedHandlerToolbar() {
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            if (abs(verticalOffset.toDouble()) >= totalScrollRange) {
                // Jika CollapsingToolbarLayout sudah collapsed, tampilkan judul
                binding.collapsingToolbarLayout.title = getString(R.string.app_name_layanan_catin)
            } else {
                // Jika masih expanded, sembunyikan judul
                binding.collapsingToolbarLayout.title = ""
            }
        }
    }

    private fun getAll(calonPengantinDao: CalonPengantinDao) {
        lifecycleScope.launch {
            calonPengantinDao.fetchAllCalonPengantin().collect {
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list)
            }
        }
    }

    private fun setupListOfDataIntoRecyclerView(calonPengantinList: ArrayList<CalonPengantinEntity>) {

        if (calonPengantinList.isNotEmpty()) {
            val calonPengantinAdapter = CalonPengantinAdapter(calonPengantinList)
            // Count item list
            countItem = calonPengantinList.size
            bindingCalonPengantinBottomSheetDialog.tvTotalData.text = getString(R.string.setup_recycler_view_sum_data, countItem.toString())
            bindingCalonPengantinBottomSheetDialog.rvBottomSheet.layoutManager = LinearLayoutManager(this)
            bindingCalonPengantinBottomSheetDialog.rvBottomSheet.adapter = calonPengantinAdapter
            // To scrolling automatic when data entered
            bindingCalonPengantinBottomSheetDialog.rvBottomSheet.smoothScrollToPosition(countItem - 1)

            // When input data automatically to last index
            bindingCalonPengantinBottomSheetDialog.rvBottomSheet
                .layoutManager!!.smoothScrollToPosition(bindingCalonPengantinBottomSheetDialog
                    .rvBottomSheet, null, countItem - 1)
        }
//        Log.e("HASILNA", calonPengantinList.toString())
    }

    private fun showBottomSheetDialog() {
        // Check if the view already has a parent
        val viewBottomSheetDialog: View = bindingCalonPengantinBottomSheetDialog.root

        if (viewBottomSheetDialog.parent != null) {
            val parentViewGroup = viewBottomSheetDialog.parent as ViewGroup
            parentViewGroup.removeView(viewBottomSheetDialog)
        }
        // Now set the view as content for the BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(viewBottomSheetDialog)
        bottomSheetDialog.show()

        bindingCalonPengantinBottomSheetDialog.apply {
            ivDelete.setOnClickListener { showCustomeDeleteDialog() }
            ivExportToXlsx.setOnClickListener { showCustomeExportDataDialog() }
            ivArrow.setOnClickListener { showCustomeInfoDialog(this@CalonPengantinActivity, layoutInflater) }
            tvInfo.setOnClickListener { showCustomeInfoDialog(this@CalonPengantinActivity, layoutInflater) }
        }
    }

    @SuppressLint("SimpleDateFormat")
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
                    this@CalonPengantinActivity, getString(R.string.title_export_success),
                    getString(R.string.description_export_success), MotionToastStyle.SUCCESS
                )
                exportDatabaseToCSV(calonPengantinDao)
            }
            exportDialog.dismiss()
            // Goto link directory download
            linkToDirectory(this@CalonPengantinActivity)
            // Destroying when exported successfully
            finish()
        }
        viewExport.tvNo.setOnClickListener {
            exportDialog.dismiss()
        }
        exportDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private suspend fun exportDatabaseToCSV(calonPengantinDao: CalonPengantinDao) {
        // Output variabel fileDir is => /storage/emulated/0/Android/data/com.example.stunting/files/Download
        // val fileDir = "${getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/"

        var dataResult: ArrayList<List<String>> = ArrayList()
        val fileName = "$NAME${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())}.csv"
        val fileDir = "/storage/emulated/0/Download/"
        try {
            val file = File(fileDir, fileName)
            dataResult.add(
                listOf("tanggal", "Nama Calon Pengantin", "NIK", "Tanggal Lahir",
                    "Umur", "Perkiraan Tanggal Pernikahan", "Periksa Kesehatan (YA/TIDAK)", "Bimbingan Perkawinan (YA/TIDAK)")
            )
            calonPengantinDao.fetchAllCalonPengantin().collect {
                for (item in it) {
                    dataResult.add(
                        listOf(
                            item.tanggal, item.namaCalonPengantin, item.nikCalonPengantin,
                            item.tglLahirCalonPengantin, item.umurCalonPengantin, item.perkiraanTanggalPernikahanCalonPengantin,
                            item.periksaKesehatanCalonPengantin, item.bimbinganPerkawinanCalonPengantin
                        )
                    )
                    csvWriter().writeAll(dataResult, file.outputStream())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            toastInfo(
                this@CalonPengantinActivity, getString(R.string.title_export_failed),
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
            deleteAllDates(calonPengantinDao)
            // We will destroy activity
            finish()
            deleteDialog.dismiss()
        }
        viewDelete.tvNo.setOnClickListener {
            deleteDialog.dismiss()
        }
        deleteDialog.show()
    }

    private fun deleteAllDates(calonPengantinDao: CalonPengantinDao) {
        lifecycleScope.launch {
            calonPengantinDao.deleteAll()
        }
        toastInfo(
            this@CalonPengantinActivity, getString(R.string.title_history_delete),
            getString(R.string.description_history_delete), MotionToastStyle.SUCCESS
        )
    }

    private fun setCalendarPerkiraanTanggalPernikahan(etTanggal: EditText) {
        _cal = Calendar.getInstance()
        _dataSetListenerPerkiraanTglPernikahan = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInViewPerkiraanTanggalPernikahan(etTanggal)
        }
        updateDateInViewPerkiraanTanggalPernikahan(etTanggal)
    }

    private fun updateDateInViewPerkiraanTanggalPernikahan(etTanggal: EditText) {
        val myFormat = "yyyy/MM/dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        etTanggal.setText(sdf.format(cal.time).toString())
    }

    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbCalonPengantin)
        supportActionBar!!.title = getString(R.string.app_name_layanan_catin)
        // Change font style text
        binding.tbCalonPengantin.setTitleTextAppearance(this, R.style.Theme_Stunting)

        // Enable back button if you're in a child activity
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.tbCalonPengantin.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_tgl_lahir_calon_pengantin -> getDatePickerDialogTglLahir(this@CalonPengantinActivity)
            R.id.et_perkiraan_tanggal_pernikahan_calon_pengantin -> getDatePickerDialogPerkiraanTglPernikahan()
            R.id.btn_submit_calon_pengantin -> {
                val nama = binding.etNamaPerempuanCalonPengantin.text.toString()
                val nik = binding.etNikCalonPengantin.text.toString()
                val tglLahir = binding.etTglLahirCalonPengantin.text.toString()
                val umur = binding.etUmurCalonPengantin.text.toString()
                val perkiraanTglPernikahan = binding.etPerkiraanTanggalPernikahanCalonPengantin.text.toString()

                if (nama.isNotEmpty() && nik.isNotEmpty() && tglLahir.isNotEmpty() &&
                    umur.isNotEmpty() && perkiraanTglPernikahan.isNotEmpty()) {
                    addRecord(calonPengantinDao, nama, nik, tglLahir, umur, perkiraanTglPernikahan, periksaKesehatanRadioButton, bimbinganPerkawinanRadioButton)
                } else {
                    toastInfo(
                        this@CalonPengantinActivity, getString(R.string.title_show_data_failed),
                        getString(R.string.description_show_data_failed), MotionToastStyle.ERROR
                    )
                }
            }
            R.id.btn_tampil_data_calon_pengantin -> {
                // Data not empty
                Log.e("CEK DATANA", countItem.toString())
                if (countItem != 0) showBottomSheetDialog() else {
                    toastInfo(
                        this@CalonPengantinActivity, getString(R.string.title_input_failed),
                        getString(R.string.description_input_failed), MotionToastStyle.ERROR
                    )
                }
            }
        }
    }

    private fun addRecord(
        calonPengantinDao: CalonPengantinDao, nama: String, nik: String, tglLahir: String,
        umur: String, perkiraanTglPernikahan: String, periksaKesehatan: String, bimbinganPerkawinan: String
    ) {
        val date = getDateTimePrimaryKey()

        lifecycleScope.launch {
            calonPengantinDao.insert(
                CalonPengantinEntity(
                    tanggal = date, namaCalonPengantin = nama, nikCalonPengantin = nik, tglLahirCalonPengantin = tglLahir,
                    umurCalonPengantin = umur, perkiraanTanggalPernikahanCalonPengantin = perkiraanTglPernikahan,
                    periksaKesehatanCalonPengantin = periksaKesehatan, bimbinganPerkawinanCalonPengantin = bimbinganPerkawinan
                )
            )
        }
        toastInfo(
            this@CalonPengantinActivity, getString(R.string.title_saved_data),
            getString(R.string.description_saved_data), MotionToastStyle.SUCCESS
        )

        // Clear the text when data saved !!! (success)
        binding.etNamaPerempuanCalonPengantin.text!!.clear()
        binding.etNikCalonPengantin.text!!.clear()
        binding.etTglLahirCalonPengantin.text!!.clear()
        binding.etUmurCalonPengantin.text!!.clear()
        binding.etPerkiraanTanggalPernikahanCalonPengantin.text!!.clear()
    }

    private fun getRadioButtonValue(bindingRadioGroup: Int, resultRadioButton: String) {
        val radioGroup = findViewById<RadioGroup>(bindingRadioGroup)
        // Get all the RadioButtons within the RadioGroup
        val radioButtons = radioGroup.childCount

        if (resultRadioButton == PERIKSA_KESEHATAN) {
            // Set a listener for each RadioButton
            for (i in 0 until radioButtons) {
                val radioButton = radioGroup.getChildAt(i) as RadioButton
                radioButton.setOnCheckedChangeListener { button, isChecked ->
                    if (isChecked) {
                        // Handle the selected RadioButton
                        val selectedRadioButtonText = button.text.toString()
                        if (selectedRadioButtonText == "YA") {
                            periksaKesehatanRadioButton = selectedRadioButtonText
                        } else {
                            periksaKesehatanRadioButton = selectedRadioButtonText
                        }
//                        Log.e("Selected RadioButton:", selectedRadioButtonText)
//                        Log.e("Selected RadioGroup:", resultRadioButton)
                    }
                }
            }
        } else  {
            // bimbinganPerkawinanButton
            for (i in 0 until radioButtons) {
                val radioButton = radioGroup.getChildAt(i) as RadioButton
                radioButton.setOnCheckedChangeListener { button, isChecked ->
                    if (isChecked) {
                        // Handle the selected RadioButton
                        val selectedRadioButtonText = button.text.toString()
                        if (selectedRadioButtonText == "YA") {
                            bimbinganPerkawinanRadioButton = selectedRadioButtonText
                        } else {
                            bimbinganPerkawinanRadioButton = selectedRadioButtonText
                        }
//                        Log.e("Selected RadioButton:", selectedRadioButtonText)
//                        Log.e("Selected RadioGroup:", resultRadioButton)
                    }
                }
            }
        }
    }

    private fun getDatePickerDialogPerkiraanTglPernikahan() {
        DatePickerDialog(this@CalonPengantinActivity, dataSetListenerPerkiraanTglPernikahan,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _bindingCalonPengantinBottomSheetDialog = null
        _calonPengantinDao = null
        _dataSetListenerPerkiraanTglPernikahan = null
        _cal = null
    }

    companion object {
        private const val NAME = "calonPengantin_data_"
        private const val PERIKSA_KESEHATAN = "periksaKesehatanButton"
        private const val BIMBINGAN_PERKAWINAN = "bimbinganPerkawinanButton"
    }
}