package com.example.stunting

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.databinding.ActivityLayananKeluargaBinding
import java.util.Calendar

class LayananKeluargaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLayananKeluargaBinding
    private lateinit var cal: Calendar
    private lateinit var dataSetListener: DatePickerDialog.OnDateSetListener
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

    }
}