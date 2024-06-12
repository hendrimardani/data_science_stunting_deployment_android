package com.example.stunting

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.databinding.ActivityBumilBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BumilActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityBumilBinding
    private lateinit var cal: Calendar
    private lateinit var dataSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var statusGiziRadioButton: String
    private lateinit var bottomSheetDialog: BottomSheetDialog

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
        // Toolbar
        setToolBar()

        val view = View.inflate(this, R.layout.dialog_bottom_sheet_bumil, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)
        val behavior = BottomSheetBehavior.from(view.parent as View)

        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {

            }

            override fun onStateChanged(p0: View, p1: Int) {
                //可以将bottomSheet的高度手动计算，设置为屏幕高度的一半。这里假设为1000
                behavior.peekHeight = 1000

                //隐藏时，销毁dialog
                if (p1 == BottomSheetBehavior.STATE_HIDDEN) {
                    dialog.dismiss()
                }
            }
        })

        // Set caledar and update in view result
        setCalendar(binding.etTglLahirBumil)
        setCalendar(binding.etHariPertamaHaidTerakhirBumil)
        setCalendar(binding.etTglPerkiraanLahirBumil)

        binding.etTglLahirBumil.setOnClickListener(this)
        binding.etHariPertamaHaidTerakhirBumil.setOnClickListener(this)
        binding.etTglPerkiraanLahirBumil.setOnClickListener(this)
        binding.rgBumil.setOnClickListener(this)

        binding.btnSubmitBumil.setOnClickListener(this)
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
            R.id.et_tgl_lahir_bumil -> getDatePickerDialog()
            R.id.et_hari_pertama_haid_terakhir_bumil -> getDatePickerDialog()
            R.id.et_tgl_perkiraan_lahir_bumil -> getDatePickerDialog()
            R.id.rg_bumil -> {
                when (binding.rgBumil.checkedRadioButtonId) {
                    R.id.rb_ya_bumil -> statusGiziRadioButton = "YA"
                    R.id.rb_tidak_bumil -> statusGiziRadioButton = "TIDAK"
                }
            }
            R.id.btn_submit_bumil -> {
                showBottomSheetDialog()
            }
        }
    }

    private fun showBottomSheetDialog() {
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.dialog_bottom_sheet_bumil)

        // Menambahkan fungsionalitas ke elemen di dalam Bottom Sheet Dialog
        // ...

        // Mengatur Bottom Sheet Dialog untuk bisa digeser ke atas menjadi fullscreen
        bottomSheetDialog.behavior.maxHeight = resources.displayMetrics.heightPixels
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.show()
    }

    private fun getDatePickerDialog() {
        DatePickerDialog(
            this@BumilActivity,
            dataSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}