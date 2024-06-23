package com.example.stunting

import LayananKeluargaAdapter
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stunting.Adapter.RemajaPutriAdapter
import com.example.stunting.Database.DatabaseApp
import com.example.stunting.Database.LayananKeluarga.LayananKeluargaDao
import com.example.stunting.Database.LayananKeluarga.LayananKeluargaEntity
import com.example.stunting.Database.RemajaPutri.RemajaPutriDao
import com.example.stunting.Database.RemajaPutri.RemajaPutriEntity
import com.example.stunting.databinding.ActivityLayananKeluargaBinding
import com.example.stunting.databinding.DialogBottomSheetAllBinding
import com.example.stunting.databinding.DialogBottomSheetLayananKeluargaBinding
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

class LayananKeluargaActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLayananKeluargaBinding
    private lateinit var bindingLayanaKeluargaBottomSheetDialog: DialogBottomSheetLayananKeluargaBinding
    private lateinit var layananKeluargaDao: LayananKeluargaDao

    var countItem = 0
    var kategoriKeluargaRentanRadioButton = "YA"
    var memilikiKartuKeluargaRadioButton = "YA"
    var memilikiJambanSehatRadioButton = "YA"
    var memilikiSumberAirBersihRadioButton = "YA"
    var pesertaJaminanKeshatanRadioButton = "YA"
    var memilikiAksesSanitasiPembuanganLimbahLayakRadioButton = "YA"
    var pendampinganKeluargaOlehTpkRadioButton = "YA"
    var pesertaKegiatanKetahananPanganRadioButton = "YA"

    companion object {
        const val NAME = "layananKeluarga_data_"
        const val KATEGORI_KELUARGA_RENTAN = "kategoriKeluargaRentanButton"
        const val MEMILIKI_KARTU_KELUARGA = "memilikiKartuKeluargaButton"
        const val MEMILIKI_JAMBAN_SEHAT = "memilikiJambanSehatButton"
        const val MEMILIKI_SUMBER_AIR_BERSIH = "memilikiSumberAirBersihButton"
        const val PESERTA_JAMINANA_KESEHATAN = "pesertaJaminanKeshatanButton"
        const val MEMILIKI_AKSES_SANITAS_PEMBUANGAN_LIMBAH_LAYAK = "memilikiAksesSanitasiPembuanganLimbahLayakButton"
        const val PENDAMPINGAN_KELUARGA_OLEH_TPK = "pendampinganKeluargaOlehTpkButton"
        const val PESERTA_KEGIATAN_KETAHANAN_PANGAN = "pesertaKegiatanKetahananPanganButton"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLayananKeluargaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // toolbar
        setToolBar()
        // Binding BumilBottomSheetDialog for retrieve xml id
        bindingLayanaKeluargaBottomSheetDialog = DialogBottomSheetLayananKeluargaBinding.inflate(layoutInflater)
        bindingLayanaKeluargaBottomSheetDialog.tvDataLayananKeluarga.text = "List Data       \nKeluarga"

        // Call database
        layananKeluargaDao = (application as DatabaseApp).dbLayananKeluargaDatabase.layananKeluargaDao()

        getRadioButtonValue(R.id.rg_kategori_keluarga_rentan_layanan_keluarga, KATEGORI_KELUARGA_RENTAN)
        getRadioButtonValue(R.id.rg_memiliki_kartu_keluarga_layanan_keluarga, MEMILIKI_KARTU_KELUARGA)
        getRadioButtonValue(R.id.rg_memiliki_jamban_sehat_layanan_keluarga, MEMILIKI_JAMBAN_SEHAT)
        getRadioButtonValue(R.id.rg_memiliki_sumber_air_bersih_layanan_keluarga, MEMILIKI_SUMBER_AIR_BERSIH)
        getRadioButtonValue(R.id.rg_peserta_jaminan_kesehatan_layanan_keluarga, PESERTA_JAMINANA_KESEHATAN)
        getRadioButtonValue(R.id.rg_memiliki_akses_sanitasi_pembuangan_limbah_layak_layanan_keluarga, MEMILIKI_AKSES_SANITAS_PEMBUANGAN_LIMBAH_LAYAK)
        getRadioButtonValue(R.id.rg_pendampingan_keluarga_oleh_tpk_layanan_keluarga, PENDAMPINGAN_KELUARGA_OLEH_TPK)
        getRadioButtonValue(R.id.rg_peserta_kegiatan_ketahanan_pangan_layanan_keluarga, PESERTA_KEGIATAN_KETAHANAN_PANGAN)

        binding.btnSubmitLayananKeluarga.setOnClickListener(this)
        binding.btnTampilDataLayananKeluarga.setOnClickListener(this)

        // Get all items
        getAll(layananKeluargaDao)
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
            bindingLayanaKeluargaBottomSheetDialog.tvTotalData.text = "$countItem Data"
            bindingLayanaKeluargaBottomSheetDialog.rvBottomSheetLayananKeluarga.layoutManager = LinearLayoutManager(this)
            bindingLayanaKeluargaBottomSheetDialog.rvBottomSheetLayananKeluarga.adapter = layananKeluargaAdapter
            // To scrolling automatic when data entered
            bindingLayanaKeluargaBottomSheetDialog.rvBottomSheetLayananKeluarga
                .smoothScrollToPosition(countItem - 1)

            // When input data automatically to last index
            bindingLayanaKeluargaBottomSheetDialog.rvBottomSheetLayananKeluarga
                .layoutManager!!.smoothScrollToPosition(bindingLayanaKeluargaBottomSheetDialog
                    .rvBottomSheetLayananKeluarga, null, countItem - 1)
        }
        Log.e("HASILNA", layananKeluargaList.toString())
    }

    private fun getRadioButtonValue(bindingRadioGroup: Int, resultRadioButton: String) {
        val radioGroup = findViewById<RadioGroup>(bindingRadioGroup)
        // Get all the RadioButtons within the RadioGroup
        val radioButtons = radioGroup.childCount

        if (resultRadioButton == "kategoriKeluargaRentanButton") {
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
                        Log.e("Selected RadioButton:", selectedRadioButtonText)
                        Log.e("Selected RadioGroup:", resultRadioButton)
                    }
                }
            }
        } else if (resultRadioButton == "memilikiKartuKeluargaButton") {
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
                        Log.e("Selected RadioButton:", selectedRadioButtonText)
                        Log.e("Selected RadioGroup:", resultRadioButton)
                    }
                }
            }
        } else if (resultRadioButton == "memilikiJambanSehatButton") {
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
                        Log.e("Selected RadioButton:", selectedRadioButtonText)
                        Log.e("Selected RadioGroup:", resultRadioButton)
                    }
                }
            }
        } else if (resultRadioButton == "memilikiSumberAirBersihButton") {
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
                        Log.e("Selected RadioButton:", selectedRadioButtonText)
                        Log.e("Selected RadioGroup:", resultRadioButton)
                    }
                }
            }
        } else if (resultRadioButton == "pesertaJaminanKeshatanButton") {
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
                        Log.e("Selected RadioButton:", selectedRadioButtonText)
                        Log.e("Selected RadioGroup:", resultRadioButton)
                    }
                }
            }
        } else if (resultRadioButton == "memilikiAksesSanitasiPembuanganLimbahLayakButton") {
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
                        Log.e("Selected RadioButton:", selectedRadioButtonText)
                        Log.e("Selected RadioGroup:", resultRadioButton)
                    }
                }
            }
        } else if (resultRadioButton == "pendampinganKeluargaOlehTpkButton") {
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
                        Log.e("Selected RadioButton:", selectedRadioButtonText)
                        Log.e("Selected RadioGroup:", resultRadioButton)
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
                        Log.e("Selected RadioButton:", selectedRadioButtonText)
                        Log.e("Selected RadioGroup:", resultRadioButton)
                    }
                }
            }
        }
    }

    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbLayananKeluarga)
        supportActionBar!!.title = "Layanan Keluarga"
        // Change font style text
        binding.tbLayananKeluarga.setTitleTextAppearance(this, R.style.Theme_Stunting)
        // Enable back button if you're in a child activity
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.tbLayananKeluarga.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_submit_layanan_keluarga -> {
                val namaAyah= binding.etNamaLayananKeluarga.text.toString()
                val dusun = binding.etDusunLayananKeluarga.text.toString()
                val namaIbu = binding.etNamaLengkapIbuHamilLayananKeluarga.text.toString()
                val anak = binding.etAnakLayananKeluarga.text.toString()

                if (namaAyah.isNotEmpty() && dusun.isNotEmpty() && namaIbu.isNotEmpty() && anak.isNotEmpty()) {
                    addRecord(layananKeluargaDao, namaAyah, dusun, namaIbu, anak, kategoriKeluargaRentanRadioButton,
                        memilikiKartuKeluargaRadioButton, memilikiJambanSehatRadioButton, memilikiSumberAirBersihRadioButton,
                        pesertaJaminanKeshatanRadioButton, memilikiAksesSanitasiPembuanganLimbahLayakRadioButton,
                        pendampinganKeluargaOlehTpkRadioButton, pesertaKegiatanKetahananPanganRadioButton)
                } else toastInfo("INPUT GAGAL !", "Data tidak boleh ada yang kosong !", MotionToastStyle.ERROR)
                }
            R.id.btn_tampil_data_layanan_keluarga -> {
                // Data not empty
                Log.e("CEK DATANA", countItem.toString())
                if (countItem != 0) showBottomSheetDialog() else
                    toastInfo("TAMPILKAN DATA GAGAL !",
                        "Data masih kosong tidak ada yang ditampilkan, silahkan input data.", MotionToastStyle.ERROR)
            }
        }
    }

    private fun showBottomSheetDialog() {
        // Check if the view already has a parent
        val viewBottomSheetDialog: View = bindingLayanaKeluargaBottomSheetDialog.root

        if (viewBottomSheetDialog.parent != null) {
            val parentViewGroup = viewBottomSheetDialog.parent as ViewGroup
            parentViewGroup.removeView(viewBottomSheetDialog)
        }
        // Now set the view as content for the BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(viewBottomSheetDialog)
        bottomSheetDialog.show()

        bindingLayanaKeluargaBottomSheetDialog.ivDelete.setOnClickListener {
            showCustomeDeleteDialog()
        }
        bindingLayanaKeluargaBottomSheetDialog.ivExportToXlsx.setOnClickListener {
            showCustomeExportDataDialog()
        }
        bindingLayanaKeluargaBottomSheetDialog.ivArrow.setOnClickListener {
            showCustomeInfoDilog()
        }
        bindingLayanaKeluargaBottomSheetDialog.tvInfo.setOnClickListener {
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
                exportDatabaseToCSV(layananKeluargaDao)
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

    private suspend fun exportDatabaseToCSV(layananKeluargaDao: LayananKeluargaDao) {
        // Output variabel fileDir is => /storage/emulated/0/Android/data/com.example.stunting/files/Download
        // val fileDir = "${getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/"

        var dataResult: ArrayList<List<String>> = ArrayList()
        val fileName = "${NAME}${SimpleDateFormat("yyyyMMMdd_HHmmss").format(Date())}.csv"
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
                        listOf("${item.tanggal}", "${item.namaLayananKeluarga}", "${item.dusunLayananKeluarga}", "${item.namaLengkapIbuHamilLayananKeluarga}",
                            "${item.anakLayananKeluarga}", "${item.kategoriKeluargaLayananKeluarga}", "${item.memilikiKartuKeluargaLayananKeluarga}",
                            "${item.memilikiJambanSehatLayananKeluarga}", "${item.memilikiSumberAirBersihLayananKeluarga}", "${item.pesertaJaminanSosialLayananKeluarga}",
                            "${item.memilikiAksesSanitasiPembuanganLimbahLayakLayananKeluarga}", "${item.pendampinganKeluargaOlehTpkLayananKeluarga}",
                            "${item.pesertaKegiatanKetahananPanganLayananKeluarga}"
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
        toastInfo("HISTORI BERHASIL DIHAPUS SEMUA", "Silahkan jalankan kembali aplikasinya.", MotionToastStyle.SUCCESS)
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
        toastInfo("DATA TERSIMPAN !", "Data berhasil di simpan di database !!", MotionToastStyle.SUCCESS)

        // Clear the text when data saved !!! (success)
        binding.etNamaLayananKeluarga.text!!.clear()
        binding.etDusunLayananKeluarga.text!!.clear()
        binding.etNamaLengkapIbuHamilLayananKeluarga.text!!.clear()
        binding.etAnakLayananKeluarga.text!!.clear()
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
}