package com.example.stunting

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.databinding.ActivityLayananKeluargaBinding

class LayananKeluargaActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLayananKeluargaBinding
    private lateinit var kategoriKeluargaRentan: String
    private lateinit var memilikiKartuKeluarga: String
    private lateinit var memilikiJambanSehat: String
    private lateinit var memilikiSumberAirBersih: String
    private lateinit var pesertaJaminanKeshatan: String
    private lateinit var memilikiAksesSanitasiPembuanganLimbahLayak: String
    private lateinit var pendampinganKeluargaOlehTPK: String
    private lateinit var pesertaKegiatanKetahananPangan: String


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

        binding.btnSubmitLayananKeluarga.setOnClickListener(this)
    }

    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbLayananKeluarga)
        supportActionBar!!.title = "Layanan Keluarga"
        // Change font style text
        binding.tbLayananKeluarga.setTitleTextAppearance(this, R.style.Theme_Stunting)
        // Set icon
        supportActionBar!!.setIcon(R.drawable.layanan_keluarga)
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
            R.id.rg_kategori_keluarga_rentan_layanan_keluarga -> {
                when (binding.rgKategoriKeluargaRentanLayananKeluarga.checkedRadioButtonId) {
                    R.id.rb_ya_kategori_keluarga_rentan_layanan_keluarga -> kategoriKeluargaRentan = "YA"
                    R.id.rb_tidak_kategori_keluarga_rentan_layanan_keluarga -> kategoriKeluargaRentan = "TIDAK"
                }
            }
            R.id.rg_memiliki_kartu_keluarga_layanan_keluarga -> {
                when(binding.rgMemilikiKartuKeluargaLayananKeluarga.checkedRadioButtonId) {
                    R.id.rb_ya_memiliki_kartu_keluarga_layanan_keluarga -> memilikiKartuKeluarga = "YA"
                    R.id.rb_tidak_memiliki_kartu_keluarga_layanan_keluarga -> memilikiKartuKeluarga = "TIDAK"
                }
            }
            R.id.rg_memiliki_jamban_sehat_layanan_keluarga -> {
                when (binding.rgMemilikiJambanSehatLayananKeluarga.checkedRadioButtonId) {
                    R.id.rg_memiliki_jamban_sehat_layanan_keluarga -> memilikiJambanSehat = "YA"
                    R.id.rg_memiliki_jamban_sehat_layanan_keluarga -> memilikiJambanSehat = "TIDAK"
                }
            }
            R.id.rg_memiliki_sumber_air_bersih_layanan_keluarga -> {
                when (binding.rgMemilikiSumberAirBersihLayananKeluarga.checkedRadioButtonId) {
                    R.id.rg_memiliki_sumber_air_bersih_layanan_keluarga -> memilikiSumberAirBersih = "YA"
                    R.id.rg_memiliki_sumber_air_bersih_layanan_keluarga -> memilikiSumberAirBersih = "TIDAK"
                }
            }
            R.id.rg_peserta_jaminan_kesehatan_layanan_keluarga -> {
                when (binding.rgPesertaJaminanKesehatanLayananKeluarga.checkedRadioButtonId) {
                    R.id.rg_peserta_jaminan_kesehatan_layanan_keluarga -> pesertaJaminanKeshatan = "YA"
                    R.id.rg_peserta_jaminan_kesehatan_layanan_keluarga -> pesertaJaminanKeshatan = "TIDAK"
                }
            }
            R.id.rg_memiliki_akses_sanitasi_pembuangan_limbah_layak_layanan_keluarga -> {
                when (binding.rgMemilikiAksesSanitasiPembuanganLimbahLayakLayananKeluarga.checkedRadioButtonId) {
                    R.id.rb_ya_memiliki_akses_sanitasi_pembuangan_limbah_layak_layanan_keluarga -> memilikiAksesSanitasiPembuanganLimbahLayak = "YA"
                    R.id.rb_ya_memiliki_akses_sanitasi_pembuangan_limbah_layak_layanan_keluarga -> memilikiAksesSanitasiPembuanganLimbahLayak = "TIDAK"
                }
            }
            R.id.rg_pendampingan_keluarga_oleh_tpk_layanan_keluarga -> {
                when (binding.rgPendampinganKeluargaOlehTpkLayananKeluarga.checkedRadioButtonId) {
                    R.id.rb_ya_pendampingan_keluarga_oleh_tpk_layanan_keluarga -> pendampinganKeluargaOlehTPK = "YA"
                    R.id.rb_tidak_pendampingan_keluarga_oleh_tpk_layanan_keluarga -> pendampinganKeluargaOlehTPK = "TIDAK"
                }
            }
            R.id.rg_peserta_kegiatan_ketahanan_pangan_layanan_keluarga -> {
                when (binding.rgPesertaKegiatanKetahananPanganLayananKeluarga.checkedRadioButtonId) {
                    R.id.rb_ya_peserta_kegiatan_ketahanan_pangan_layanan_keluarga -> pesertaKegiatanKetahananPangan = "YA"
                    R.id.rb_tidak_peserta_kegiatan_ketahanan_pangan_layanan_keluarga -> pesertaKegiatanKetahananPangan = "TIDAK"
                }
            }
            R.id.btn_submit_layanan_keluarga -> {
                /* TODO */
            }
        }
    }
}