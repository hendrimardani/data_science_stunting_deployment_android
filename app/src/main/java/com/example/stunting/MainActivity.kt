package com.example.stunting

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stunting.Database.BabyApp
import com.example.stunting.Database.BabyDao
import com.example.stunting.Database.BabyEntity
import com.example.stunting.databinding.ActivityMainBinding
import com.example.stunting.databinding.DialogCustomeExportDataBinding
import com.example.stunting.ml.ModelStunting
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
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


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

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
        val babyDao = (application as BabyApp).db.babyDao()

        binding.btnSubmit.setOnClickListener {
            val tanggal = addDateToDatabase()
            val umur = binding.etUmur.text.toString()
            val jk = binding.etJk.text.toString()
            val tinggi = binding.etTinggi.text.toString()

            // Check for rename 0 for male and 1 for female
            if (jk == "0") Outputs.jk = "Laki-laki" else Outputs.jk = "Perempuan"

            if (umur.isNotEmpty() && jk.isNotEmpty() && tinggi.isNotEmpty() ) {
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
                toastInfo("Gagal ☹️",
                    "Tidak boleh ada data yang kosong !", MotionToastStyle.ERROR)
            }
        }

        binding.btnExport.setOnClickListener {
            customDialog(babyDao)
        }

        // Get all items
        getAll(babyDao)
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

    private fun addDateToDatabase(): String {
        val c = Calendar.getInstance()
        val dateTime =c.time

        // 10-03-2024 Min 14:59:11
        val sdf =SimpleDateFormat("dd-MM-yyyy EEE HH:mm:ss", Locale.getDefault())
        val date =sdf.format(dateTime)
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
        if (babyList.isNotEmpty()) {
            val mainAdapter = MainAdapter(babyList)

            // Count item list
            val countItem = babyList.size

            binding.rvItemList.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
            binding.rvItemList.adapter = mainAdapter
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

            val model = ModelStunting.newInstance(this)

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
            toastInfo("DATA TERSIMPAN !", "Data berhasil di simpan di database !!", MotionToastStyle.SUCCESS)

            // Clear the text when data saved !!! (success)
            binding.etUmur.text!!.clear()
            binding.etJk.text!!.clear()
            binding.etTinggi.text!!.clear()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun customDialog(babyDao: BabyDao) {
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomeExportDataBinding.inflate(layoutInflater)

        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set text
        dialogBinding.tvTitle.setTextColor(ContextCompat.getColor(this, R.color.red))
        dialogBinding.tvDescription.text = "Apakah anda yakin ingin mengeksport data ke CSV pada " +
                "${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())} ? " +
                "Untuk melihat hasilnya silahkan cek dengan format data_user_(tahunbulantanggal_jammenitdetik).csv" +
                "hari ini Cth: data_user_2024Mei24_005247.csv"

        dialogBinding.tvYes.setOnClickListener {
            // Export to CSV
            lifecycleScope.launch {
                toastInfo("DATA BERHASIL DI EKSPOR", "Silahkan cek di folder Download atau Unduhan !", MotionToastStyle.SUCCESS)
                exportDatabaseToCSV(babyDao)
            }
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