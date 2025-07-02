package com.example.stunting.ui.tambah_anak_patient

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.stunting.MyNamaEditText.Companion.MIN_CHARACTER_NAMA
import com.example.stunting.MyNikEditText.Companion.CHARACTER_16
import com.example.stunting.R
import com.example.stunting.ResultState
import com.example.stunting.databinding.ActivityTambahAnakPatientBinding
import com.example.stunting.ui.ContainerMainActivity
import com.example.stunting.ui.ContainerMainActivity.Companion.EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY
import com.example.stunting.ui.ViewModelFactory
import com.example.stunting.utils.Functions.calculateAge
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TambahAnakPatientActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityTambahAnakPatientBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<TambahAnakPatientViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var cal: Calendar
    private var dataSetListenerTgllahirAnak: DatePickerDialog.OnDateSetListener? = null
    private var sweetAlertDialog: SweetAlertDialog? = null
    private var userPatientId: Int? = null
    private var jenisKelaminAnakValueRadioButton = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTambahAnakPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPatientId = intent?.getIntExtra(EXTRA_USER_PATIENT_ID_TO_TAMBAH_ANAK_PATIENT_ACTIVITY, 0)

        setCalendarTglLahirAnak(binding.tietTglLahirAnak)

        binding.tietTglLahirAnak.setOnClickListener(this)

        setTglLahirAnak()
        btnAdd()
        textWatcher()
        setToolBar()
    }

    private fun getDatePickerDialogTglLahirAnak(context: Context) {
        DatePickerDialog(context, dataSetListenerTgllahirAnak, cal.get(Calendar.YEAR), cal.get(
            Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setCalendarTglLahirAnak(etTanggal: EditText) {
        cal = Calendar.getInstance()
        dataSetListenerTgllahirAnak = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInViewTglLahirAnak(etTanggal)
        }
        updateDateInViewTglLahirAnak(etTanggal)
    }

    private fun updateDateInViewTglLahirAnak(etTanggal: EditText) {
        val myFormat = "yyyy/MM/dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        etTanggal.setText(sdf.format(cal.time).toString())
    }

    private fun setTglLahirAnak() {
        binding.tietTglLahirAnak.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                val tglLahir = s.toString()
//                Log.e("TEST FORMAT", tglLahir)
                if (tglLahir.isNotEmpty()) {
                    val umur = calculateAge(tglLahir)
                    binding.tietUmurAnak.setText(umur)
                } else {
                    binding.tietUmurAnak.setText("")
                }
            }
        })
    }

    private fun btnAdd() {
        binding.btnAdd.setOnClickListener {
            val namaAnak = binding.tietNamaAnak.text.toString().trim()
            val nikAnak = binding.tietNikAnak.text.toString().trim()
            val jenisKelaminAnakSelectedId = binding.rgJenisKelaminAnak.checkedRadioButtonId
            if (jenisKelaminAnakSelectedId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(jenisKelaminAnakSelectedId)
                jenisKelaminAnakValueRadioButton = selectedRadioButton.text.toString()
            }
            val tglLahirAnak = binding.tietTglLahirAnak.text.toString().trim()
            val umurAnak = binding.tietUmurAnak.text.toString().trim()

            addChildrenPatientByUserPatientId(
                namaAnak, nikAnak, jenisKelaminAnakValueRadioButton, tglLahirAnak, umurAnak
            )
        }
    }

    private fun addChildrenPatientByUserPatientId(
        namaAnak: String, nikAnak: String, jenisKelaminAnak: String, tglLahirAnak: String, umurAnak: String
    ) {
        val progressBar = SweetAlertDialog(this@TambahAnakPatientActivity, SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        viewModel.addChildrenPatientByUserPatientId(
            userPatientId!!, namaAnak, nikAnak, jenisKelaminAnak, tglLahirAnak, umurAnak
        ).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> progressBar.show()
                    is ResultState.Error -> {
                        progressBar.dismiss()
//                            Log.d(TAG, "onAddChildrenPatientByUserPatientId : Error ${result.error}")
                        sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        sweetAlertDialog!!.setTitleText(getString(R.string.title_validation_error))
                        sweetAlertDialog!!.setContentText(result.error)
                        sweetAlertDialog!!.setCancelable(false)
                        sweetAlertDialog!!.show()
                    }
                    is ResultState.Success -> {
                        progressBar.dismiss()
//                            Log.d(TAG, "onAddChildrenPatientByUserPatientId : Success ${result.data}")
                            Snackbar.make(binding.root, "Data berhasil ditambah", Snackbar.LENGTH_SHORT).show()
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(this, ContainerMainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tiet_tgl_lahir_anak -> getDatePickerDialogTglLahirAnak(this)
        }
    }

    private fun setToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = getString(R.string.text_tambah_anak)
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