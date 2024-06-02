package com.example.stunting

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.databinding.ActivityStuntingPreventBinding

class StuntingPreventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStuntingPreventBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStuntingPreventBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Tollbar
        setToolBar()

        binding.tvLangkahLangkah.text = "Berikut adalah beberapa tips penting untuk mencegah terjadinya stunting pada anak:"
        // Values
        valuesTips()
    }
    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbMencegahStunting)
        supportActionBar!!.title = "Pencegahan Stunting Anak"
        // Change font style text
        binding.tbMencegahStunting.setTitleTextAppearance(this, R.style.Theme_Stunting)

        // Enable back button if you're in a child activity
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.tbMencegahStunting.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun valuesTips() {
        // Sebelum kehamilan
        binding.tvTitle1.text = "1. Persiapkan diri dengan baik: "
        binding.tvValue1.text = "Lakukan pemeriksaan kesehatan pranikah untuk memastikan kondisi kesehatan ibu hamil baik. Konsultasikan dengan dokter tentang asupan gizi yang tepat selama kehamilan."
        binding.tvTitle2.text = "2. Penuhi kebutuhan gizi: "
        binding.tvValue2.text = "Pastikan ibu hamil mengonsumsi makanan bergizi seimbang yang kaya akan protein, zat besi, kalsium, vitamin, dan mineral lainnya."
        binding.tvTitle3.text = "3. Minum air putih yang cukup: "
        binding.tvValue3.text = "Minumlah air putih minimal 8 gelas per hari untuk menjaga tubuh tetap terhidrasi."
        binding.tvTitle4.text = "4. Istirahat yang cukup: "
        binding.tvValue4.text = "Pastikan ibu hamil mendapatkan istirahat yang cukup selama kehamilan."
        binding.tvTitle5.text = "5. Hindari alkohol, rokok, dan narkoba: "
        binding.tvValue5.text = "Pastikan ibu hamil mendapatkan istirahat yang cukup selama kehamilan."
        // Saat kehamilan
        binding.tvTitle6.text = "1. Rutin memeriksakan kehamilan: "
        binding.tvValue6.text = "Lakukan pemeriksaan kehamilan minimal 4 kali selama kehamilan untuk memantau kesehatan ibu dan janin."
        binding.tvTitle7.text = "2. Minum tablet tambah darah (TTD): "
        binding.tvValue7.text = "Konsumsi TTD sesuai anjuran dokter untuk mencegah anemia pada ibu hamil."
        binding.tvTitle8.text = "3. Konsumsi makanan bergizi seimbang: "
        binding.tvValue8.text = "Konsumsi TTD sesuai anjuran dokter untuk mencegah anemia pada ibu hamil."
        binding.tvTitle9.text = "4. Ikuti kelas edukasi kehamilan: "
        binding.tvValue9.text = "Pastikan ibu hamil terus mengonsumsi makanan bergizi seimbang untuk memenuhi kebutuhan gizi ibu dan janin."
        // Setelah kehamilan
        binding.tvTitle10.text = "1. Berikan ASI ekslusif: "
        binding.tvValue10.text = "Berikan ASI eksklusif kepada bayi selama 6 bulan pertama. ASI adalah makanan terbaik untuk bayi dan dapat membantu mencegah stunting."
        binding.tvTitle11.text = "2. Berikan MPASI yang bergizi: "
        binding.tvValue11.text = "Setelah usia 6 bulan, berikan MPASI yang bergizi seimbang kepada bayi. Konsultasikan dengan dokter atau ahli gizi untuk mendapatkan panduan tentang pemberian MPASI yang tepat."
    }
}