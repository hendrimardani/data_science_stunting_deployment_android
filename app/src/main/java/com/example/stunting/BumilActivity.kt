package com.example.stunting

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stunting.Adapter.BumilAdapter
import com.example.stunting.Database.Bumil.BumilDao
import com.example.stunting.Database.Bumil.BumilEntity
import com.example.stunting.Database.DatabaseApp
import com.example.stunting.databinding.ActivityBumilBinding
import com.example.stunting.databinding.DialogBottomSheetBumilBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BumilActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityBumilBinding
    private lateinit var bumilDao: BumilDao
    private lateinit var bindingBumilBottomSheetDialog: DialogBottomSheetBumilBinding
    private lateinit var cal: Calendar
    private lateinit var dataSetListenerTgllahir: DatePickerDialog.OnDateSetListener
    private lateinit var dataSetListenerHariPertamaHaidTerakhir: DatePickerDialog.OnDateSetListener
    private lateinit var dataSetListenerTglPerkiraanLahir: DatePickerDialog.OnDateSetListener
    var statusGiziRadioButton = "YA"

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
        // Binding BumilBottomSheetDialog for retrieve xml id
        bindingBumilBottomSheetDialog = DialogBottomSheetBumilBinding.inflate(layoutInflater)

        // Toolbar
        setToolBar()

        // Call database
        bumilDao = (application as DatabaseApp).dbBumilDatabase.bumilDao()

        // Set caledar and update in view result
        setCalendarTglLahir(binding.etTglLahirBumil)
        setCalendarHariPertamaHaidTerakhir(binding.etHariPertamaHaidTerakhirBumil)
        setCalendarTglPerkiraanLahir(binding.etTglPerkiraanLahirBumil)

        binding.etTglLahirBumil.setOnClickListener(this)
        binding.etHariPertamaHaidTerakhirBumil.setOnClickListener(this)
        binding.etTglPerkiraanLahirBumil.setOnClickListener(this)
        binding.rgBumil.setOnClickListener(this)
        binding.btnSubmitBumil.setOnClickListener(this)
        binding.btnTampilData.setOnClickListener(this)

        // Get all items
        getAll(bumilDao)
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
            R.id.rg_bumil -> {
                when (binding.rgBumil.checkedRadioButtonId) {
                    R.id.rb_ya_bumil -> statusGiziRadioButton = "YA"
                    R.id.rb_tidak_bumil -> statusGiziRadioButton = "TIDAK"
                }
            }
            R.id.btn_submit_bumil -> {
                val tanggal = addDateToDatabase()
                val nama = binding.etNamaBumil.text.toString()
                val nik = binding.etNikBumil.text.toString()
                val tglLahir = binding.etTglLahirBumil.text.toString()
                val umur = binding.etUmurBumil.text.toString()
                val hariPertamaHaidTerakhir = binding.etHariPertamaHaidTerakhirBumil.text.toString()
                val tanggalPerkiraanLahir = binding.etTglPerkiraanLahirBumil.text.toString()
                val umurKehamilan = binding.etUmurKehamilanBumil.text.toString()
                val statusGiziKesehatan = statusGiziRadioButton

                addRecord(bumilDao, tanggal, nama, nik, tglLahir, umur,
                    hariPertamaHaidTerakhir, tanggalPerkiraanLahir, umurKehamilan, statusGiziKesehatan)
            }
            R.id.btn_tampil_data -> {
                showBottomSheetDialog()
            }
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


    private fun setupListOfDataIntoRecyclerView(bumilList: ArrayList<BumilEntity>) {

        if (bumilList.isNotEmpty()) {
            val bumilAdapter = BumilAdapter(bumilList)
            // Count item list
            val countItem = bumilList.size

            bindingBumilBottomSheetDialog.rvBottomSheetBumil.layoutManager = LinearLayoutManager(this)
            bindingBumilBottomSheetDialog.rvBottomSheetBumil.adapter = bumilAdapter
            // To scrolling automatic when data entered
            bindingBumilBottomSheetDialog.rvBottomSheetBumil
                .smoothScrollToPosition(countItem - 1)

            // When input data automatically to last index
            bindingBumilBottomSheetDialog.rvBottomSheetBumil
                .layoutManager!!.smoothScrollToPosition(bindingBumilBottomSheetDialog
                    .rvBottomSheetBumil, null, countItem - 1)
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

    private fun addRecord(bumilDao: BumilDao, tanggal: String, nama: String, nik: String, tglLahir: String,
                          umur: String, hariPertamaHaidTerakhir: String, tanggalPerkiraanLahir: String,
                          umurKehamilan: String, statusGiziKesehatan: String) {
        lifecycleScope.launch {
            bumilDao.insert(
                BumilEntity(
                    tanggalBumil = tanggal, namaBumil = nama, nikBumil = nik, tglLahirBumil = tglLahir,
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
    }
}