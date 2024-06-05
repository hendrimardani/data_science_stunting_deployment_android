package com.example.stunting

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stunting.Database.BabyApp
import com.example.stunting.Database.BabyDao
import com.example.stunting.Database.BabyEntity
import com.example.stunting.databinding.ActivityMainBinding
import com.example.stunting.databinding.DialogCustomAboutBinding
import com.example.stunting.databinding.DialogCustomDeleteBinding
import com.example.stunting.databinding.DialogCustomExportDataBinding
import com.example.stunting.ml.ModelRegularizer
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.coroutines.launch
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var babyDao: BabyDao

    companion object Outputs {
        // For result output to display
        var classification = "NORMAL"
        var jk = "Laki-laki"
        var umur = ""
        var tinggi =""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Call database
        babyDao = (application as BabyApp).db.babyDao()

        setToolBar()

        binding.btnSubmit.setOnClickListener {
            val tanggal = addDateToDatabase()
            val umur = binding.etUmur.text.toString()
            val jk = binding.etJk.text.toString()
            val tinggi = binding.etTinggi.text.toString()

            // Check for rename 0 for male and 1 for female
            if (jk == "0") Outputs.jk = "Laki-laki" else Outputs.jk = "Perempuan"

            if (umur.isNotEmpty() && jk.isNotEmpty() && tinggi.isNotEmpty() ) {

                // Checking if there is no contains 0 (laki-laki) or 1 (perempuan)
                if (jk.contains("0") or jk.contains("1")) {
                    // Get data
                    val umurPred = umur.toFloat()
                    val jkPred = jk.toFloat()
                    val tinggiPred = tinggi.toFloat()

                    // For result to int and display it.
                    Outputs.umur = umur
                    Outputs.tinggi = tinggi

                    // Prediction
                    prediction(babyDao, tanggal, Outputs.umur, umurPred, jkPred,  tinggiPred)
                } else {
                    toastInfo("Data Jenis Kelamin Tidak Ada", "Silahkan masukkan kode jenis kelamin yang sesuai", MotionToastStyle.ERROR)
                }
            } else {
                toastInfo("Gagal ☹️",
                    "Tidak boleh ada data yang kosong !", MotionToastStyle.ERROR)
            }
        }

        binding.ivDelete.setOnClickListener {
            // Delete all items
            customDialogDeleteAll(babyDao)
        }

        // Get all items
        getAll(babyDao)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_about -> {
                // About
                customDialogAbout()
            }
            R.id.menu_export_to_csv -> {
                // Export database to CSV
                customDialogExportData(babyDao, "csv")
            }
            R.id.menu_export_to_xlsx -> {
                // Export database to XLS
                customDialogExportData(babyDao, "xlsx")
            }
            R.id.menu_mencegah_stunting -> {
                // Mencegah stunting
                stuntingPreventInfo()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbMain)
        supportActionBar!!.title = "Stunting Prediksi"
        // Set icon
        supportActionBar!!.setIcon(R.drawable.icon_stunting)
        // Change font style text
        binding.tbMain.setTitleTextAppearance(this, R.style.Theme_Stunting)
    }

    private fun stuntingPreventInfo() {
        val intent = Intent(this, StuntingPreventActivity::class.java)
        startActivity(intent)
    }

    private suspend fun exportDatabaseToCSV(babyDao: BabyDao) {
        // Output variabel fileDir is => /storage/emulated/0/Android/data/com.example.stunting/files/Download
        // val fileDir = "${getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/"

        var dataResult: ArrayList<List<String>> = ArrayList()
        val fileName = "data_user_${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())}.csv"
        val fileDir = "/storage/emulated/0/Download/"
        try {
            val file = File(fileDir, fileName)
            dataResult.add(
                listOf("tanggal", "umur(bulan)", "jenis_kelamin", "tinggi(cm)", "hasil")
            )
            babyDao.fetchAllbabies().collect {
                for (item in it) {
                    dataResult.add(
                        listOf("${item.tanggal}", "${item.umur}", "${item.jenisKelamin}", "${item.tinggi}", "${item.klasifikasi}")
                    )
                    csvWriter().writeAll(dataResult, file.outputStream())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            toastInfo("Gagal Mengekspor File !", "Silahkan coba lagi !, atau jika mengalami kendala hubungi pembuat aplikasi !", MotionToastStyle.ERROR)
        }
    }

    private suspend fun exportDatabaseToExcel(babyDao: BabyDao) {
        // Output variabel fileDir is => /storage/emulated/0/Android/data/com.example.stunting/files/Download
        // val fileDir = "${getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/"

        val fileName = "data_user_${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())}.xlsx"
        val fileDir = "/storage/emulated/0/Download/"

        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Data")

        try {
            val file = File(fileDir, fileName)
            // Buat header tabel
            val headerRow = sheet.createRow(0)
            headerRow.createCell(0).setCellValue("tanggal")
            headerRow.createCell(1).setCellValue("umur(bulan)")
            headerRow.createCell(2).setCellValue("jenis_kelamin")
            headerRow.createCell(3).setCellValue("tinggi(cm)")
            headerRow.createCell(4).setCellValue("hasil")

            // Isi data ke dalam tabel
            var rowNum = 1
            babyDao.fetchAllbabies().collect {
                for (item in it) {
                    val row = sheet.createRow(rowNum++)
                    row.createCell(0).setCellValue(item.tanggal)
                    row.createCell(1).setCellValue(item.umur)
                    row.createCell(2).setCellValue(item.jenisKelamin)
                    row.createCell(3).setCellValue(item.tinggi)
                    row.createCell(4).setCellValue(item.klasifikasi)

                    val fileOutputStream = FileOutputStream(file)
                    workbook.write(fileOutputStream)
                    fileOutputStream.close()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            toastInfo("Gagal Mengekspor File !", "Silahkan coba lagi !, atau jika mengalami kendala hubungi pembuat aplikasi !", MotionToastStyle.ERROR)
        }
    }

    private fun addDateToDatabase(): String {
        val c = Calendar.getInstance()
        val dateTime = c.time

        // 10-03-2024 Min 14:59:11
        val sdf = SimpleDateFormat("dd-MM-yyyy EEE HH:mm:ss", Locale.getDefault())
        val date = sdf.format(dateTime)
        return date
    }

    private fun getAll(babyDao: BabyDao) {
        lifecycleScope.launch {
            babyDao.fetchAllbabies().collect {
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list)
            }
        }
    }

    private fun setupListOfDataIntoRecyclerView(babyList: ArrayList<BabyEntity>) {
        val dataNormal: ArrayList<String> = ArrayList()
        val dataStunting: ArrayList<String> = ArrayList()

        if (babyList.isNotEmpty()) {
            binding.llHeader.visibility = View.VISIBLE
            binding.ivDelete.visibility = View.VISIBLE
            val mainAdapter = MainAdapter(babyList)

            // Count item list
            val countItem = babyList.size
            binding.tvTotalData.text = "Total ${countItem} data"

            // Count classification data
            babyList.forEach {
                if (it.klasifikasi == "NORMAL") dataNormal.add(it.klasifikasi)
                else dataStunting.add(it.klasifikasi)
            }

            // Display output data
            binding.tvNumberNormalData.text = "${dataNormal.size} data"
            binding.tvNumberStuntingData.text = "${dataStunting.size} data"


            binding.rvItemList.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
            binding.rvItemList.adapter = mainAdapter

            // When input data automatically to last index
            binding.rvItemList.layoutManager!!.smoothScrollToPosition(binding.rvItemList, null, countItem - 1)
        } else {
            binding.llHeader.visibility = View.INVISIBLE
            binding.ivDelete.visibility = View.INVISIBLE
        }
    }

    @SuppressLint("SuspiciousIndentation", "ResourceAsColor")
    private fun prediction(
        babyDao: BabyDao,
        tanggal: String,
        umurOutput: String,
        umur: Float,
        jk: Float,
        tinggi: Float
    ) {
        // Normalisasi data with formula => (Xi - Xmin) / (Xmax - Xmin)
        val umurNormalized = (umur - 0f) / (60f - 0f)
        val tinggiNormalized = (tinggi - 40.01f) / (128f - 40.0104370037594f)

        try { // this is apropriated by input and output value, 3 * 4 It depends on memory allocated
            val byteBuffer = ByteBuffer.allocateDirect(3 * 4)
                byteBuffer.order(ByteOrder.nativeOrder())
            byteBuffer.putFloat(umurNormalized)
            byteBuffer.putFloat(jk)
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
            if ( Math.round(outputFeature0[0]) < 0.5 ) {
                Outputs.classification = "NORMAL"

                // Add record
                addRecord(babyDao, tanggal, umurOutput, Outputs.jk, Outputs.classification)
            } else {
                Outputs.classification = "STUNTING"
                // Add record
                addRecord(babyDao, tanggal, umurOutput, Outputs.jk, Outputs.classification)
            }
            // Releases model resources if no longer used.
            model.close()
        } catch (e: Exception) {
            e.printStackTrace()
            toastInfo("Gagal Mengekspor File !", "Silahkan coba lagi !, atau jika mengalami kendala hubungi pembuat aplikasi !", MotionToastStyle.ERROR)
        }
    }

    private fun addRecord(babyDao: BabyDao, tanggal: String, umur: String, jk: String, klasifikasi: String) {
        lifecycleScope.launch {
            babyDao.insert(
                BabyEntity(
                    tanggal = tanggal,
                    umur = umur,
                    jenisKelamin = jk,
                    tinggi = Outputs.tinggi,
                    klasifikasi = klasifikasi
                )
            )
        }
        toastInfo("DATA TERSIMPAN !", "Data berhasil di simpan di database !!", MotionToastStyle.SUCCESS)

        // Clear the text when data saved !!! (success)
        binding.etUmur.text!!.clear()
        binding.etJk.text!!.clear()
        binding.etTinggi.text!!.clear()
    }

    private fun customDialogAbout() {
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomAboutBinding.inflate(layoutInflater)

        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set text
        dialogBinding.tvDescription.text =
            "Jika anda mengalami permasalah pada aplikasi anda silahkan hubungi pembuat aplikasi " +
                    "dibawah ini, atau jika ingin ada yang ditanyakan karena proses pembuatan aplikasi" +
                    " ini masih tahap percobaan dan belum sebenuhnya sempurna.\nSilahkan hubungi Kotak dibawah ini :"

        // Link to linkedin
        dialogBinding.ivLinkedin.setOnClickListener {
            val url = "https://www.linkedin.com/in/hendri-mardani-1b6ba61a8/"
            linkToWebBrowser(url)
        }

        // Link to WA
        dialogBinding.ivWa.setOnClickListener {
            val url = "https://api.whatsapp.com/send?phone=6281388372075"
            linkToWebBrowser(url)
        }

        // Link to Email
        dialogBinding.ivEmail.setOnClickListener {
            val url = "https://mail.google.com/mail/u/0/#inbox?compose=DmwnWrRvxmkxPlXNnrdQHFnTpMFwwGQFsRbBzClXRqRrMbKBBgxdXmgPxVTVNKmFGBHJTdgpsnsV"
            linkToWebBrowser(url)
        }

        // Link to Instagral
        dialogBinding.ivIg.setOnClickListener {
            val url = "https://www.instagram.com/hendri.mardani/"
            linkToWebBrowser(url)
        }

        dialogBinding.tvOk.setOnClickListener {
           // Close
            customDialog.dismiss()
        }
        // Display dialog
        customDialog.show()
    }

    private fun linkToWebBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun customDialogExportData(babyDao: BabyDao, fileType: String) {
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomExportDataBinding.inflate(layoutInflater)

        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (fileType == "csv") {
            // File type CSV
            // Set text
            dialogBinding.tvDescription.text = "Apakah anda yakin ingin mengeksport data ke CSV pada " +
                    "${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())} ? " +
                    "Untuk melihat hasilnya silahkan cek dengan format data_user_(tahunbulantanggal_jammenitdetik).csv di folder Download/Unduhan" +
                    " hari ini Cth: data_user_${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())}.csv"

            dialogBinding.tvYes.setOnClickListener {
                // Export to CSV
                lifecycleScope.launch {
                    toastInfo("DATA BERHASIL DI EKSPOR", "Silahkan cek di folder Download atau Unduhan penyimpanan internal anda !", MotionToastStyle.SUCCESS)
                    exportDatabaseToCSV(babyDao)
                }
                customDialog.dismiss()
                // Goto link directory download
                linkToDirectory()
                // Destroying when exported successfully
                finish()
            }
            dialogBinding.tvNo.setOnClickListener {
                customDialog.dismiss()
            }
            // Display dialog
            customDialog.show()
        } else {
            // File type excel
            // Set text
            dialogBinding.tvDescription.text = "Apakah anda yakin ingin mengeksport data ke Excel pada " +
                    "${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())} ? " +
                    "Untuk melihat hasilnya silahkan cek dengan format data_user_(tahunbulantanggal_jammenitdetik).xlsx di folder Download/Unduhan" +
                    " hari ini Cth: data_user_${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())}.xlsx"

            dialogBinding.tvYes.setOnClickListener {
                // Export to Xlsx
                lifecycleScope.launch {
                    toastInfo("DATA BERHASIL DI EKSPOR", "Silahkan cek di folder Download atau Unduhan penyimpanan internal anda !", MotionToastStyle.SUCCESS)
                    exportDatabaseToExcel(babyDao)
                }
                customDialog.dismiss()
                // Goto link directory download
                linkToDirectory()
                // Destroying when exported successfully
                finish()
            }
            dialogBinding.tvNo.setOnClickListener {
                customDialog.dismiss()
            }
            // Display dialog
            customDialog.show()
        }
    }

    private fun deleteAllDates(babyDao: BabyDao) {
        lifecycleScope.launch {
            babyDao.deleteAll()
        }
        toastInfo("HISTORI BERHASIL DIHAPUS SEMUA", "Silahkan jalankan kembali aplikasinya.", MotionToastStyle.SUCCESS)
    }

    private fun linkToDirectory() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, 101) // Replace REQUEST_CODE with a unique code (free numeric)
    }

    private fun customDialogDeleteAll(babyDao: BabyDao) {
        val customDialog = Dialog(this@MainActivity)
        val dialogBinding = DialogCustomDeleteBinding.inflate(layoutInflater)

        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        // Set text
        dialogBinding.tvDescription.text = "Apakah anda yakin ingin menghapus semua histori ?, " +
                "Jika anda belum membackup histori datanya silahkan export dulu !! Untuk eksport ada " +
                "di pojok kanan atas."

        dialogBinding.tvYes.setOnClickListener {
            deleteAllDates(babyDao)
            // We will destroy activity
            finish()
            customDialog.dismiss()
        }
        dialogBinding.tvNo.setOnClickListener {
            customDialog.dismiss()
        }
        // Display dialog
        customDialog.show()
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
}