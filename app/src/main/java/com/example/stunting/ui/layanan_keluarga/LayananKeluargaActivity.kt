package com.example.stunting.ui.layanan_keluarga

import LayananKeluargaAdapter
import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.example.stunting.database.no_api.DatabaseApp
import com.example.stunting.database.no_api.layanan_keluarga.LayananKeluargaDao
import com.example.stunting.database.no_api.layanan_keluarga.LayananKeluargaEntity
import com.example.stunting.databinding.ActivityLayananKeluargaBinding
import com.example.stunting.databinding.DialogBottomSheetLayananKeluargaBinding
import com.example.stunting.databinding.DialogCustomDeleteBinding
import com.example.stunting.databinding.DialogCustomExportDataBinding
import com.example.stunting.functions_helper.Functions.getDateTimePrimaryKey
import com.example.stunting.functions_helper.Functions.linkToDirectory
import com.example.stunting.functions_helper.Functions.showCustomeInfoDialog
import com.example.stunting.functions_helper.Functions.toastInfo
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToastStyle
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.abs

class LayananKeluargaActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityLayananKeluargaBinding? = null
    private val binding get() = _binding!!
    private var _bindingLayananKeluargaBottomSheetDialog: DialogBottomSheetLayananKeluargaBinding? = null
    private val bindingLayananKeluargaBottomSheetDialog get() = _bindingLayananKeluargaBottomSheetDialog!!
    private var _layananKeluargaDao: LayananKeluargaDao? = null
    private val layananKeluargaDao get() = _layananKeluargaDao!!

    var countItem = 0
    var kategoriKeluargaRentanRadioButton = "YA"
    var memilikiKartuKeluargaRadioButton = "YA"
    var memilikiJambanSehatRadioButton = "YA"
    var memilikiSumberAirBersihRadioButton = "YA"
    var pesertaJaminanKeshatanRadioButton = "YA"
    var memilikiAksesSanitasiPembuanganLimbahLayakRadioButton = "YA"
    var pendampinganKeluargaOlehTpkRadioButton = "YA"
    var pesertaKegiatanKetahananPanganRadioButton = "YA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityLayananKeluargaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // toolbar
        setToolBar()

        // Collapsed Toolbar
        collapsedHandlerToolbar()

        // Binding BumilBottomSheetDialog for retrieve xml id
        _bindingLayananKeluargaBottomSheetDialog = DialogBottomSheetLayananKeluargaBinding.inflate(layoutInflater)
        bindingLayananKeluargaBottomSheetDialog.tvListDataLayananKeluarga.text = getString(R.string.list_data_layanan_keluarga)

        // Call database
        _layananKeluargaDao = (application as DatabaseApp).dbApp.layananKeluargaDao()

        getRadioButtonValue(R.id.rg_kategori_keluarga_rentan_keluarga, KATEGORI_KELUARGA_RENTAN)
        getRadioButtonValue(R.id.rg_memiliki_kartu_keluarga_keluarga, MEMILIKI_KARTU_KELUARGA)
        getRadioButtonValue(R.id.rg_memiliki_jamban_sehat_keluarga, MEMILIKI_JAMBAN_SEHAT)
        getRadioButtonValue(R.id.rg_memiliki_sumber_air_bersih_keluarga, MEMILIKI_SUMBER_AIR_BERSIH)
        getRadioButtonValue(R.id.rg_peserta_jaminan_kesehatan_keluarga, PESERTA_JAMINANA_KESEHATAN)
        getRadioButtonValue(R.id.rg_memiliki_akses_sanitasi_pembuangan_limbah_layak_keluarga, MEMILIKI_AKSES_SANITAS_PEMBUANGAN_LIMBAH_LAYAK)
        getRadioButtonValue(R.id.rg_pendampingan_keluarga_oleh_tpk_keluarga, PENDAMPINGAN_KELUARGA_OLEH_TPK)
        getRadioButtonValue(R.id.rg_peserta_kegiatan_ketahanan_pangan_keluarga, PESERTA_KEGIATAN_KETAHANAN_PANGAN)

        binding.btnSubmitKeluarga.setOnClickListener(this)
        binding.btnTampilDataKeluarga.setOnClickListener(this)

        // Get all items
        getAll(layananKeluargaDao)
    }

    private fun collapsedHandlerToolbar() {
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            if (abs(verticalOffset.toDouble()) >= totalScrollRange) {
                // Jika CollapsingToolbarLayout sudah collapsed, tampilkan judul
                binding.collapsingToolbarLayout.title = getString(R.string.app_name_layanan_keluarga)
            } else {
                // Jika masih expanded, sembunyikan judul
                binding.collapsingToolbarLayout.title = ""
            }
        }
    }

    private fun getAll(layananKeluargaDao: LayananKeluargaDao) {
        lifecycleScope.launch {
            layananKeluargaDao.fetchAllLayananKeluarga().collect {
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list)
            }
        }
    }

    private fun setupListOfDataIntoRecyclerView(layananKeluargaList: ArrayList<LayananKeluargaEntity>) {

        if (layananKeluargaList.isNotEmpty()) {
            val layananKeluargaAdapter = LayananKeluargaAdapter(layananKeluargaList)
            // Count item list
            countItem = layananKeluargaList.size
            bindingLayananKeluargaBottomSheetDialog.tvTotalData.text = getString(R.string.setup_recycler_view_sum_data, countItem.toString())
            bindingLayananKeluargaBottomSheetDialog.rvBottomSheetLayananKeluarga.layoutManager = LinearLayoutManager(this)
            bindingLayananKeluargaBottomSheetDialog.rvBottomSheetLayananKeluarga.adapter = layananKeluargaAdapter
            // To scrolling automatic when data entered
            bindingLayananKeluargaBottomSheetDialog.rvBottomSheetLayananKeluarga.smoothScrollToPosition(countItem - 1)

            // When input data automatically to last index
            bindingLayananKeluargaBottomSheetDialog.rvBottomSheetLayananKeluarga
                .layoutManager!!.smoothScrollToPosition(bindingLayananKeluargaBottomSheetDialog
                    .rvBottomSheetLayananKeluarga, null, countItem - 1)
        }
//        Log.e("HASILNA", layananKeluargaList.toString())
    }

    private fun getRadioButtonValue(bindingRadioGroup: Int, resultRadioButton: String) {
        val radioGroup = findViewById<RadioGroup>(bindingRadioGroup)
        // Get all the RadioButtons within the RadioGroup
        val radioButtons = radioGroup.childCount

        if (resultRadioButton == KATEGORI_KELUARGA_RENTAN) {
            // Set a listener for each RadioButton
            for (i in 0 until radioButtons) {
                val radioButton = radioGroup.getChildAt(i) as RadioButton
                radioButton.setOnCheckedChangeListener { button, isChecked ->
                    if (isChecked) {
                        // Handle the selected RadioButton
                        val selectedRadioButtonText = button.text.toString()
                        if (selectedRadioButtonText == "YA") {
                            kategoriKeluargaRentanRadioButton = selectedRadioButtonText
                        } else {
                            kategoriKeluargaRentanRadioButton = selectedRadioButtonText
                        }
//                        Log.e("Selected RadioButton:", selectedRadioButtonText)
//                        Log.e("Selected RadioGroup:", resultRadioButton)
                    }
                }
            }
        } else if (resultRadioButton == MEMILIKI_KARTU_KELUARGA) {
            // periksaAnemiaRadioButton
            for (i in 0 until radioButtons) {
                val radioButton = radioGroup.getChildAt(i) as RadioButton
                radioButton.setOnCheckedChangeListener { button, isChecked ->
                    if (isChecked) {
                        // Handle the selected RadioButton
                        val selectedRadioButtonText = button.text.toString()
                        if (selectedRadioButtonText == "YA") {
                            memilikiKartuKeluargaRadioButton = selectedRadioButtonText
                        } else {
                            memilikiKartuKeluargaRadioButton = selectedRadioButtonText
                        }
//                        Log.e("Selected RadioButton:", selectedRadioButtonText)
//                        Log.e("Selected RadioGroup:", resultRadioButton)
                    }
                }
            }
        } else if (resultRadioButton == MEMILIKI_JAMBAN_SEHAT) {
            // hasilPeriksaAnemiaRadioButton
            for (i in 0 until radioButtons) {
                val radioButton = radioGroup.getChildAt(i) as RadioButton
                radioButton.setOnCheckedChangeListener { button, isChecked ->
                    if (isChecked) {
                        // Handle the selected RadioButton
                        val selectedRadioButtonText = button.text.toString()
                        if (selectedRadioButtonText == "YA") {
                            memilikiJambanSehatRadioButton = selectedRadioButtonText
                        } else {
                            memilikiJambanSehatRadioButton = selectedRadioButtonText
                        }
//                        Log.e("Selected RadioButton:", selectedRadioButtonText)
//                        Log.e("Selected RadioGroup:", resultRadioButton)
                    }
                }
            }
        } else if (resultRadioButton == MEMILIKI_SUMBER_AIR_BERSIH) {
            // memilikiSumberAirBersihRadioButton
            for (i in 0 until radioButtons) {
                val radioButton = radioGroup.getChildAt(i) as RadioButton
                radioButton.setOnCheckedChangeListener { button, isChecked ->
                    if (isChecked) {
                        // Handle the selected RadioButton
                        val selectedRadioButtonText = button.text.toString()
                        if (selectedRadioButtonText == "YA") {
                            memilikiSumberAirBersihRadioButton = selectedRadioButtonText
                        } else {
                            memilikiSumberAirBersihRadioButton = selectedRadioButtonText
                        }
//                        Log.e("Selected RadioButton:", selectedRadioButtonText)
//                        Log.e("Selected RadioGroup:", resultRadioButton)
                    }
                }
            }
        } else if (resultRadioButton == PESERTA_JAMINANA_KESEHATAN) {
            // pesertaJaminanKeshatanRadioButton
            for (i in 0 until radioButtons) {
                val radioButton = radioGroup.getChildAt(i) as RadioButton
                radioButton.setOnCheckedChangeListener { button, isChecked ->
                    if (isChecked) {
                        // Handle the selected RadioButton
                        val selectedRadioButtonText = button.text.toString()
                        if (selectedRadioButtonText == "YA") {
                            pesertaJaminanKeshatanRadioButton = selectedRadioButtonText
                        } else {
                            pesertaJaminanKeshatanRadioButton = selectedRadioButtonText
                        }
//                        Log.e("Selected RadioButton:", selectedRadioButtonText)
//                        Log.e("Selected RadioGroup:", resultRadioButton)
                    }
                }
            }
        } else if (resultRadioButton == MEMILIKI_AKSES_SANITAS_PEMBUANGAN_LIMBAH_LAYAK) {
            // memilikiAksesSanitasiPembuanganLimbahLayakRadioButton
            for (i in 0 until radioButtons) {
                val radioButton = radioGroup.getChildAt(i) as RadioButton
                radioButton.setOnCheckedChangeListener { button, isChecked ->
                    if (isChecked) {
                        // Handle the selected RadioButton
                        val selectedRadioButtonText = button.text.toString()
                        if (selectedRadioButtonText == "YA") {
                            memilikiAksesSanitasiPembuanganLimbahLayakRadioButton = selectedRadioButtonText
                        } else {
                            memilikiAksesSanitasiPembuanganLimbahLayakRadioButton = selectedRadioButtonText
                        }
//                        Log.e("Selected RadioButton:", selectedRadioButtonText)
//                        Log.e("Selected RadioGroup:", resultRadioButton)
                    }
                }
            }
        } else if (resultRadioButton == PENDAMPINGAN_KELUARGA_OLEH_TPK) {
            // pendampinganKeluargaOlehTPKRadioButton
            for (i in 0 until radioButtons) {
                val radioButton = radioGroup.getChildAt(i) as RadioButton
                radioButton.setOnCheckedChangeListener { button, isChecked ->
                    if (isChecked) {
                        // Handle the selected RadioButton
                        val selectedRadioButtonText = button.text.toString()
                        if (selectedRadioButtonText == "YA") {
                            pendampinganKeluargaOlehTpkRadioButton = selectedRadioButtonText
                        } else {
                            pendampinganKeluargaOlehTpkRadioButton = selectedRadioButtonText
                        }
//                        Log.e("Selected RadioButton:", selectedRadioButtonText)
//                        Log.e("Selected RadioGroup:", resultRadioButton)
                    }
                }
            }
        } else {
            // pesertaKegiatanKetahananPanganRadioButton
            for (i in 0 until radioButtons) {
                val radioButton = radioGroup.getChildAt(i) as RadioButton
                radioButton.setOnCheckedChangeListener { button, isChecked ->
                    if (isChecked) {
                        // Handle the selected RadioButton
                        val selectedRadioButtonText = button.text.toString()
                        if (selectedRadioButtonText == "YA") {
                            pesertaKegiatanKetahananPanganRadioButton = selectedRadioButtonText
                        } else {
                            pesertaKegiatanKetahananPanganRadioButton = selectedRadioButtonText
                        }
//                        Log.e("Selected RadioButton:", selectedRadioButtonText)
//                        Log.e("Selected RadioGroup:", resultRadioButton)
                    }
                }
            }
        }
    }

    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbKeluarga)
        supportActionBar!!.title = getString(R.string.app_name_layanan_keluarga)
        // Change font style text
        binding.tbKeluarga.setTitleTextAppearance(this, R.style.Theme_Stunting)
        // Enable back button if you're in a child activity
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.tbKeluarga.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_submit_keluarga -> {
                val namaAyah= binding.etNamaKeluarga.text.toString()
                val dusun = binding.etDusunKeluarga.text.toString()
                val namaIbu = binding.etNamaLengkapIbuHamilKeluarga.text.toString()
                val anak = binding.etAnakKeluarga.text.toString()

                if (namaAyah.isNotEmpty() && dusun.isNotEmpty() && namaIbu.isNotEmpty() && anak.isNotEmpty()) {
                    addRecord(layananKeluargaDao, namaAyah, dusun, namaIbu, anak, kategoriKeluargaRentanRadioButton,
                        memilikiKartuKeluargaRadioButton, memilikiJambanSehatRadioButton, memilikiSumberAirBersihRadioButton,
                        pesertaJaminanKeshatanRadioButton, memilikiAksesSanitasiPembuanganLimbahLayakRadioButton,
                        pendampinganKeluargaOlehTpkRadioButton, pesertaKegiatanKetahananPanganRadioButton)
                } else {
                    toastInfo(
                        this@LayananKeluargaActivity, getString(R.string.title_input_failed),
                        getString(R.string.description_input_failed), MotionToastStyle.ERROR
                    )
                }
            }
            R.id.btn_tampil_data_keluarga -> {
                // Data not empty
//                Log.e("CEK DATANA", countItem.toString())
                if (countItem != 0) showBottomSheetDialog()
                else {
                    toastInfo(
                        this@LayananKeluargaActivity, getString(R.string.title_show_data_failed),
                        getString(R.string.description_show_data_failed), MotionToastStyle.ERROR
                    )
                }
            }
        }
    }

    private fun showBottomSheetDialog() {
        // Check if the view already has a parent
        val viewBottomSheetDialog: View = bindingLayananKeluargaBottomSheetDialog.root

        if (viewBottomSheetDialog.parent != null) {
            val parentViewGroup = viewBottomSheetDialog.parent as ViewGroup
            parentViewGroup.removeView(viewBottomSheetDialog)
        }
        // Now set the view as content for the BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(viewBottomSheetDialog)
        bottomSheetDialog.show()

        bindingLayananKeluargaBottomSheetDialog.apply {
            ivDelete.setOnClickListener { showCustomeDeleteDialog() }
            ivExportToXlsx.setOnClickListener { showCustomeExportDataDialog() }
            ivArrow.setOnClickListener { showCustomeInfoDialog(this@LayananKeluargaActivity, layoutInflater) }
            tvInfo.setOnClickListener { showCustomeInfoDialog(this@LayananKeluargaActivity, layoutInflater) }
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
                toastInfo(this@LayananKeluargaActivity, getString(R.string.title_export_failed),
                    getString(R.string.description_export_failed), MotionToastStyle.SUCCESS
                )
                exportDatabaseToCSV(layananKeluargaDao)
            }
            exportDialog.dismiss()
            // Goto link directory download
            linkToDirectory(this@LayananKeluargaActivity)
            // Destroying when exported successfully
            finish()
        }
        viewExport.tvNo.setOnClickListener {
            exportDialog.dismiss()
        }
        exportDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private suspend fun exportDatabaseToCSV(layananKeluargaDao: LayananKeluargaDao) {
        // Output variabel fileDir is => /storage/emulated/0/Android/data/com.example.stunting/files/Download
        // val fileDir = "${getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/"

        var dataResult: ArrayList<List<String>> = ArrayList()
        val fileName = "$NAME${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())}.csv"
        val fileDir = "/storage/emulated/0/Download/"
        try {
            val file = File(fileDir, fileName)
            dataResult.add(
                listOf("tanggal", "Nama Lengkap Kepala Keluarga", "Dusun", "Nama Lengkap Ibu Hamil",
                    "Anak", "Kategori Keluarga Rentan (YA/TIDAK)", "Memiliki Kartu Keluarga (YA/TIDAK)", "Memiliki Jamban Sehat (YA/TIDAK)",
                    "Memiliki Sumber Air Bersih (YA/TIDAK)", "Peserta Jaminan Sosial (YA/TIDAK)", "Memiliki Akses Sanitasi Pembuangan Limbah Layak (YA/TIDAK)",
                    "Pendampingan Keluarga oleh TPK (YA/TIDAK)", "Peserta Kegiatan Ketahanan Pangan (YA/TIDAK)")
            )
            layananKeluargaDao.fetchAllLayananKeluarga().collect {
                for (item in it) {
                    dataResult.add(
                        listOf(
                            item.tanggal, item.namaLayananKeluarga, item.dusunLayananKeluarga,
                            item.namaLengkapIbuHamilLayananKeluarga, item.anakLayananKeluarga,
                            item.kategoriKeluargaLayananKeluarga, item.memilikiKartuKeluargaLayananKeluarga,
                            item.memilikiJambanSehatLayananKeluarga,
                            item.memilikiSumberAirBersihLayananKeluarga,
                            item.pesertaJaminanSosialLayananKeluarga,
                            item.memilikiAksesSanitasiPembuanganLimbahLayakLayananKeluarga,
                            item.pendampinganKeluargaOlehTpkLayananKeluarga,
                            item.pesertaKegiatanKetahananPanganLayananKeluarga
                        )
                    )
                    csvWriter().writeAll(dataResult, file.outputStream())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            toastInfo(
                this@LayananKeluargaActivity, getString(R.string.title_export_failed),
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
            deleteAllDates(layananKeluargaDao)
            // We will destroy activity
            finish()
            deleteDialog.dismiss()
        }
        viewDelete.tvNo.setOnClickListener {
            deleteDialog.dismiss()
        }
        deleteDialog.show()
    }

    private fun deleteAllDates(layananKeluargaDao: LayananKeluargaDao) {
        lifecycleScope.launch {
            layananKeluargaDao.deleteAll()
        }
        toastInfo(
            this@LayananKeluargaActivity,getString(R.string.title_history_delete),
            getString(R.string.description_history_delete), MotionToastStyle.SUCCESS
        )
    }

    private fun addRecord(layananKeluargaDao: LayananKeluargaDao, namaAyah: String, dusun: String, namaIbu: String,
                          anak: String, kategoriKeluargaRentan: String, memilikiKartuKeluarga: String, memilikiJambanSehat: String,
                          memilikiSumberAirBersih: String, pesertaJaminanSosial: String, memilikiAksesSanitasiPembuanganLimbahLayak: String,
                          pendampinganKeluargaOlehTpk: String, pesertaKegiatanKetahananPangan: String) {

        val date = getDateTimePrimaryKey()

        lifecycleScope.launch {
            layananKeluargaDao.insert(
                LayananKeluargaEntity(
                    tanggal = date, namaLayananKeluarga = namaAyah, dusunLayananKeluarga = dusun,
                    namaLengkapIbuHamilLayananKeluarga = namaIbu, anakLayananKeluarga = anak,
                    kategoriKeluargaLayananKeluarga = kategoriKeluargaRentan, memilikiKartuKeluargaLayananKeluarga = memilikiKartuKeluarga,
                    memilikiJambanSehatLayananKeluarga = memilikiJambanSehat, memilikiSumberAirBersihLayananKeluarga = memilikiSumberAirBersih,
                    pesertaJaminanSosialLayananKeluarga = pesertaJaminanSosial, memilikiAksesSanitasiPembuanganLimbahLayakLayananKeluarga = memilikiAksesSanitasiPembuanganLimbahLayak,
                    pendampinganKeluargaOlehTpkLayananKeluarga = pendampinganKeluargaOlehTpk, pesertaKegiatanKetahananPanganLayananKeluarga = pesertaKegiatanKetahananPangan
                )
            )
        }
        toastInfo(
            this@LayananKeluargaActivity, getString(R.string.title_saved_data),
            getString(R.string.description_saved_data), MotionToastStyle.SUCCESS
        )

        // Clear the text when data saved !!! (success)
        binding.etNamaKeluarga.text!!.clear()
        binding.etDusunKeluarga.text!!.clear()
        binding.etNamaLengkapIbuHamilKeluarga.text!!.clear()
        binding.etAnakKeluarga.text!!.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _layananKeluargaDao = null
        _bindingLayananKeluargaBottomSheetDialog = null
    }

    companion object {
        private const val NAME = "layananKeluarga_data_"
        private const val KATEGORI_KELUARGA_RENTAN = "kategoriKeluargaRentanButton"
        private const val MEMILIKI_KARTU_KELUARGA = "memilikiKartuKeluargaButton"
        private const val MEMILIKI_JAMBAN_SEHAT = "memilikiJambanSehatButton"
        private const val MEMILIKI_SUMBER_AIR_BERSIH = "memilikiSumberAirBersihButton"
        private const val PESERTA_JAMINANA_KESEHATAN = "pesertaJaminanKeshatanButton"
        private const val MEMILIKI_AKSES_SANITAS_PEMBUANGAN_LIMBAH_LAYAK = "memilikiAksesSanitasiPembuanganLimbahLayakButton"
        private const val PENDAMPINGAN_KELUARGA_OLEH_TPK = "pendampinganKeluargaOlehTpkButton"
        private const val PESERTA_KEGIATAN_KETAHANAN_PANGAN = "pesertaKegiatanKetahananPanganButton"
    }
}