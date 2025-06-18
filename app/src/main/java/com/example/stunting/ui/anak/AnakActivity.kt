package com.example.stunting.ui.anak

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.stunting.R
import com.example.stunting.ResultState
import com.example.stunting.adapter.AnakAdapter
import com.example.stunting.database.no_api.anak.AnakDao
import com.example.stunting.database.no_api.anak.AnakEntity
import com.example.stunting.databinding.ActivityAnakBinding
import com.example.stunting.databinding.DialogBottomSheetAnakBinding
import com.example.stunting.databinding.DialogCustomDeleteBinding
import com.example.stunting.databinding.DialogCustomExportDataBinding
import com.example.stunting.utils.Functions.getDatePickerDialogTglLahir
import com.example.stunting.utils.Functions.getDateTimePrimaryKey
import com.example.stunting.utils.Functions.linkToDirectory
import com.example.stunting.utils.Functions.setCalendarTglLahir
import com.example.stunting.utils.Functions.showCustomeInfoDialog
import com.example.stunting.utils.Functions.toastInfo
import com.example.stunting.ml.ModelRegularizerCategorical
import com.example.stunting.ui.MainActivity
import com.example.stunting.ui.MainActivity.Companion.EXTRA_FRAGMENT_TO_MAIN_ACTIVITY
import com.example.stunting.ui.ViewModelFactory
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import www.sanju.motiontoast.MotionToastStyle
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.abs


class AnakActivity : AppCompatActivity() {
    private var _binding: ActivityAnakBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AnakViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var _bindingAnakBottomSheetDialog: DialogBottomSheetAnakBinding? = null
    private val bindingAnakBottomSheetDialog get() = _bindingAnakBottomSheetDialog!!

    private var userId: Int? = null
    private var categoriServiceId: Int? = null

    private var countItem = 0
    private var classification = "NORMAL"
    private var jkOutput = "Laki-laki"
    private var namaAnak = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityAnakBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        userId = intent?.getIntExtra(EXTRA_USER_ID_TO_ANAK_ACTIVITY, 0)!!
        categoriServiceId = intent?.getIntExtra(EXTRA_CATEGORY_SERVICE_ID_TO_ANAK_ACTIVITY, 0)

        setToolBar()
        collapsedHandlerToolbar()

        // Binding AnakBottomSheetDialog for retrieve xml id
        _bindingAnakBottomSheetDialog = DialogBottomSheetAnakBinding.inflate(layoutInflater)
        bindingAnakBottomSheetDialog.tvDataAnak.text = getString(R.string.list_data_anak)

        // Set caledar and update in view result
        setCalendarTglLahir(binding.etTglLahirAnak)

        binding.etTglLahirAnak.setOnClickListener {
            getDatePickerDialogTglLahir(this@AnakActivity)
        }

        binding.btnTampilDataAnak.setOnClickListener {
            // Data not empty
//            Log.e("CEK DATANA", countItem.toString())
            if (countItem != 0) showBottomSheetDialog() else
                toastInfo(
                    this@AnakActivity,getString(R.string.title_show_data_failed),
                    getString(R.string.description_show_data_failed), MotionToastStyle.ERROR
                )
        }

//        // Get all items
//        getAll(anakDao)
        getChecksFromApi()
        getChildrenPatientsFromApi()
        setInputTextUmur()
        btnSubmit()
    }

    private fun getChecksFromApi() {
        viewModel.getChecksFromApiResult.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> { }
                    is ResultState.Error -> {
//                        Log.d(TAG, "onAnakActivity from LoginFragment getChecksFromApi error : ${result.error}")
                    }
                    is ResultState.Success -> {
                        viewModel.getPregnantMomServiceFromApi()
//                        Log.d(TAG, "onAnakActivity from LoginFragment getChecksFromApi success : ${result.data}")
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun btnSubmit() {
        binding.btnSubmitAnak.setOnClickListener {
            val jk = binding.etJkAnak.text.toString()
            val nik = binding.etNikAnak.text.toString()
            val tglLahir = binding.etTglLahirAnak.text.toString()
            val umur = binding.etUmurAnak.text.toString()
            val tinggi = binding.etTinggiAnak.text.toString()

            // Check for rename 0 for male and 1 for female
            if (jk == "0") jkOutput = "laki-laki" else jkOutput = "perempuan"

            if (namaAnak.isNotEmpty() && nik.isNotEmpty() && tglLahir.isNotEmpty() &&
                umur.isNotEmpty() && jk.isNotEmpty() && tinggi.isNotEmpty()) {

                // Checking if there is no contains 0 (laki-laki) or 1 (perempuan)
                if (jk.contains("0") or jk.contains("1")) {
                    // Get data for predictions
                    val umurPred = umur.toFloat()
                    val jkPred = jk.toFloat()
                    val tinggiPred = tinggi.toFloat()
                    prediction(namaAnak, jk, nik, tglLahir, umur, tinggi, umurPred, jkPred, tinggiPred)
                } else toastInfo(
                    this@AnakActivity, getString(R.string.title_code_gender_not_valid),
                    getString(R.string.description_code_gender_not_valid), MotionToastStyle.ERROR
                )
            } else toastInfo(
                this@AnakActivity, getString(R.string.title_input_failed),
                getString(R.string.description_input_failed), MotionToastStyle.ERROR
            )
        }
    }

    private fun getChildrenPatientsFromApi() {
        val progressBar = SweetAlertDialog(this@AnakActivity, SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        viewModel.getChildrenPatientsFromApiResult.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> progressBar.show()
                    is ResultState.Error -> {
                        progressBar.dismiss()
//                        Log.d(TAG, "onAnakActivity getChildrenPatientsFromApi : ${result.error}")
                    }
                    is ResultState.Success -> {
                        spinnerNamaAnak()
                        progressBar.dismiss()
//                        Log.d(TAG, "onAnakActivity getChildrenPatientsFromApi : ${result.data}")
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(this@AnakActivity, MainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun spinnerNamaAnak() {
        val items = mutableListOf("Nama Anak")

        viewModel.getChildrenPatientsFromLocal().observe(this) { childrenPatientsList ->
            childrenPatientsList.forEach { item ->
                items.add(item.namaAnak.toString())
            }
        }

        val adapter = object: ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items) {
            override fun isEnabled(position: Int): Boolean {
                // Item di posisi 0 tidak bisa dipilih
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView

                if (position == 0) {
                    textView.setTextColor(getColor(R.color.buttonDisabledColor))
                }
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.sNamaAnak.adapter = adapter

        binding.sNamaAnak.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    namaAnak = parent?.getItemAtPosition(position).toString()
                    getChildrenPatientByNamaAnak(namaAnak)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
    }

    private fun getChildrenPatientByNamaAnak(namaAnak: String) {
        viewModel.getChildrenPatientByNamaAnak(namaAnak).observe(this) { childrenPatientEntity ->
            binding.etJkAnak.setText(convertJenisKelaminToNumeric(childrenPatientEntity.jenisKelaminAnak!!))
            binding.etNikAnak.setText(childrenPatientEntity.nikAnak)
            binding.etTglLahirAnak.setText(convertDateFormat(childrenPatientEntity.tglLahirAnak!!))
            binding.etUmurAnak.setText(childrenPatientEntity.umurAnak)
        }
    }

    private fun convertDateFormat(dateFormat: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        // hasil: 2025/06/17
        val outputFormat = SimpleDateFormat("yyyy/MM/dd", Locale("id"))
        val date = inputFormat.parse(dateFormat)
        val formattedDate = outputFormat.format(date)
        return formattedDate
    }

    private fun convertJenisKelaminToNumeric(jenisKelamin: String): String {
        var resultConvert = ""
        when (jenisKelamin) {
            "laki-laki" -> resultConvert = "0"
            "perempuan" -> resultConvert = "0"
        }
        return resultConvert
    }

    private fun setInputTextUmur() {
        // Ketika tiap sentuh inputText, inputText umur akan terupdate
        binding.etTglLahirAnak.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                val tglLahir = s.toString()
                // Log format untuk debugging
//                Log.e("TEST FORMAT", tglLahir)

                // Pastikan umur tidak kosong sebelum mencoba menghitung umur
                if (tglLahir.isNotEmpty()) {
                    val umur = calculateAge(tglLahir)
                    binding.etUmurAnak.setText(umur.toString())
                } else {
                    // Clear umur jika input tanggal kosong
                    binding.etUmurAnak.setText("")
                }
            }
        })
    }

    private fun calculateAge(birthDateString: String): Int {
        // Format tanggal (ubah format jika perlu)
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val birthDate = LocalDate.parse(birthDateString, formatter)

        // Tanggal hari ini
        val today = LocalDate.now()

        // Hitung umur
        val period = Period.between(birthDate, today)
//        Log.e("TEST RESULT PERIOD ", period.toString())

        val totalDays = period.days + period.months * 30 + period.years * 365
        Log.e("TEST TOTAL DAYS", totalDays.toString())

        // Hitung dalam minggu
        val weeksPregnant = totalDays / 7
        val monthsPregnant = weeksPregnant / 4 // Hitung dalam bulan
        return monthsPregnant
    }

    private fun collapsedHandlerToolbar() {
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            if (abs(verticalOffset.toDouble()) >= totalScrollRange) {
                // Jika CollapsingToolbarLayout sudah collapsed, tampilkan judul
                binding.collapsingToolbarLayout.title = getString(R.string.app_name_layanan_anak)
            } else {
                // Jika masih expanded, sembunyikan judul
                binding.collapsingToolbarLayout.title = ""
            }
        }
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

        bindingAnakBottomSheetDialog.apply {
            ivDelete.setOnClickListener { showCustomeDeleteDialog() }
            ivExportToXlsx.setOnClickListener { showCustomeExportDataDialog() }
            ivArrow.setOnClickListener { showCustomeInfoDialog(this@AnakActivity, layoutInflater) }
            tvInfo.setOnClickListener { showCustomeInfoDialog(this@AnakActivity, layoutInflater) }
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
                    this@AnakActivity, getString(R.string.title_export_success),
                    getString(R.string.description_export_success), MotionToastStyle.SUCCESS
                )
//                exportDatabaseToCSV(this@AnakActivity, anakDao, NAME)
            }
            exportDialog.dismiss()
            // Goto link directory download
            linkToDirectory(this@AnakActivity)
            // Destroying when exported successfully
            finish()
        }
        viewExport.tvNo.setOnClickListener {
            exportDialog.dismiss()
        }
        exportDialog.show()
    }

    private fun showCustomeDeleteDialog() {
        val viewDelete = DialogCustomDeleteBinding.inflate(layoutInflater)
        val deleteDialog = Dialog(this)

        deleteDialog.setContentView(viewDelete.root)
        deleteDialog.setCanceledOnTouchOutside(false)
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        viewDelete.tvDescription.text = getString(R.string.description_delete_dialog)
        viewDelete.tvYes.setOnClickListener {
//            deleteAllDates(anakDao)
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
        toastInfo(
            this@AnakActivity,getString(R.string.title_history_delete),
            getString(R.string.description_history_delete), MotionToastStyle.SUCCESS
        )
    }

    private fun setupListOfDataIntoRecyclerView(anakList: ArrayList<AnakEntity>) {
        if (anakList.isNotEmpty()) {
            val anakAdapter = AnakAdapter(anakList)
            // Count item list
            countItem = anakList.size
            bindingAnakBottomSheetDialog.tvTotalData.text = getString(R.string.setup_recycler_view_sum_data, countItem.toString())
            bindingAnakBottomSheetDialog.rvBottomSheetAnak.layoutManager = LinearLayoutManager(this)
            bindingAnakBottomSheetDialog.rvBottomSheetAnak.adapter = anakAdapter
            // To scrolling automatic when data entered
            bindingAnakBottomSheetDialog.rvBottomSheetAnak.smoothScrollToPosition(countItem - 1)

            // When input data automatically to last index
            bindingAnakBottomSheetDialog.rvBottomSheetAnak
                .layoutManager!!.smoothScrollToPosition(bindingAnakBottomSheetDialog
                    .rvBottomSheetAnak, null, countItem - 1)
        }
//        Log.e("HASILNA", anakList.toString())
    }

    @SuppressLint("SimpleDateFormat")
    suspend fun exportDatabaseToCSV(activity: Activity, anakDao: AnakDao, name: String) {
        // Output variabel fileDir is => /storage/emulated/0/Android/data/com.example.stunting/files/Download
        // val fileDir = "${getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/"

        val dataResult: ArrayList<List<String>> = ArrayList()
        val fileName = "$name${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())}.csv"
        val fileDir = "/storage/emulated/0/Download/"
        try {
            val file = File(fileDir, fileName)
            dataResult.add(
                listOf("tanggal", "Nama Anak", "Jenis Kelamin", "NIK", "Tanggal Lahir", "Umur",
                    "Tinggi Badan (cm)", "Nama Orang Tua", "Hasil", "Pencegahan Stunting")
            )
            anakDao.fetchAllAnak().collect {
                for (item in it) {
                    dataResult.add(
                        listOf(
                            item.tanggal, item.namaAnak, item.jkAnak,
                            item.nikAnak, item.tglLahirAnak, item.umurAnak,
                            item.tinggiAnak, item.ortuAnak, item.klasifikasiAnak, item.pencegahanStunting
                        )
                    )
                    csvWriter().writeAll(dataResult, file.outputStream())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            toastInfo(activity, activity.applicationContext.getString(R.string.title_export_failed),
                activity.applicationContext.getString(R.string.description_export_failed), MotionToastStyle.ERROR)
        }
    }

    private fun prediction(
        namaAnak: String, jk: String, nik: String, tglLahir: String,
        umur: String, tinggi: String, umurFloat: Float, jkFloat: Float, tinggiFloat: Float
    ) {
        // Normalisasi data with formula => (Xi - Xmin) / (Xmax - Xmin)
        val umurNormalized = (umurFloat - 0f) / (60f - 0f)
        val tinggiNormalized = (tinggiFloat - 40.01f) / (128f - 40.0104370037594f)

        try { // this is apropriated by input and output value, 3 * 4 It depends on memory allocated
            val byteBuffer = ByteBuffer.allocateDirect(3 * 4)
            byteBuffer.order(ByteOrder.nativeOrder())
            byteBuffer.putFloat(umurNormalized)
            byteBuffer.putFloat(jkFloat)
            byteBuffer.putFloat(tinggiNormalized)

            val model = ModelRegularizerCategorical.newInstance(this@AnakActivity)

            // Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(byteBuffer)

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray
            val categoryList = outputFeature0

            // Result 0 : Normal, 1 : Stunting Kurus, 2 : Stunting, 3 : Stunting Tinggi
            var maxIndex = 0
            var maxValue = categoryList[0]
            for (i in categoryList.indices) {
                if (categoryList[i] > maxValue) {
                    maxValue = categoryList[i]
                    maxIndex = i
                }
            }

            if (maxIndex == 0) {
                classification = getString(R.string.classification_normal)
                // Add record
                addRecord(namaAnak, nik, tglLahir, umur, jk, tinggi, classification, getString(R.string.pencegahan_stunting_tidak_ada))
                sweetAlertDialog(maxIndex, SweetAlertDialog.SUCCESS_TYPE)
            } else if (maxIndex == 1) {
                classification = getString(R.string.classification_stunting_kurus)
                // Add record
                addRecord(namaAnak, nik, tglLahir, umur, jk, tinggi, classification, getString(R.string.pencegahan_stunting_kurus))
                sweetAlertDialog(maxIndex, SweetAlertDialog.WARNING_TYPE)
            } else if (maxIndex == 2) {
                classification = getString(R.string.classification_stunting)
                // Add record
                addRecord(namaAnak, nik, tglLahir, umur, jk, tinggi, classification, getString(R.string.pencegahan_stunting))
                sweetAlertDialog(maxIndex, SweetAlertDialog.WARNING_TYPE)
            } else {
                classification = getString(R.string.classification_stunting_tinggi)
                // Add record
                addRecord(namaAnak, nik, tglLahir, umur, jk, tinggi, classification, getString(R.string.pencegahan_stunting_tinggi))
                sweetAlertDialog(maxIndex, SweetAlertDialog.WARNING_TYPE)
            }
            // Releases model resources if no longer used.
            model.close()
        } catch (e: Exception) {
            e.printStackTrace()
            toastInfo(
                this@AnakActivity, getString(R.string.title_export_failed),
                getString(R.string.description_export_failed), MotionToastStyle.ERROR
            )
        }
    }

    private fun sweetAlertDialog(conditionStunting: Int, type: Int) {
        if (conditionStunting == 0 && type == 2) {
            // Normal
            val sweetAlertDialog = SweetAlertDialog(this@AnakActivity, type)
            sweetAlertDialog.setTitleText(getString(R.string.title_pencegahan_stunting_tidak_ada))
            sweetAlertDialog.setContentText(getString(R.string.pencegahan_stunting_tidak_ada))
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.show()
        } else if (conditionStunting == 1 && type == 3) {
            // Warning stunting kurus
            val sweetAlertDialog = SweetAlertDialog(this@AnakActivity, type)
            sweetAlertDialog.setTitleText(getString(R.string.title_pencegahan_stunting_kurus))
            sweetAlertDialog.setContentText(getString(R.string.pencegahan_stunting_kurus))
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.show()
        } else if (conditionStunting == 2 && type == 3) {
            // Warning stunting normal
            val sweetAlertDialog = SweetAlertDialog(this@AnakActivity, type)
            sweetAlertDialog.setTitleText(getString(R.string.title_pencegahan_stunting))
            sweetAlertDialog.setContentText(getString(R.string.pencegahan_stunting))
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.show()
        } else {
            // Warning stunting tinggi
            val sweetAlertDialog = SweetAlertDialog(this@AnakActivity, type)
            sweetAlertDialog.setTitleText(getString(R.string.title_pencegahan_stunting_tinggi))
            sweetAlertDialog.setContentText(getString(R.string.pencegahan_stunting_tinggi))
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.show()
        }
    }

    private fun addRecord(
        namaAnak: String, nik: String, tglLahir: String, umur: String,
        jk: String, tinggi: String, hasil: String, pencegahanStunting: String
    ) {
        val date = getDateTimePrimaryKey()


//        lifecycleScope.launch {
//            anakDao.insert(
//                AnakEntity(
//                    tanggal = date,
//                    namaAnak = nama,
//                    nikAnak = nik,
//                    tglLahirAnak = tglLahir,
//                    umurAnak = umur,
//                    jkAnak = jk,
//                    tinggiAnak = tinggi,
//                    ortuAnak = namaOrtu,
//                    klasifikasiAnak = hasil,
//                    pencegahanStunting = pencegahanStunting
//                )
//            )
//        }
        toastInfo(
            this@AnakActivity, getString(R.string.title_saved_data),
            getString(R.string.description_saved_data), MotionToastStyle.SUCCESS
        )

        // Clear the text when data saved !!! (success)
//        binding.etNamaAnak.text!!.clear()
//        binding.etNikAnak.text!!.clear()
        binding.etTglLahirAnak.text!!.clear()
        binding.etUmurAnak.text!!.clear()
        binding.etJkAnak.text!!.clear()
        binding.etTinggiAnak.text!!.clear()
//        binding.etNamaOrtuAnak.text!!.clear()
    }

    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbAnak)
        supportActionBar!!.title = getString(R.string.app_name_layanan_anak)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _bindingAnakBottomSheetDialog = null
//        _anakDao = null
    }

    companion object {
        private val TAG = AnakActivity::class.java.simpleName
        const val EXTRA_USER_ID_TO_ANAK_ACTIVITY = "extra_user_id_to_anak_activity"
        const val EXTRA_CATEGORY_SERVICE_ID_TO_ANAK_ACTIVITY = "extra_category_service_id_to_anak_activity"
        private const val NAME = "anak_data_"
    }
}