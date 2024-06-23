package com.example.stunting

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stunting.Adapter.AnakAdapter
import com.example.stunting.Database.Bumil.BumilDao
import com.example.stunting.Database.Child.AnakDao
import com.example.stunting.Database.Child.AnakEntity
import com.example.stunting.Database.DatabaseApp
import com.example.stunting.databinding.ActivityAnakBinding
import com.example.stunting.databinding.DialogBottomSheetAnakBinding
import com.example.stunting.databinding.DialogCustomDeleteBinding
import com.example.stunting.databinding.DialogCustomExportDataBinding
import com.example.stunting.databinding.DialogCustomeInfoBinding
import com.example.stunting.ml.ModelRegularizer
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.log

class AnakActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnakBinding
    private lateinit var anakDao: AnakDao
    private lateinit var bindingAnakBottomSheetDialog: DialogBottomSheetAnakBinding
    private lateinit var cal: Calendar
    private lateinit var dataSetListenerTgllahir: DatePickerDialog.OnDateSetListener

    var countItem = 0
    var classification = "NORMAL"
    var jkOutput = "Laki-laki"

    companion object {
        const val NAME = "anak_data_"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAnakBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Binding BumilBottomSheetDialog for retrieve xml id
        bindingAnakBottomSheetDialog = DialogBottomSheetAnakBinding.inflate(layoutInflater)

        // Toolbar
        setToolBar()

        // Call database
        anakDao = (application as DatabaseApp).dbChildDatabase.anakDao()

        // Set caledar and update in view result
        setCalendarTglLahir(binding.etTglAnak)

        binding.etTglAnak.setOnClickListener {
            getDatePickerDialogTglLahir()
        }

        binding.btnSubmitAnak.setOnClickListener {
            val nama = binding.etNamaAnak.text.toString()
            val jk = binding.etJkAnak.text.toString()
            val nik = binding.etNikAnak.text.toString()
            val tglLahir = binding.etTglAnak.text.toString()
            val umur = binding.etUmurAnak.text.toString()
            val tinggi = binding.etTinggiAnak.text.toString()
            val namaOrtu = binding.etNamaOrtuAnak.text.toString()

            // Check for rename 0 for male and 1 for female
            if (jk == "0") jkOutput = "Laki-laki" else jkOutput = "Perempuan"

            if (nama.isNotEmpty() && nik.isNotEmpty() && tglLahir.isNotEmpty() &&
                umur.isNotEmpty() && jk.isNotEmpty() && tinggi.isNotEmpty() && namaOrtu.isNotEmpty()) {

                // Checking if there is no contains 0 (laki-laki) or 1 (perempuan)
                if (jk.contains("0") or jk.contains("1")) {
                    // Get data for predictions
                    val umurPred = umur.toFloat()
                    val jkPred = jk.toFloat()
                    val tinggiPred = tinggi.toFloat()

                    // Prediction
                    prediction(anakDao, nama, jk, nik, tglLahir, namaOrtu, umur, tinggi, umurPred, jkPred, tinggiPred)
                } else toastInfo("KODE JENIS KELAMIN TIDAK VALID !", "Data jenis kelamin hanya angka 0 dan 1 !", MotionToastStyle.ERROR)
            } else toastInfo("INPUT GAGAL !", "Data tidak boleh ada yang kosong !", MotionToastStyle.ERROR)
        }

        binding.btnTampilDataAnak.setOnClickListener {
            // Data not empty
            Log.e("CEK DATANA", countItem.toString())
            if (countItem != 0) showBottomSheetDialog() else
                toastInfo("TAMPILKAN DATA GAGAL !",
                    "Data masih kosong tidak ada yang ditampilkan, silahkan input data.", MotionToastStyle.ERROR)
        }

        // Get all items
        getAll(anakDao)
    }

    private fun getAll(anakDao: AnakDao) {
        lifecycleScope.launch {
            anakDao.fetchAllAnak().collect {
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list)
            }
        }
    }

    private fun showBottomSheetDialog() {
        // Check if the view already has a parent
        val viewBottomSheetDialog: View = bindingAnakBottomSheetDialog.root

        if (viewBottomSheetDialog.parent != null) {
            val parentViewGroup = viewBottomSheetDialog.parent as ViewGroup
            parentViewGroup.removeView(viewBottomSheetDialog)
        }
        // Now set the view as content for the BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(viewBottomSheetDialog)
        bottomSheetDialog.show()

        bindingAnakBottomSheetDialog.ivDelete.setOnClickListener {
            showCustomeDeleteDialog()
        }
        bindingAnakBottomSheetDialog.ivExportToXlsx.setOnClickListener {
            showCustomeExportDataDialog()
        }
        bindingAnakBottomSheetDialog.ivArrow.setOnClickListener {
            showCustomeInfoAnakDialog()
        }
        bindingAnakBottomSheetDialog.tvInfo.setOnClickListener {
            showCustomeInfoAnakDialog()
        }
    }

    private fun showCustomeInfoAnakDialog() {
        val bindingAnakBottomSheetDialog = DialogCustomeInfoBinding.inflate(layoutInflater)
        // Check if the view already has a parent
        val viewBottomSheetDialog: View = bindingAnakBottomSheetDialog.root

        val infoDialog = Dialog(this)
        infoDialog.setContentView(viewBottomSheetDialog)
        // Set content
        bindingAnakBottomSheetDialog.tvDescription.text = "Untuk melihat detail data bisa eksport terlebih dahulu datanya."
        bindingAnakBottomSheetDialog.tvYes.setOnClickListener {
            infoDialog.dismiss()
        }
        infoDialog.show()
    }

    private fun linkToDirectory() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, 101) // Replace REQUEST_CODE with a unique code (free numeric)
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
                exportDatabaseToCSV(anakDao)
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

    private suspend fun exportDatabaseToCSV(anakDao: AnakDao) {
        // Output variabel fileDir is => /storage/emulated/0/Android/data/com.example.stunting/files/Download
        // val fileDir = "${getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/"

        var dataResult: ArrayList<List<String>> = ArrayList()
        val fileName = "${NAME}${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())}.csv"
        val fileDir = "/storage/emulated/0/Download/"
        try {
            val file = File(fileDir, fileName)
            dataResult.add(
                listOf("tanggal", "Nama Anak", "Jenis Kelamin", "NIK", "Tanggal Lahir", "Umur",
                    "Tinggi Badan (cm)", "Nama Orang Tua", "Hasil")
            )
            anakDao.fetchAllAnak().collect {
                for (item in it) {
                    dataResult.add(
                        listOf("${item.tanggal}", "${item.namaAnak}", "${item.jkAnak}",
                            "${item.nikAnak}", "${item.tglLahirAnak}", "${item.umurAnak}",
                            "${item.tinggiAnak}", "${item.ortuAnak}", "${item.klasifikasiAnak}")
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
            deleteAllDates(anakDao)
            // We will destroy activity
            finish()
            deleteDialog.dismiss()
        }
        viewDelete.tvNo.setOnClickListener {
            deleteDialog.dismiss()
        }
        deleteDialog.show()
    }

    private fun deleteAllDates(anakDao: AnakDao) {
        lifecycleScope.launch {
            anakDao.deleteAll()
        }
        toastInfo("HISTORI BERHASIL DIHAPUS SEMUA", "Silahkan jalankan kembali aplikasinya.", MotionToastStyle.SUCCESS)
    }

    private fun setupListOfDataIntoRecyclerView(anakList: ArrayList<AnakEntity>) {
        if (anakList.isNotEmpty()) {
            val anakAdapter = AnakAdapter(anakList)
            // Count item list
            countItem = anakList.size
            bindingAnakBottomSheetDialog.tvTotalData.text = "$countItem Data"
            bindingAnakBottomSheetDialog.rvBottomSheetAnak.layoutManager = LinearLayoutManager(this)
            bindingAnakBottomSheetDialog.rvBottomSheetAnak.adapter = anakAdapter
            // To scrolling automatic when data entered
            bindingAnakBottomSheetDialog.rvBottomSheetAnak
                .smoothScrollToPosition(countItem - 1)

            // When input data automatically to last index
            bindingAnakBottomSheetDialog.rvBottomSheetAnak
                .layoutManager!!.smoothScrollToPosition(bindingAnakBottomSheetDialog
                    .rvBottomSheetAnak, null, countItem - 1)
        }
        Log.e("HASILNA", anakList.toString())
    }

    private fun prediction(anakDao: AnakDao, nama: String, jk: String, nik: String, tglLahir: String,
        namaOrtu: String, umur: String, tinggi: String, umurFloat: Float, jkFloat: Float, tinggiFloat: Float) {

        // Normalisasi data with formula => (Xi - Xmin) / (Xmax - Xmin)
        val umurNormalized = (umurFloat - 0f) / (60f - 0f)
        val tinggiNormalized = (tinggiFloat - 40.01f) / (128f - 40.0104370037594f)

        try { // this is apropriated by input and output value, 3 * 4 It depends on memory allocated
            val byteBuffer = ByteBuffer.allocateDirect(3 * 4)
            byteBuffer.order(ByteOrder.nativeOrder())
            byteBuffer.putFloat(umurNormalized)
            byteBuffer.putFloat(jkFloat)
            byteBuffer.putFloat(tinggiNormalized)

            val model = ModelRegularizer.newInstance(this)

            // Creates inputs for reference.
            val inputFeature0 =
                TensorBuffer.createFixedSize(intArrayOf(1, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(byteBuffer)

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

            // If the result value is less than 0.5 then Normal, otherwise is Stunting
            if (Math.round(outputFeature0[0]) < 0.5) {
                classification = "NORMAL"

                // Add record
                addRecord(anakDao, nama, nik, tglLahir, umur, jk, tinggi, namaOrtu, classification)
            } else {
                classification = "STUNTING"
                // Add record
                addRecord(anakDao, nama, nik, tglLahir, umur, jk, tinggi, namaOrtu, classification)
            }
            // Releases model resources if no longer used.
            model.close()
        } catch (e: Exception) {
            e.printStackTrace()
            toastInfo(
                "Gagal Mengekspor File !",
                "Silahkan coba lagi !, atau jika mengalami kendala hubungi pembuat aplikasi !",
                MotionToastStyle.ERROR
            )
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

    private fun getDatePickerDialogTglLahir() {
        DatePickerDialog(this@AnakActivity, dataSetListenerTgllahir, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun addRecord(anakDao: AnakDao, nama: String, nik: String, tglLahir: String,
        umur: String, jk: String, tinggi: String, namaOrtu: String, hasil: String) {

        val date = getDateTimePrimaryKey()

        lifecycleScope.launch {
            anakDao.insert(
                AnakEntity(
                    tanggal = date,
                    namaAnak = nama,
                    nikAnak = nik,
                    tglLahirAnak = tglLahir,
                    umurAnak = umur,
                    jkAnak = jk,
                    tinggiAnak = tinggi,
                    ortuAnak = namaOrtu,
                    klasifikasiAnak = hasil
                )
            )
        }
        toastInfo("DATA TERSIMPAN !", "Data berhasil di simpan di database !!", MotionToastStyle.SUCCESS)

        // Clear the text when data saved !!! (success)
        binding.etNamaAnak.text!!.clear()
        binding.etNikAnak.text!!.clear()
        binding.etTglAnak.text!!.clear()
        binding.etUmurAnak.text!!.clear()
        binding.etJkAnak.text!!.clear()
        binding.etTinggiAnak.text!!.clear()
        binding.etNamaOrtuAnak.text!!.clear()
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

    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbAnak)
        supportActionBar!!.title = "Layanan Anak"
        // Change font style text
        binding.tbAnak.setTitleTextAppearance(this, R.style.Theme_Stunting)
        // Enable back button if you're in a child activity
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.tbAnak.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}