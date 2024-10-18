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
import com.example.stunting.adapter.CalonPengantinAdapter
import com.example.stunting.database.CalonPengantin.CalonPengantinDao
import com.example.stunting.database.CalonPengantin.CalonPengantinEntity
import com.example.stunting.database.DatabaseApp
import com.example.stunting.databinding.ActivityCalonPengantinBinding
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

class CalonPengantinActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityCalonPengantinBinding
    private lateinit var bindingCalonPengantinBottomSheetDialog: DialogBottomSheetAllBinding
    private lateinit var calonPengantinDao: CalonPengantinDao
    private lateinit var cal: Calendar
    private lateinit var dataSetListenerTglLahir: DatePickerDialog.OnDateSetListener
    private lateinit var dataSetListenerPerkiraanTglPernikahan: DatePickerDialog.OnDateSetListener

    var countItem = 0
    var periksaKesehatanRadioButton = "YA"
    var bimbinganPerkawinanRadioButton = "YA"

    companion object {
        const val NAME = "calonPengantin_data_"
        const val PERIKSA_KESEHATAN = "periksaKesehatanButton"
        const val BIMBINGAN_PERKAWINAN = "bimbinganPerkawinanButton"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCalonPengantinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Toolbar
        setToolBar()

        // Binding BumilBottomSheetDialog for retrieve xml id
        bindingCalonPengantinBottomSheetDialog = DialogBottomSheetAllBinding.inflate(layoutInflater)
        bindingCalonPengantinBottomSheetDialog.tvListData.text = "List Data Catin"

        // Call database
        calonPengantinDao = (application as DatabaseApp).dbCalonPengantinDatabase.calonPengantinDao()

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
            bindingCalonPengantinBottomSheetDialog.tvTotalData.text = "$countItem Data"
            bindingCalonPengantinBottomSheetDialog.rvBottomSheet.layoutManager = LinearLayoutManager(this)
            bindingCalonPengantinBottomSheetDialog.rvBottomSheet.adapter = calonPengantinAdapter
            // To scrolling automatic when data entered
            bindingCalonPengantinBottomSheetDialog.rvBottomSheet
                .smoothScrollToPosition(countItem - 1)

            // When input data automatically to last index
            bindingCalonPengantinBottomSheetDialog.rvBottomSheet
                .layoutManager!!.smoothScrollToPosition(bindingCalonPengantinBottomSheetDialog
                    .rvBottomSheet, null, countItem - 1)
        }
        Log.e("HASILNA", calonPengantinList.toString())
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

        bindingCalonPengantinBottomSheetDialog.ivDelete.setOnClickListener {
            showCustomeDeleteDialog()
        }
        bindingCalonPengantinBottomSheetDialog.ivExportToXlsx.setOnClickListener {
            showCustomeExportDataDialog()
        }
        bindingCalonPengantinBottomSheetDialog.ivArrow.setOnClickListener {
            showCustomeInfoDilog()
        }
        bindingCalonPengantinBottomSheetDialog.tvInfo.setOnClickListener {
            showCustomeInfoDilog()
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
                exportDatabaseToCSV(calonPengantinDao)
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

    private suspend fun exportDatabaseToCSV(calonPengantinDao: CalonPengantinDao) {
        // Output variabel fileDir is => /storage/emulated/0/Android/data/com.example.stunting/files/Download
        // val fileDir = "${getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/"

        var dataResult: ArrayList<List<String>> = ArrayList()
        val fileName = "${NAME}${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())}.csv"
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
                        listOf("${item.tanggal}", "${item.namaCalonPengantin}", "${item.nikCalonPengantin}",
                            "${item.tglLahirCalonPengantin}", "${item.umurCalonPengantin}", "${item.perkiraanTanggalPernikahanCalonPengantin}",
                            "${item.periksaKesehatanCalonPengantin}", "${item.bimbinganPerkawinanCalonPengantin}"
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
        toastInfo("HISTORI BERHASIL DIHAPUS SEMUA", "Silahkan jalankan kembali aplikasinya.", MotionToastStyle.SUCCESS)
    }

    private fun setCalendarTglLahir(etTanggal: EditText) {
        cal = Calendar.getInstance()
        dataSetListenerTglLahir = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInViewCalendarTglLahir(etTanggal)
        }
        updateDateInViewCalendarTglLahir(etTanggal)
    }

    private fun updateDateInViewCalendarTglLahir(etTanggal: EditText) {
        val myFormat = "yyyy/MM/dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        etTanggal.setText(sdf.format(cal.time).toString())
    }

    private fun setCalendarPerkiraanTanggalPernikahan(etTanggal: EditText) {
        cal = Calendar.getInstance()
        dataSetListenerPerkiraanTglPernikahan = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
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
        supportActionBar!!.title = "Calon Pengantin"
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
            R.id.et_tgl_lahir_calon_pengantin -> getDatePickerDialogTglLahir()
            R.id.et_perkiraan_tanggal_pernikahan_calon_pengantin -> getDatePickerDialogPerkiraanTglPernikahan()
            R.id.btn_submit_calon_pengantin -> {
                val nama = binding.etNamaCalonPengantin.text.toString()
                val nik = binding.etNikCalonPengantin.text.toString()
                val tglLahir = binding.etTglLahirCalonPengantin.text.toString()
                val umur = binding.etUmurCalonPengantin.text.toString()
                val perkiraanTglPernikahan = binding.etPerkiraanTanggalPernikahanCalonPengantin.text.toString()

                if (nama.isNotEmpty() && nik.isNotEmpty() && tglLahir.isNotEmpty() &&
                    umur.isNotEmpty() && perkiraanTglPernikahan.isNotEmpty()) {
                    addRecord(calonPengantinDao, nama, nik, tglLahir, umur, perkiraanTglPernikahan, periksaKesehatanRadioButton, bimbinganPerkawinanRadioButton)
                } else toastInfo("INPUT GAGAL !", "Data tidak boleh ada yang kosong !", MotionToastStyle.ERROR)
            }
            R.id.btn_tampil_data_calon_pengantin -> {
                // Data not empty
                Log.e("CEK DATANA", countItem.toString())
                if (countItem != 0) showBottomSheetDialog() else
                    toastInfo("TAMPILKAN DATA GAGAL !",
                        "Data masih kosong tidak ada yang ditampilkan, silahkan input data.", MotionToastStyle.ERROR)
            }
        }
    }

    private fun getDateTimePrimaryKey(): String {
        val c = Calendar.getInstance()
        val dateTime = c.time

        // 10-03-2024 Min 14:59:11
        val sdf = SimpleDateFormat("dd-MM-yyyy EEE HH:mm:ss", Locale.getDefault())
        val date = sdf.format(dateTime)
        return date
    }

    private fun addRecord(calonPengantinDao: CalonPengantinDao, nama: String, nik: String, tglLahir: String,
                          umur: String, perkiraanTglPernikahan: String, periksaKesehatan: String,
                          bimbinganPerkawinan: String) {

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
        toastInfo("DATA TERSIMPAN !", "Data berhasil di simpan di database !!", MotionToastStyle.SUCCESS)

        // Clear the text when data saved !!! (success)
        binding.etNamaCalonPengantin.text!!.clear()
        binding.etNikCalonPengantin.text!!.clear()
        binding.etTglLahirCalonPengantin.text!!.clear()
        binding.etUmurCalonPengantin.text!!.clear()
        binding.etPerkiraanTanggalPernikahanCalonPengantin.text!!.clear()
    }


    private fun getRadioButtonValue(bindingRadioGroup: Int, resultRadioButton: String) {
        val radioGroup = findViewById<RadioGroup>(bindingRadioGroup)
        // Get all the RadioButtons within the RadioGroup
        val radioButtons = radioGroup.childCount

        if (resultRadioButton == "periksaKesehatanButton") {
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
                        Log.e("Selected RadioButton:", selectedRadioButtonText)
                        Log.e("Selected RadioGroup:", resultRadioButton)
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
                        Log.e("Selected RadioButton:", selectedRadioButtonText)
                        Log.e("Selected RadioGroup:", resultRadioButton)
                    }
                }
            }
        }
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

    private fun getDatePickerDialogTglLahir() {
        DatePickerDialog(this@CalonPengantinActivity, dataSetListenerTglLahir,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun getDatePickerDialogPerkiraanTglPernikahan() {
        DatePickerDialog(this@CalonPengantinActivity, dataSetListenerPerkiraanTglPernikahan,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}