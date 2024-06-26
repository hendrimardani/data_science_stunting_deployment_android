package com.example.stunting

import android.annotation.SuppressLint
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
import com.example.stunting.Adapter.BumilAdapter
import com.example.stunting.Database.Bumil.BumilDao
import com.example.stunting.Database.Bumil.BumilEntity
import com.example.stunting.Database.DatabaseApp
import com.example.stunting.databinding.ActivityBumilBinding
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

class BumilActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityBumilBinding
    private lateinit var bumilDao: BumilDao
    private lateinit var bindingBumilBottomSheetDialog: DialogBottomSheetAllBinding
    private lateinit var cal: Calendar
    private lateinit var dataSetListenerTgllahir: DatePickerDialog.OnDateSetListener
    private lateinit var dataSetListenerHariPertamaHaidTerakhir: DatePickerDialog.OnDateSetListener
    private lateinit var dataSetListenerTglPerkiraanLahir: DatePickerDialog.OnDateSetListener

    var countItem = 0
    var statusGiziRadioButton = "YA"

    companion object {
        const val NAME = "bumil_data_"
        const val STATUS_GIZI = "statusGiziRadioButton"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBumilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Toolbar
        setToolBar()

        // Binding BumilBottomSheetDialog for retrieve xml id
        bindingBumilBottomSheetDialog = DialogBottomSheetAllBinding.inflate(layoutInflater)
        bindingBumilBottomSheetDialog.tvListData.text = "List Data Bumil"

        // Call database
        bumilDao = (application as DatabaseApp).dbBumilDatabase.bumilDao()

        // Set caledar and update in view result
        setCalendarTglLahir(binding.etTglLahirBumil)
        setCalendarHariPertamaHaidTerakhir(binding.etHariPertamaHaidTerakhirBumil)
        setCalendarTglPerkiraanLahir(binding.etTglPerkiraanLahirBumil)

        // getRadioButtomValue
        getRadioButtonValue(R.id.rg_bumil, STATUS_GIZI)

        binding.etTglLahirBumil.setOnClickListener(this)
        binding.etHariPertamaHaidTerakhirBumil.setOnClickListener(this)
        binding.etTglPerkiraanLahirBumil.setOnClickListener(this)
        binding.rgBumil.setOnClickListener(this)
        binding.btnSubmitBumil.setOnClickListener(this)
        binding.btnTampilDataBumil.setOnClickListener(this)

        // Get all items
        getAll(bumilDao)
    }

    private fun getRadioButtonValue(bindingRadioGroup: Int, resultRadioButton: String) {
        val radioGroup = findViewById<RadioGroup>(bindingRadioGroup)
        // Get all the RadioButtons within the RadioGroup
        val radioButtons = radioGroup.childCount

        if (resultRadioButton == "statusGiziRadioButton") {
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
                        Log.e("Selected RadioButton:", selectedRadioButtonText)
                    }
                }
            }
        }
    }

    private fun setCalendarTglLahir(etTanggal: EditText) {
        cal = Calendar.getInstance()
        dataSetListenerTgllahir = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInViewTglLahir(etTanggal)
        }
        updateDateInViewTglLahir(etTanggal)
    }

    private fun updateDateInViewTglLahir(etTanggal: EditText) {
        val myFormat = "yyyy/MM/dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        etTanggal.setText(sdf.format(cal.time).toString())
    }

    private fun setCalendarHariPertamaHaidTerakhir(etTanggal: EditText) {
        cal = Calendar.getInstance()
        dataSetListenerHariPertamaHaidTerakhir = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
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

    private fun setCalendarTglPerkiraanLahir(etTanggal: EditText) {
        cal = Calendar.getInstance()
        dataSetListenerTglPerkiraanLahir = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInViewTglPerkiraanLahir(etTanggal)
        }
        updateDateInViewTglPerkiraanLahir(etTanggal)
    }

    private fun updateDateInViewTglPerkiraanLahir(etTanggal: EditText) {
        val myFormat = "yyyy/MM/dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        etTanggal.setText(sdf.format(cal.time).toString())
    }

    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbBumil)
        supportActionBar!!.title = "Ibu Hamil"
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
            R.id.et_tgl_lahir_bumil -> getDatePickerDialogTglLahir()
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
                } else toastInfo("INPUT GAGAL !", "Data tidak boleh ada yang kosong !", MotionToastStyle.ERROR)
            }
            R.id.btn_tampil_data_bumil -> {
                // Data not empty
                Log.e("CEK DATANA", countItem.toString())
                if (countItem != 0) showBottomSheetDialog() else
                    toastInfo("TAMPILKAN DATA GAGAL !",
                        "Data masih kosong tidak ada yang ditampilkan, silahkan input data.", MotionToastStyle.ERROR)
            }
        }
    }

    private fun setupListOfDataIntoRecyclerView(bumilList: ArrayList<BumilEntity>) {

        if (bumilList.isNotEmpty()) {
            val allAdapter = BumilAdapter(bumilList)
            // Count item list
            countItem = bumilList.size
            bindingBumilBottomSheetDialog.tvTotalData.text = "$countItem Data"
            bindingBumilBottomSheetDialog.rvBottomSheet.layoutManager = LinearLayoutManager(this)
            bindingBumilBottomSheetDialog.rvBottomSheet.adapter = allAdapter
            // To scrolling automatic when data entered
            bindingBumilBottomSheetDialog.rvBottomSheet
                .smoothScrollToPosition(countItem - 1)

            // When input data automatically to last index
            bindingBumilBottomSheetDialog.rvBottomSheet
                .layoutManager!!.smoothScrollToPosition(bindingBumilBottomSheetDialog
                    .rvBottomSheet, null, countItem - 1)
        }
        Log.e("HASILNA", bumilList.toString())
    }

    private fun getAll(bumilDao: BumilDao) {
        lifecycleScope.launch {
            bumilDao.fetchAllBumil().collect {
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list)
            }
        }
    }

    private fun getDatePickerDialogHariPertamaHaidTerakhir() {
        DatePickerDialog(this@BumilActivity, dataSetListenerHariPertamaHaidTerakhir, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun getDatePickerDialogTglLahir() {
        DatePickerDialog(this@BumilActivity, dataSetListenerTgllahir, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun getDatePickerDialogTglPerkiraanLahir() {
        DatePickerDialog(this@BumilActivity, dataSetListenerTglPerkiraanLahir, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun getDateTimePrimaryKey(): String {
        val c = Calendar.getInstance()
        val dateTime = c.time

        // 10-03-2024 Min 14:59:11
        val sdf = SimpleDateFormat("dd-MM-yyyy EEE HH:mm:ss", Locale.getDefault())
        val date = sdf.format(dateTime)
        return date
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
        toastInfo("DATA TERSIMPAN !", "Data berhasil di simpan di database !!", MotionToastStyle.SUCCESS)

        // Clear the text when data saved !!! (success)
        binding.etNamaBumil.text!!.clear()
        binding.etNikBumil.text!!.clear()
        binding.etTglLahirBumil.text!!.clear()
        binding.etUmurBumil.text!!.clear()
        binding.etHariPertamaHaidTerakhirBumil.text!!.clear()
        binding.etTglPerkiraanLahirBumil.text!!.clear()
        binding.etUmurKehamilanBumil.text!!.clear()
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

        bindingBumilBottomSheetDialog.ivDelete.setOnClickListener {
            showCustomeDeleteDialog()
        }
        bindingBumilBottomSheetDialog.ivExportToXlsx.setOnClickListener {
            showCustomeExportDataDialog()
        }
        bindingBumilBottomSheetDialog.ivArrow.setOnClickListener {
            showCustomeInfoDilog()
        }
        bindingBumilBottomSheetDialog.tvInfo.setOnClickListener {
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

    private fun deleteAllDates(bumilDao: BumilDao) {
        lifecycleScope.launch {
            bumilDao.deleteAll()
        }
        toastInfo("HISTORI BERHASIL DIHAPUS SEMUA", "Silahkan jalankan kembali aplikasinya.", MotionToastStyle.SUCCESS)
    }

    @SuppressLint("SetTextI18n")
    private fun showCustomeExportDataDialog() {
        val viewExport = DialogCustomExportDataBinding.inflate(layoutInflater)
        val exportDialog = Dialog(this)
        exportDialog.setContentView(viewExport.root)

        viewExport.tvDescription.text = "Apakah anda yakin ingin mengeksport data ke Excel pada " +
                "${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())} ? " +
                "Untuk melihat hasilnya silahkan cek dengan $NAME(tahunbulantanggal_jammenitdetik).xlsx di folder Download/Unduhan" +
                " hari ini Cth: $NAME${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())}.xlsx"
        viewExport.tvYes.setOnClickListener {
            // Export to CSV
            lifecycleScope.launch {
                toastInfo("DATA BERHASIL DI EKSPOR", "Silahkan cek di folder Download atau Unduhan penyimpanan internal anda !", MotionToastStyle.SUCCESS)
                exportDatabaseToCSV(bumilDao)
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
                        listOf("${item.tanggal}", "${item.namaBumil}", "${item.tglLahirBumil}",
                            "${item.tglLahirBumil}", "${item.umurBumil}", "${item.hariPertamaHaidTerakhirBumil}",
                            "${item.tanggalPerkiraanLahirBumil}", "${item.umurKehamilanBumil}", "${item.statusGiziKesehatanBumil}")
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
}