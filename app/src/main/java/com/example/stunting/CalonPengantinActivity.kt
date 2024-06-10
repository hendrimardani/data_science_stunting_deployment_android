package com.example.stunting

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.databinding.ActivityCalonPengantinBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalonPengantinActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding:ActivityCalonPengantinBinding
    private lateinit var cal: Calendar
    private lateinit var dataSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var periksaKesehatanRadioButton: String
    private lateinit var bimbinganPerkawinanRadioButton: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCalonPengantinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Toolbar
        setToolBar()

        // Set caledar and update in view result
        setCalendar(binding.etTglLahirCalonPengantin)
        setCalendar(binding.etPerkiraanTanggalPernikahanCalonPengantin)

        binding.etTglLahirCalonPengantin.setOnClickListener(this)
        binding.etPerkiraanTanggalPernikahanCalonPengantin.setOnClickListener(this)

        binding.btnSubmitCalonPengantin.setOnClickListener(this)
    }

    private fun setCalendar(etTanggal: EditText) {
        cal = Calendar.getInstance()
        dataSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView(etTanggal)
        }
        updateDateInView(etTanggal)
    }

    private fun updateDateInView(etTanggal: EditText) {
        val myFormat = "yyyy/MM/dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        etTanggal.setText(sdf.format(cal.time).toString())
    }

    private fun setToolBar() {
        // Call object actionBar
        setSupportActionBar(binding.tbCalonPengantin)
        supportActionBar!!.title = "Calon Pengantin"
        // Change font style text
        binding.tbCalonPengantin.setTitleTextAppearance(this, R.style.Theme_Stunting)

        // Enable back button if you're in a child activity
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.tbCalonPengantin.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_tgl_lahir_calon_pengantin -> getDatePickerDialog()
            R.id.et_perkiraan_tanggal_pernikahan_calon_pengantin -> getDatePickerDialog()
            R.id.rg_periksa_kesehatan_calon_pengantin -> {
                when (binding.rgPeriksaKesehatanCalonPengantin.checkedRadioButtonId) {
                    R.id.rb_ya_periksa_kesehatan_calon_pengantin -> periksaKesehatanRadioButton = "YA"
                    R.id.rb_tidak_periksa_kesehatan_calon_pengantin -> periksaKesehatanRadioButton = "TIDAK"
                }
            }
            R.id.rg_bimbingan_perkawinan_calon_pengantin -> {
                when (binding.rgBimbinganPerkawinanCalonPengantin.checkedRadioButtonId) {
                    R.id.rb_ya_bimbingan_perkawinan_calon_pengantin -> bimbinganPerkawinanRadioButton = "YA"
                    R.id.rb_tidak_bimbingan_perkawinan_calon_pengantin -> bimbinganPerkawinanRadioButton = "TIDAK"
                }
            }
            R.id.btn_submit_calon_pengantin -> {
                /* TODO */
            }
        }
    }

    private fun getDatePickerDialog() {
        DatePickerDialog(
            this@CalonPengantinActivity,
            dataSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}