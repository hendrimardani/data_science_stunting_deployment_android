package com.example.stunting.ui.detail_anak_patient

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.stunting.MyNamaEditText.Companion.MIN_CHARACTER_NAMA
import com.example.stunting.MyNikEditText.Companion.CHARACTER_16
import com.example.stunting.R
import com.example.stunting.ResultState
import com.example.stunting.databinding.ActivityDetailAnakPatientBinding
import com.example.stunting.ui.ContainerMainActivity
import com.example.stunting.ui.ContainerMainActivity.Companion.EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY
import com.example.stunting.ui.ViewModelFactory
import com.example.stunting.ui.nav_drawer_patient_fragment.NavDrawerMainActivityPatient
import com.example.stunting.ui.nav_drawer_patient_fragment.NavDrawerMainActivityPatient.Companion.EXTRA_ACTIVITY_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT
import com.example.stunting.ui.nav_drawer_patient_fragment.NavDrawerMainActivityPatient.Companion.EXTRA_USER_PATIENT_ID_TO_NAV_DRAWER_MAIN_ACTIVITY_PATIENT
import com.google.android.material.snackbar.Snackbar

class DetailAnakPatientActivity : AppCompatActivity() {
    private var _binding: ActivityDetailAnakPatientBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DetailAnakPatientViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var sweetAlertDialog: SweetAlertDialog? = null
    private var userPatientId: Int? = null
    private var childrenPatientId: Int? = null
    private var jenisKelaminAnakValueRadioButton = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailAnakPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPatientId = intent?.getIntExtra(EXTRA_USER_PATIENT_ID_TO_DETAIL_ANAK_PATIENT_ACTIVITY, 0)
        childrenPatientId = intent?.getIntExtra(EXTRA_CHILDREN_PATIENT_ID_TO_DETAIL_ANAK_PATIENT_ACTIVITY, 0)

        getChildrenPatientByIdUserPatientIdFromLocal()
        textWatcher()
        btnUpdate()
        setToolBar()
    }

    private fun getChildrenPatientByIdUserPatientIdFromLocal() {
        viewModel.getChildrenPatientByIdUserPatientIdFromLocal(childrenPatientId!!, userPatientId!!)
            .observe(this) { childrenPatient ->
                binding.tietNamaAnak.setText(childrenPatient.namaAnak)
                binding.tietNikAnak.setText(childrenPatient.nikAnak)
                binding.tietTglLahirAnak.setText(childrenPatient.tglLahirAnak)
                binding.tietUmurAnak.setText(childrenPatient.umurAnak)
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

                binding.btnUpdate.isEnabled = namaAnakIsValid && nikAnakIsValid &&
                        tglLahirAnakIsNotEmpty && umurAnakIsNotEmpty

                if (binding.btnUpdate.isEnabled) {
                    binding.btnUpdate.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(this@DetailAnakPatientActivity, R.color.blueSecond))
                } else {
                    binding.btnUpdate.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(this@DetailAnakPatientActivity, R.color.buttonDisabledColor))
                    binding.btnUpdate.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@DetailAnakPatientActivity, R.color.white))
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

    private fun btnUpdate() {
        binding.btnUpdate.setOnClickListener {
            val namaAnak = binding.tietNamaAnak.text.toString().trim()
            val nikAnak = binding.tietNikAnak.text.toString().trim()
            val jenisKelaminAnakSelectedId = binding.rgJenisKelaminAnak.checkedRadioButtonId
            if (jenisKelaminAnakSelectedId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(jenisKelaminAnakSelectedId)
                jenisKelaminAnakValueRadioButton = selectedRadioButton.text.toString()
            }
            val tglLahirAnak = binding.tietTglLahirAnak.text.toString().trim()
            val umurAnak = binding.tietUmurAnak.text.toString().trim()

            updateChildrenPatientByUserPatientIdFromApi(
                namaAnak, nikAnak, jenisKelaminAnakValueRadioButton, tglLahirAnak, umurAnak
            )
        }
    }

    private fun updateChildrenPatientByUserPatientIdFromApi(
        namaAnak: String, nikAnak: String, jenisKelaminAnak: String, tglLahirAnak: String, umurAnak: String
    ) {
        val progressBar = SweetAlertDialog(this@DetailAnakPatientActivity, SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        viewModel.updateChildrenPatientByUserPatientIdFromApi(
            userPatientId!!, namaAnak, nikAnak, jenisKelaminAnak, tglLahirAnak, umurAnak)
            .observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> progressBar.show()
                        is ResultState.Error -> {
                            progressBar.dismiss()
//                            Log.d(TAG, "onUpdateChildrenPatientByUserPatientIdFromApi : Error ${result.error}")
                            sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            sweetAlertDialog!!.setTitleText(getString(R.string.title_validation_error))
                            sweetAlertDialog!!.setContentText(result.error)
                            sweetAlertDialog!!.setCancelable(false)
                            sweetAlertDialog!!.show()
                        }
                        is ResultState.Success -> {
                            progressBar.dismiss()
//                            Log.d(TAG, "onUpdateChildrenPatientByUserPatientIdFromApi : Success ${result.data}")
                            val dataUpdateChildrenPatienttByUserPatientId = result.data?.dataUpdateChildrenPatienttByUserPatientId
                            if (dataUpdateChildrenPatienttByUserPatientId != null) {
                                viewModel.updateChildrenPatientByUserPatientIdFromLocal(
                                    userPatientId!!, namaAnak, nikAnak, jenisKelaminAnak, tglLahirAnak, umurAnak
                                )
                                Snackbar.make(binding.root, "Data berhasil diubah", Snackbar.LENGTH_SHORT).show()
                            }
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
        private val TAG = DetailAnakPatientActivity::class.java.simpleName
        const val EXTRA_USER_PATIENT_ID_TO_DETAIL_ANAK_PATIENT_ACTIVITY = "extra_user_patient_id_to_detail_anak_patient_activity"
        const val EXTRA_CHILDREN_PATIENT_ID_TO_DETAIL_ANAK_PATIENT_ACTIVITY = "extra_children_patient_id_to_detail_anak_patient_activity"
    }
}