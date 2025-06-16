package com.example.stunting.ui.reamaja_putri

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stunting.R
import com.example.stunting.adapter.RemajaPutriAdapter
import com.example.stunting.database.no_api.DatabaseApp
import com.example.stunting.database.no_api.remaja_putri.RemajaPutriDao
import com.example.stunting.database.no_api.remaja_putri.RemajaPutriEntity
import com.example.stunting.databinding.ActivityRemajaPutriBinding
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
import java.util.Date
import kotlin.math.abs

class RemajaPutriActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityRemajaPutriBinding? = null
    private val binding get() = _binding!!
    private var _bindingRemajaPutriBottomSheetDialog: DialogBottomSheetAllBinding? = null
    private val bindingRemajaPutriBottomSheetDialog get() = _bindingRemajaPutriBottomSheetDialog!!
    private var _remajaPutriDao: RemajaPutriDao? = null
    private val remajaPutriDao get() = _remajaPutriDao!!

    var countItem = 0
    var mendapatTtdRadioButton = "YA"
    var periksaAnemiaRadioButton = "YA"
    var hasilPeriksaAnemiaRadioButton = "YA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityRemajaPutriBinding.inflate(layoutInflater)
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
        _bindingRemajaPutriBottomSheetDialog = DialogBottomSheetAllBinding.inflate(layoutInflater)
        bindingRemajaPutriBottomSheetDialog.tvListData.text = getString(R.string.list_data_rematri)

        // Call database
        _remajaPutriDao = (application as DatabaseApp).dbApp.remajaPutriDao()

        getRadioButtonValue(R.id.rg_mendapat_ttd_remaja_putri, MENDAPAT_TTD)
        getRadioButtonValue(R.id.rg_periksa_anemia_remaja_putri, PERIKSA_ANEMIA)
        getRadioButtonValue(R.id.rg_hasil_periksa_anemia_remaja_putri, HASIL_PERIKSA_ANEMIA)

        // Set caledar and update in view result
        setCalendarTglLahir(binding.etTglLahirRemajaPutri)

        binding.etTglLahirRemajaPutri.setOnClickListener(this)

        binding.btnSubmitRemajaPutri.setOnClickListener(this)
        binding.btnTampilDataRemajaPutri.setOnClickListener(this)

        // Set inputText umur from calculate tgl lahir
        setInputTextUmur()

        // Get all items
        getAll(remajaPutriDao)
    }

    private fun setInputTextUmur() {
        // Ketika tiap sentuh inputText, inputText umur akan terupdate
        binding.etTglLahirRemajaPutri.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                val tglLahir = s.toString()
                // Log format untuk debugging
//                Log.e("TEST FORMAT", tglLahir)

                // Pastikan tglLahir tidak kosong sebelum mencoba menghitung umur
                if (tglLahir.isNotEmpty()) {
                    val umur = calculateAge(tglLahir)
                    binding.etUmurRemajaPutri.setText(umur)
                } else {
                    // Clear tglLahir jika input tanggal kosong
                    binding.etUmurRemajaPutri.setText("")
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
                binding.collapsingToolbarLayout.title = getString(R.string.app_name_layanan_rematri)
            } else {
                // Jika masih expanded, sembunyikan judul
                binding.collapsingToolbarLayout.title = ""
            }
        }
    }

    private fun getRadioButtonValue(bindingRadioGroup: Int, resultRadioButton: String) {
        val radioGroup = findViewById<RadioGroup>(bindingRadioGroup)
        // Get all the RadioButtons within the RadioGroup
        val radioButtons = radioGroup.childCount

        if (resultRadioButton == MENDAPAT_TTD) {
            // Set a listener for each RadioButton
            for (i in 0 until radioButtons) {
                val radioButton = radioGroup.getChildAt(i) as RadioButton
                radioButton.setOnCheckedChangeListener { button, isChecked ->
                    if (isChecked) {
                        // Handle the selected RadioButton
                        val selectedRadioButtonText = button.text.toString()
                        if (selectedRadioButtonText == "YA") {
                            mendapatTtdRadioButton = selectedRadioButtonText
                        } else {
                            mendapatTtdRadioButton = selectedRadioButtonText
                        }
//                        Log.e("Selected RadioButton:", selectedRadioButtonText)
                    }
                }
            }
        } else if (resultRadioButton == PERIKSA_ANEMIA) {
            // periksaAnemiaRadioButton
            for (i in 0 until radioButtons) {
                val radioButton = radioGroup.getChildAt(i) as RadioButton
                radioButton.setOnCheckedChangeListener { button, isChecked ->
                    if (isChecked) {
                        // Handle the selected RadioButton
                        val selectedRadioButtonText = button.text.toString()
                        if (selectedRadioButtonText == "YA") {
                            periksaAnemiaRadioButton = selectedRadioButtonText
                        } else {
                            periksaAnemiaRadioButton = selectedRadioButtonText
                        }
//                        Log.e("Selected RadioButton:", selectedRadioButtonText)
                    }
                }
            }
        } else {
            // hasilPeriksaAnemiaRadioButton
            for (i in 0 until radioButtons) {
                val radioButton = radioGroup.getChildAt(i) as RadioButton
                radioButton.setOnCheckedChangeListener { button, isChecked ->
                    if (isChecked) {
                        // Handle the selected RadioButton
                        val selectedRadioButtonText = button.text.toString()
                        if (selectedRadioButtonText == "YA") {
                            hasilPeriksaAnemiaRadioButton = selectedRadioButtonText
                        } else {
                            hasilPeriksaAnemiaRadioButton = selectedRadioButtonText
                        }
//                        Log.e("Selected RadioButton:", selectedRadioButtonText)
                    }
                }
            }
        }
    }

    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbRemajaPutri)
        supportActionBar!!.title = getString(R.string.app_name_layanan_rematri)
        // Change font style text
        binding.tbRemajaPutri.setTitleTextAppearance(this, R.style.Theme_Stunting)
        // Enable back button if you're in a child activity
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.tbRemajaPutri.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun getAll(remajaPutriDao: RemajaPutriDao) {
        lifecycleScope.launch {
            remajaPutriDao.fetchAllRemajaPutri().collect {
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list)
            }
        }
    }

    private fun setupListOfDataIntoRecyclerView(remajaPutriList: ArrayList<RemajaPutriEntity>) {

        if (remajaPutriList.isNotEmpty()) {
            val remajaPutriAdapter = RemajaPutriAdapter(remajaPutriList)
            // Count item list
            countItem = remajaPutriList.size
            bindingRemajaPutriBottomSheetDialog.tvTotalData.text = getString(R.string.setup_recycler_view_sum_data, countItem.toString())
            bindingRemajaPutriBottomSheetDialog.rvBottomSheet.layoutManager = LinearLayoutManager(this)
            bindingRemajaPutriBottomSheetDialog.rvBottomSheet.adapter = remajaPutriAdapter
            // To scrolling automatic when data entered
            bindingRemajaPutriBottomSheetDialog.rvBottomSheet.smoothScrollToPosition(countItem - 1)

            // When input data automatically to last index
            bindingRemajaPutriBottomSheetDialog.rvBottomSheet
                .layoutManager!!.smoothScrollToPosition(bindingRemajaPutriBottomSheetDialog
                    .rvBottomSheet, null, countItem - 1)
        }
//        Log.e("HASILNA", remajaPutriList.toString())
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_tgl_lahir_remaja_putri -> getDatePickerDialogTglLahir(this@RemajaPutriActivity)
            R.id.btn_submit_remaja_putri -> {
                val nama = binding.etNamaRemajaPutri.text.toString()
                val nik = binding.etNikRemajaPutri.text.toString()
                val tglLahir = binding.etTglLahirRemajaPutri.text.toString()
                val umur = binding.etUmurRemajaPutri.text.toString()

                if (nama.isNotEmpty() && nik.isNotEmpty() && tglLahir.isNotEmpty() && umur.isNotEmpty()) {
                    addRecord(remajaPutriDao, nama, nik, tglLahir, umur, mendapatTtdRadioButton,
                        periksaAnemiaRadioButton, hasilPeriksaAnemiaRadioButton)
                } else toastInfo(
                    this@RemajaPutriActivity, getString(R.string.title_input_failed),
                    getString(R.string.description_input_failed), MotionToastStyle.ERROR
                )
            }
            R.id.btn_tampil_data_remaja_putri -> {
                // Data not empty
//                Log.e("CEK DATANA", countItem.toString())
                if (countItem != 0) showBottomSheetDialog()
                else {
                    toastInfo(
                        this@RemajaPutriActivity,
                        getString(R.string.title_show_data_failed),
                        getString(R.string.description_show_data_failed), MotionToastStyle.ERROR
                    )
                }
            }
        }
    }

    private fun showBottomSheetDialog() {
        // Check if the view already has a parent
        val viewBottomSheetDialog: View = bindingRemajaPutriBottomSheetDialog.root

        if (viewBottomSheetDialog.parent != null) {
            val parentViewGroup = viewBottomSheetDialog.parent as ViewGroup
            parentViewGroup.removeView(viewBottomSheetDialog)
        }
        // Now set the view as content for the BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(viewBottomSheetDialog)
        bottomSheetDialog.show()

        bindingRemajaPutriBottomSheetDialog.apply {
            ivDelete.setOnClickListener { showCustomeDeleteDialog() }
            ivExportToXlsx.setOnClickListener { showCustomeExportDataDialog() }
            ivArrow.setOnClickListener { showCustomeInfoDialog(this@RemajaPutriActivity, layoutInflater) }
            tvInfo.setOnClickListener { showCustomeInfoDialog(this@RemajaPutriActivity, layoutInflater) }
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
                    this@RemajaPutriActivity, getString(R.string.title_export_success),
                    getString(R.string.description_export_success), MotionToastStyle.SUCCESS
                )
                exportDatabaseToCSV(remajaPutriDao)
            }
            exportDialog.dismiss()
            // Goto link directory download
            linkToDirectory(this@RemajaPutriActivity)
            // Destroying when exported successfully
            finish()
        }
        viewExport.tvNo.setOnClickListener {
            exportDialog.dismiss()
        }
        exportDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private suspend fun exportDatabaseToCSV(remajaPutriDao: RemajaPutriDao) {
        // Output variabel fileDir is => /storage/emulated/0/Android/data/com.example.stunting/files/Download
        // val fileDir = "${getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/"

        var dataResult: ArrayList<List<String>> = ArrayList()
        val fileName = "$NAME${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())}.csv"
        val fileDir = "/storage/emulated/0/Download/"
        try {
            val file = File(fileDir, fileName)
            dataResult.add(
                listOf("tanggal", "Nama Remaja Putri", "NIK", "Tanggal Lahir",
                    "Umur (tahun)", "Mendapat TTD (YA/TIDAK)", "Periksa Anemia (YA/TIDAK)", "Hasil Periksa Anemia (YA/TIDAK)")
            )
            remajaPutriDao.fetchAllRemajaPutri().collect {
                for (item in it) {
                    dataResult.add(
                        listOf(
                            item.tanggal, item.namaRemajaPutri, item.nikRemajaPutri,
                            item.tglLahirRemajaPutri, item.umurRemajaPutri,
                            item.mendapatTtdRemajaPutri,
                            item.periksaAnemiaRemajaPutri, item.hasilPeriksaAnemiaRemajaPutri
                        )
                    )
                    csvWriter().writeAll(dataResult, file.outputStream())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            toastInfo(
                this@RemajaPutriActivity, getString(R.string.title_export_failed),
                getString(R.string.description_export_failed), MotionToastStyle.ERROR
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
            deleteAllDates(remajaPutriDao)
            // We will destroy activity
            finish()
            deleteDialog.dismiss()
        }
        viewDelete.tvNo.setOnClickListener {
            deleteDialog.dismiss()
        }
        deleteDialog.show()
    }

    private fun deleteAllDates(remajaPutriDao: RemajaPutriDao) {
        lifecycleScope.launch {
            remajaPutriDao.deleteAll()
        }
        toastInfo(
            this@RemajaPutriActivity,getString(R.string.title_history_delete),
            getString(R.string.description_history_delete), MotionToastStyle.SUCCESS
        )
    }

    private fun addRecord(remajaPutriDao: RemajaPutriDao, nama: String, nik: String, tglLahir: String,
                          umur: String, mendapatTtd: String, periksaAnemia: String, hasilPeriksaAnemia: String) {

        val date = getDateTimePrimaryKey()

        lifecycleScope.launch {
            remajaPutriDao.insert(
                RemajaPutriEntity(
                    tanggal = date, namaRemajaPutri = nama, nikRemajaPutri = nik, tglLahirRemajaPutri = tglLahir,
                    umurRemajaPutri = umur, mendapatTtdRemajaPutri = mendapatTtd,
                    periksaAnemiaRemajaPutri = periksaAnemia, hasilPeriksaAnemiaRemajaPutri = hasilPeriksaAnemia
                )
            )
        }
        toastInfo(
            this@RemajaPutriActivity, getString(R.string.title_saved_data),
            getString(R.string.description_saved_data), MotionToastStyle.SUCCESS
        )

        // Clear the text when data saved !!! (success)
        binding.etNamaRemajaPutri.text!!.clear()
        binding.etNikRemajaPutri.text!!.clear()
        binding.etTglLahirRemajaPutri.text!!.clear()
        binding.etUmurRemajaPutri.text!!.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _bindingRemajaPutriBottomSheetDialog = null
        _remajaPutriDao = null
    }

    companion object {
        private const val NAME = "remajaPutri_data_"
        private const val MENDAPAT_TTD = "mendapatTtdButton"
        private const val PERIKSA_ANEMIA = "periksaAnemiaButton"
        private const val HASIL_PERIKSA_ANEMIA = "hasilPeriksaAnemiaButton"
    }
}