package com.example.stunting

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.databinding.ActivityRemajaPutriBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RemajaPutriActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityRemajaPutriBinding
    private lateinit var cal: Calendar
    private lateinit var dataSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var mendapatTTDRadioButton: String
    private lateinit var periksaAnemiaRadioButton: String
    private lateinit var hasilPeriksaAnemiaRadioButton: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRemajaPutriBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Toolbar
        setToolBar()

        // Set caledar and update in view result
        setCalendar(binding.etTglLahirRemajaPutri)

        binding.etTglLahirRemajaPutri.setOnClickListener(this)

        binding.btnSubmitRemajaPutri.setOnClickListener(this)
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
        setSupportActionBar(binding.tbRemajaPutri)
        supportActionBar!!.title = "Remaja Putri"
        // Change font style text
        binding.tbRemajaPutri.setTitleTextAppearance(this, R.style.Theme_Stunting)
        // Set icon
        supportActionBar!!.setIcon(R.drawable.remaja_putri)
        // Enable back button if you're in a child activity
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.tbRemajaPutri.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_tgl_lahir_remaja_putri -> getDatePickerDialog()
            R.id.rg_mendapat_ttd_remaja_putri -> {
                when (binding.rgMendapatTtdRemajaPutri.checkedRadioButtonId) {
                    R.id.rb_ya_mendapat_ttd_remaja_putri -> mendapatTTDRadioButton = "YA"
                    R.id.rb_tidak_mendapat_ttd_remaja_putri -> mendapatTTDRadioButton = "TIDAK"
                }
            }
            R.id.rg_periksa_anemia_remaja_putri -> {
                when (binding.rgPeriksaAnemiaRemajaPutri.checkedRadioButtonId) {
                    R.id.rb_ya_periksa_anemia_remaja_putri -> periksaAnemiaRadioButton = "YA"
                    R.id.rb_tidak_periksa_anemia_remaja_putri -> periksaAnemiaRadioButton = "TIDAK"
                }
            }
            R.id.rg_hasil_periksa_anemia_remaja_putri -> {
                when (binding.rgHasilPeriksaAnemiaRemajaPutri.checkedRadioButtonId) {
                    R.id.rb_ya_hasil_periksa_anemia_remaja_putri -> hasilPeriksaAnemiaRadioButton = "YA"
                    R.id.rb_tidak_hasil_periksa_anemia_remaja_putri -> hasilPeriksaAnemiaRadioButton = "TIDAK"
                }
            }
            R.id.btn_submit_remaja_putri -> {
                /* TODO */
            }
        }
    }

    private fun getDatePickerDialog() {
        DatePickerDialog(
            this@RemajaPutriActivity,
            dataSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}