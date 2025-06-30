package com.example.stunting.ui.tambah_anak_patient

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.MyNamaEditText.Companion.MIN_CHARACTER_NAMA
import com.example.stunting.MyNikEditText.Companion.CHARACTER_16
import com.example.stunting.R
import com.example.stunting.databinding.ActivityTambahAnakPatientBinding
import com.example.stunting.ui.ViewModelFactory

class TambahAnakPatientActivity : AppCompatActivity() {
    private var _binding: ActivityTambahAnakPatientBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<TambahAnakPatientViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var userPatientId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityTambahAnakPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        userPatientId = intent?.getIntExtra(EXTRA_USER_PATIENT_ID_TO_TAMBAH_ANAK_PATIENT_ACTIVITY, 0)

        btnAdd()
        textWatcher()
        setToolBar()
    }

    private fun btnAdd() {
        binding.btnAdd.setOnClickListener {

        }
    }

    private fun textWatcher() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val namaAnak = binding.tietNamaAnak.text.toString().trim()
                val nikAnak = binding.tietNikAnak.text.toString().trim()
                val tglLahirAnak = binding.tietTglLahirAnak.text.toString().trim()
                val umurAnak = binding.tietUmurAnak.text.toString().trim()

                val namaAnakIsValid = namaAnak.length >= MIN_CHARACTER_NAMA
                val nikAnakIsValid = nikAnak.length == CHARACTER_16
                val tglLahirAnakIsNotEmpty = tglLahirAnak.isNotEmpty()
                val umurAnakIsNotEmpty = umurAnak.isNotEmpty()

                binding.btnAdd.isEnabled = namaAnakIsValid && nikAnakIsValid &&
                        tglLahirAnakIsNotEmpty && umurAnakIsNotEmpty

                if (binding.btnAdd.isEnabled) {
                    binding.btnAdd.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(this@TambahAnakPatientActivity, R.color.blueSecond))
                } else {
                    binding.btnAdd.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(this@TambahAnakPatientActivity, R.color.buttonDisabledColor))
                    binding.btnAdd.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@TambahAnakPatientActivity, R.color.white))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        }
        binding.tietNamaAnak.addTextChangedListener(textWatcher)
        binding.tietNikAnak.addTextChangedListener(textWatcher)
        binding.tietTglLahirAnak.addTextChangedListener(textWatcher)
        binding.tietUmurAnak.addTextChangedListener(textWatcher)
    }

    private fun setToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = getString(R.string.text_detail)
        binding.toolbar.setTitleTextAppearance(this, R.style.Theme_Stunting)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    companion object {
        const val EXTRA_USER_PATIENT_ID_TO_TAMBAH_ANAK_PATIENT_ACTIVITY = "extra_user_patient_id_to_tambah_anak_patient_activity"
    }
}