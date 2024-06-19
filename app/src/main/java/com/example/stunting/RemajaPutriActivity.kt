package com.example.stunting

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stunting.Adapter.CalonPengantinAdapter
import com.example.stunting.Adapter.RemajaPutriAdapter
import com.example.stunting.Database.CalonPengantin.CalonPengantinDao
import com.example.stunting.Database.CalonPengantin.CalonPengantinEntity
import com.example.stunting.Database.DatabaseApp
import com.example.stunting.Database.RemajaPutri.RemajaPutriDao
import com.example.stunting.Database.RemajaPutri.RemajaPutriEntity
import com.example.stunting.databinding.ActivityRemajaPutriBinding
import com.example.stunting.databinding.DialogBottomSheetAllBinding
import com.example.stunting.databinding.DialogCustomDeleteBinding
import com.example.stunting.databinding.DialogCustomExportDataBinding
import com.example.stunting.databinding.DialogCustomeInfoBinding
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RemajaPutriActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityRemajaPutriBinding
    private lateinit var bindingRemajaPutriBottomSheetDialog: DialogBottomSheetAllBinding
    private lateinit var remajaPutriDao: RemajaPutriDao
    private lateinit var cal: Calendar
    private lateinit var dataSetListener: DatePickerDialog.OnDateSetListener

    var countItem = 0
    var mendapatTtdRadioButton = "YA"
    var periksaAnemiaRadioButton = "YA"
    var hasilPeriksaAnemiaRadioButton = "YA"

    companion object {
        const val NAME = "remajaPutri_data_"
        const val MENDAPAT_TTD = "mendapatTtdRadioButton"
        const val PERIKSA_ANEMIA = "periksaAnemiaRadioButton"
        const val HASIL_PERIKSA_ANEMIA = "hasilPeriksaAnemiaRadioButton"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRemajaPutriBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Toolbar
        setToolBar()

        // Binding BumilBottomSheetDialog for retrieve xml id
        bindingRemajaPutriBottomSheetDialog = DialogBottomSheetAllBinding.inflate(layoutInflater)
        bindingRemajaPutriBottomSheetDialog.tvListData.text = "List Data       \nRematri"

        // Call database
        remajaPutriDao = (application as DatabaseApp).dbRemajaPutriDatabase.remajaPutriDao()

        getRadioButtonValue(R.id.rg_mendapat_ttd_remaja_putri, MENDAPAT_TTD)
        getRadioButtonValue(R.id.rg_periksa_anemia_remaja_putri, PERIKSA_ANEMIA)
        getRadioButtonValue(R.id.rg_hasil_periksa_anemia_remaja_putri, HASIL_PERIKSA_ANEMIA)

        // Set caledar and update in view result
        setCalendar(binding.etTglLahirRemajaPutri)

        binding.etTglLahirRemajaPutri.setOnClickListener(this)

        binding.btnSubmitRemajaPutri.setOnClickListener(this)
        binding.btnTampilDataRemajaPutri.setOnClickListener(this)

        // Get all items
        getAll(remajaPutriDao)
    }

    private fun getRadioButtonValue(bindingRadioGroup: Int, resultRadioButton: String) {
        val radioGroup = findViewById<RadioGroup>(bindingRadioGroup)
        // Get all the RadioButtons within the RadioGroup
        val radioButtons = radioGroup.childCount

        if (resultRadioButton == "mendapatTtdRadioButton") {
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
                        Log.e("Selected RadioButton:", selectedRadioButtonText)
                    }
                }
            }
        } else if (resultRadioButton == "periksaAnemiaRadioButton") {
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
                        Log.e("Selected RadioButton:", selectedRadioButtonText)
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
                        Log.e("Selected RadioButton:", selectedRadioButtonText)
                    }
                }
            }
        }
    }

    private fun setCalendar(etTanggal: EditText) {
        cal = Calendar.getInstance()
        dataSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView(etTanggal)
        }
        updateDateInView(etTanggal)
    }

    private fun updateDateInView(etTanggal: EditText) {
        val myFormat = "yyyy/MM/dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        etTanggal.setText(sdf.format(cal.time).toString())
    }

    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbRemajaPutri)
        supportActionBar!!.title = "Remaja Putri"
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
            bindingRemajaPutriBottomSheetDialog.tvTotalData.text = "$countItem Data"
            bindingRemajaPutriBottomSheetDialog.rvBottomSheet.layoutManager = LinearLayoutManager(this)
            bindingRemajaPutriBottomSheetDialog.rvBottomSheet.adapter = remajaPutriAdapter
            // To scrolling automatic when data entered
            bindingRemajaPutriBottomSheetDialog.rvBottomSheet
                .smoothScrollToPosition(countItem - 1)

            // When input data automatically to last index
            bindingRemajaPutriBottomSheetDialog.rvBottomSheet
                .layoutManager!!.smoothScrollToPosition(bindingRemajaPutriBottomSheetDialog
                    .rvBottomSheet, null, countItem - 1)
        }
        Log.e("HASILNA", remajaPutriList.toString())
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_tgl_lahir_remaja_putri -> getDatePickerDialog()
            R.id.btn_submit_remaja_putri -> {
                val nama = binding.etNamaRemajaPutri.text.toString()
                val nik = binding.etNikRemajaPutri.text.toString()
                val tglLahir = binding.etTglLahirRemajaPutri.text.toString()
                val umur = binding.etUmurRemajaPutri.text.toString()

                if (nama.isNotEmpty() && nik.isNotEmpty() && tglLahir.isNotEmpty() && umur.isNotEmpty()) {
                    addRecord(remajaPutriDao, nama, nik, tglLahir, umur, mendapatTtdRadioButton,
                        periksaAnemiaRadioButton, hasilPeriksaAnemiaRadioButton)
                }
            }
            R.id.btn_tampil_data_remaja_putri -> {
                showBottomSheetDialog()
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

        bindingRemajaPutriBottomSheetDialog.ivDelete.setOnClickListener {
            showCustomeDeleteDialog()
        }
        bindingRemajaPutriBottomSheetDialog.ivExportToXlsx.setOnClickListener {
            showCustomeExportDataDialog()
        }
        bindingRemajaPutriBottomSheetDialog.ivArrow.setOnClickListener {
            showCustomeInfoDilog()
        }
        bindingRemajaPutriBottomSheetDialog.tvInfo.setOnClickListener {
//            showCustomeInfoDilog()
        }
    }

    private fun showCustomeInfoDilog() {
        val bindingBumilBottomSheetDialog = DialogCustomeInfoBinding.inflate(layoutInflater)
        // Check if the view already has a parent
        val viewBottomSheetDialog: View = bindingBumilBottomSheetDialog.root

        val infoDialog = Dialog(this)
        infoDialog.setContentView(viewBottomSheetDialog)
        // Set content
        bindingBumilBottomSheetDialog.tvDescription.text = "Untuk melihat detail data bisa eksport terlebih dahulu datanya."
        bindingBumilBottomSheetDialog.tvYes.setOnClickListener {
            infoDialog.dismiss()
        }
        infoDialog.show()
    }

    private fun showCustomeExportDataDialog() {
        val viewExport = DialogCustomExportDataBinding.inflate(layoutInflater)
        val exportDialog = Dialog(this)
        exportDialog.setContentView(viewExport.root)

        viewExport.tvDescription.text = "Apakah anda yakin ingin mengeksport data ke Excel pada " +
                "${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())} ? " +
                "Untuk melihat hasilnya silahkan cek dengan ${NAME}(tahunbulantanggal_jammenitdetik).xlsx di folder Download/Unduhan" +
                " hari ini Cth: ${NAME}${SimpleDateFormat("yyyyMMMdd_HHmmss").format(
                    Date()
                )}.xlsx"
        viewExport.tvYes.setOnClickListener {
            // Export to CSV
            lifecycleScope.launch {
                toastInfo("DATA BERHASIL DI EKSPOR", "Silahkan cek di folder Download atau Unduhan penyimpanan internal anda !", MotionToastStyle.SUCCESS)
                exportDatabaseToCSV(remajaPutriDao)
            }
            exportDialog.dismiss()
            // Goto link directory download
            linkToDirectory()
            // Destroying when exported successfully
            finish()
        }
        viewExport.tvNo.setOnClickListener {
            exportDialog.dismiss()
        }
        exportDialog.show()
    }

    private fun linkToDirectory() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, 101) // Replace REQUEST_CODE with a unique code (free numeric)
    }

    private suspend fun exportDatabaseToCSV(remajaPutriDao: RemajaPutriDao) {
        // Output variabel fileDir is => /storage/emulated/0/Android/data/com.example.stunting/files/Download
        // val fileDir = "${getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/"

        var dataResult: ArrayList<List<String>> = ArrayList()
        val fileName = "${NAME}${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())}.csv"
        val fileDir = "/storage/emulated/0/Download/"
        try {
            val file = File(fileDir, fileName)
            dataResult.add(
                listOf("tanggal", "Nama Remaja Putri", "NIK", "Tanggal Lahir",
                    "Umur", "Mendapat TTD (YA/TIDAK)", "Periksa Anemia (YA/TIDAK)", "Hasil Periksa Anemia (YA/TIDAK)")
            )
            remajaPutriDao.fetchAllRemajaPutri().collect {
                for (item in it) {
                    dataResult.add(
                        listOf("${item.tanggal}", "${item.namaRemajaPutri}", "${item.nikRemajaPutri}",
                            "${item.tglLahirRemajaPutri}","${item.umurRemajaPutri}", "${item.mendapatTtdRemajaPutri}",
                            "${item.periksaAnemiaRemajaPutri}", "${item.hasilPeriksaAnemiaRemajaPutri}"
                        )
                    )
                    csvWriter().writeAll(dataResult, file.outputStream())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            toastInfo("Gagal Mengekspor File !",
                "Silahkan coba lagi !, atau jika mengalami kendala hubungi pembuat aplikasi !", MotionToastStyle.ERROR)
        }
    }

    private fun showCustomeDeleteDialog() {
        val viewDelete = DialogCustomDeleteBinding.inflate(layoutInflater)
        val deleteDialog = Dialog(this)
        deleteDialog.setContentView(viewDelete.root)

        viewDelete.tvDescription.text = "Ini akan mengakibatkan semua data terhapus !, pastikan sebelum menghapus eksport terlebih dahulu."
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
        toastInfo("HISTORI BERHASIL DIHAPUS SEMUA", "Silahkan jalankan kembali aplikasinya.", MotionToastStyle.SUCCESS)
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
        toastInfo("DATA TERSIMPAN !", "Data berhasil di simpan di database !!", MotionToastStyle.SUCCESS)

        // Clear the text when data saved !!! (success)
        binding.etNamaRemajaPutri.text!!.clear()
        binding.etNikRemajaPutri.text!!.clear()
        binding.etTglLahirRemajaPutri.text!!.clear()
        binding.etUmurRemajaPutri.text!!.clear()
    }

    private fun getDateTimePrimaryKey(): String {
        val c = Calendar.getInstance()
        val dateTime = c.time

        // 10-03-2024 Min 14:59:11
        val sdf = SimpleDateFormat("dd-MM-yyyy EEE HH:mm:ss", Locale.getDefault())
        val date = sdf.format(dateTime)
        return date
    }

    private fun toastInfo(title: String, description: String, info: MotionToastStyle) {
        MotionToast.createToast(this,
            title,
            description,
            info,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helveticabold)
        )
    }

    private fun getDatePickerDialog() {
        DatePickerDialog(
            this@RemajaPutriActivity,
            dataSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}