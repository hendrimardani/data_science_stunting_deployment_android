package com.example.stunting

import android.app.DatePickerDialog
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
import com.example.stunting.Database.Child.AnakDao
import com.example.stunting.Database.Child.AnakEntity
import com.example.stunting.Database.DatabaseApp
import com.example.stunting.databinding.ActivityAnakBinding
import com.example.stunting.databinding.DialogBottomSheetAnakBinding
import com.example.stunting.ml.ModelRegularizer
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

        binding.btnTampilData.setOnClickListener {
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
//            showCustomeDeleteDialog()
        }
        bindingAnakBottomSheetDialog.ivExportToXlsx.setOnClickListener {
//            showCustomeExportDataDialog()
        }
        bindingAnakBottomSheetDialog.ivArrow.setOnClickListener {
//            showCustomeInfoDilog()
        }
        bindingAnakBottomSheetDialog.tvInfo.setOnClickListener {
//            showCustomeInfoDilog()
        }
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