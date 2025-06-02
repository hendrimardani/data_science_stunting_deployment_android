package com.example.stunting.ui.opening_user_profile_patient_form

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.stunting.MyNamaEditText.Companion.MIN_CHARACTER_NAMA
import com.example.stunting.MyNikEditText.Companion.CHARACTER_16
import com.example.stunting.R
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.entities.user_profile_patient.UserProfilePatientEntity
import com.example.stunting.databinding.ActivityOpeningUserProfilePatientFormBinding
import com.example.stunting.ui.MainActivity
import com.example.stunting.ui.MainActivity.Companion.EXTRA_FRAGMENT_TO_MAIN_ACTIVITY
import com.example.stunting.ui.ViewModelFactory
import com.example.stunting.ui.nav_drawer_patient_fragment.NavDrawerMainActivityPatient
import com.example.stunting.ui.opening_user_profile_patient_ready.OpeningUserProfilePatientReadyActivity
import com.example.stunting.ui.opening_user_profile_patient_ready.OpeningUserProfilePatientReadyActivity.Companion.EXTRA_USER_PATIENT_ID_TO_OPENING_USER_PROFILE_PATIENT_READY
import com.example.stunting.utils.Functions.calculateAge
import com.example.stunting.utils.Functions.getDatePickerDialogTglLahir
import com.example.stunting.utils.Functions.setCalendarTglLahir
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OpeningUserProfilePatientFormActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityOpeningUserProfilePatientFormBinding? = null
    private val binding get() = _binding!!

    private lateinit var cal: Calendar
    private var dataSetListenerTgllahirAnak: DatePickerDialog.OnDateSetListener? = null

    private val viewModel by viewModels<OpeningUserProfilePatientFormViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var userPatientId: Int? = null
    private var jenisKelaminAnakValueRadioButton = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityOpeningUserProfilePatientFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        userPatientId = intent?.getIntExtra(EXTRA_USER_PATIENT_ID_TO_OPENING_USER_PROFILE_PATIENT_FORM, 0)
        getUserProfilePatientsFromApi()

        setCalendarTglLahir(binding.tietTglLahirBumil)
        setCalendarTglLahirAnak(binding.tietTglLahirAnak)

        binding.tietTglLahirBumil.setOnClickListener(this)
        binding.tietTglLahirAnak.setOnClickListener(this)

        setTglLahirBumil()
        setTglLahirAnak()
        textWatcher()
        showButtonPreviousNextAndSave()
        btnSave()
    }

    private fun btnSave() {
        binding.btnSave.setOnClickListener {
            val namaBumil = binding.tietNamaBumil.text.toString().trim()
            val nikBumil = binding.tietNikBumil.text.toString().trim()
            val tglLahirBumil = binding.tietTglLahirBumil.text.toString().trim()
            val umurBumil = binding.tietUmurBumil.text.toString().trim()
            val alamat = binding.tietAlamat.text.toString().trim()
            val namaAyah = binding.tietNamaAyah.text.toString().trim()
            val namaAnak = binding.tietNamaAnak.text.toString().trim()
            val nikAnak = binding.tietNikAnak.text.toString().trim()
            val jenisKelaminAnakSelectedId = binding.rgJenisKelaminAnak.checkedRadioButtonId
            if (jenisKelaminAnakSelectedId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(jenisKelaminAnakSelectedId)
                jenisKelaminAnakValueRadioButton = selectedRadioButton.text.toString()
            }
            val tglLahirAnak = binding.tietTglLahirAnak.text.toString().trim()
            val umurAnak = binding.tietUmurAnak.text.toString().trim()
            val namaCabang = binding.tietCabang.text.toString().trim()

            val progressBar = SweetAlertDialog(this@OpeningUserProfilePatientFormActivity, SweetAlertDialog.PROGRESS_TYPE)
            progressBar.setTitleText(getString(R.string.title_loading))
            progressBar.setContentText(getString(R.string.description_loading))
                .progressHelper.barColor = Color.parseColor("#73D1FA")
            progressBar.setCancelable(false)

            viewModel.updateUserProfilePatientByIdFromApi(
                userPatientId!!, namaBumil, nikBumil, tglLahirBumil, umurBumil, alamat, namaAyah, namaAnak,
                nikAnak, jenisKelaminAnakValueRadioButton, tglLahirAnak, umurAnak, namaCabang
            ).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> progressBar.show()
                        is ResultState.Error -> {
                            progressBar.dismiss()
//                            Log.d(TAG, "onUpdateUserProfilePatientById : Error ${result.error}")
                        }
                        is ResultState.Success -> {
                            progressBar.dismiss()
//                            Log.d(TAG, "onUpdateUserProfilePatientById : Success ${result.data}")
                            val updatedUserProfilePatient = result.data?.dataUpdateUserProfilePatientById?.get(0)

                            if (updatedUserProfilePatient != null) {
                                viewModel.updateUserProfilePatientByIdFromLocal(
                                    UserProfilePatientEntity(
                                        id = updatedUserProfilePatient.id,
                                        userPatientId = updatedUserProfilePatient.userPatientId,
                                        branchId = updatedUserProfilePatient.branchId,
                                        namaBumil = updatedUserProfilePatient.namaBumil,
                                        nikBumil = updatedUserProfilePatient.nikBumil,
                                        tglLahirBumil = updatedUserProfilePatient.tglLahirBumil,
                                        umurBumil = updatedUserProfilePatient.umurBumil,
                                        alamat = updatedUserProfilePatient.alamat,
                                        namaAyah = updatedUserProfilePatient.namaAyah,
                                        gambarProfile = updatedUserProfilePatient.gambarProfile,
                                        gambarBanner = updatedUserProfilePatient.gambarBanner,
                                        createdAt = updatedUserProfilePatient.createdAt,
                                        updatedAt = updatedUserProfilePatient.updatedAt
                                    )
                                )
                                val intent = Intent(this, OpeningUserProfilePatientReadyActivity::class.java)
                                intent.putExtra(EXTRA_USER_PATIENT_ID_TO_OPENING_USER_PROFILE_PATIENT_READY, userPatientId)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        }
                        is ResultState.Unauthorized -> {
                            viewModel.logout()
                            val intent = Intent(this@OpeningUserProfilePatientFormActivity, MainActivity::class.java)
                            intent.putExtra(EXTRA_FRAGMENT_TO_MAIN_ACTIVITY, "LoginFragment")
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun showButtonPreviousNextAndSave() {
        binding.llBack.visibility = View.INVISIBLE
        binding.llNext.visibility = View.VISIBLE
        binding.btnSave.visibility = View.GONE
        binding.llBack.setOnClickListener {
            binding.viewFlipper.showPrevious()
            binding.llBack.visibility = View.INVISIBLE
            binding.llNext.visibility = View.VISIBLE
            binding.btnSave.visibility = View.GONE
        }
        binding.llNext.setOnClickListener {
            binding.viewFlipper.showNext()
            binding.llBack.visibility = View.VISIBLE
            binding.llNext.visibility = View.INVISIBLE
            binding.btnSave.visibility = View.GONE
        }
    }

    private fun textWatcher() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val namaBumil = binding.tietNamaBumil.text.toString().trim()
                val nikBumil = binding.tietNikBumil.text.toString().trim()
                val tglLahirBumil = binding.tietTglLahirBumil.text.toString().trim()
                val alamat = binding.tietAlamat.text.toString().trim()
                val namaAyah = binding.tietNamaAyah.text.toString().trim()
                val namaAnak = binding.tietNamaAnak.text.toString().trim()
                val nikAnak = binding.tietNikAnak.text.toString().trim()
                val tglLahirAnak = binding.tietTglLahirAnak.text.toString().trim()
                val umurAnak = binding.tietUmurAnak.text.toString().trim()
                val cabang = binding.tietCabang.text.toString().trim()

                val isNamaBumilValid = namaBumil.length >= MIN_CHARACTER_NAMA
                val isNikBumilValid = nikBumil.length == CHARACTER_16
                val isTglLahirBumilNotEmpty = tglLahirBumil.isNotEmpty()
                val alamatIsNotEmpty = alamat.isNotEmpty()
                val namaAyahIsValid = namaAyah.length >= MIN_CHARACTER_NAMA
                val namaAnakIsValid = namaAnak.length >= MIN_CHARACTER_NAMA
                val nikAnakIsValid = nikAnak.length == CHARACTER_16
                val tglLahirAnakIsNotEmpty = tglLahirAnak.isNotEmpty()
                val umurAnakIsNotEmpty = umurAnak.isNotEmpty()
                val cabangIsNotEmpty = cabang.isNotEmpty()

                binding.btnSave.isEnabled = isNamaBumilValid && isNikBumilValid &&
                        isTglLahirBumilNotEmpty && alamatIsNotEmpty && namaAyahIsValid &&
                        namaAnakIsValid && nikAnakIsValid && tglLahirAnakIsNotEmpty &&
                        umurAnakIsNotEmpty && cabangIsNotEmpty

                if (binding.btnSave.isEnabled) {
                    binding.btnSave.visibility = View.VISIBLE
                } else {
                    binding.btnSave.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        }
        binding.tietNamaBumil.addTextChangedListener(textWatcher)
        binding.tietNikBumil.addTextChangedListener(textWatcher)
        binding.tietTglLahirBumil.addTextChangedListener(textWatcher)
        binding.tietAlamat.addTextChangedListener(textWatcher)
        binding.tietNamaAyah.addTextChangedListener(textWatcher)
        binding.tietNamaAnak.addTextChangedListener(textWatcher)
        binding.tietNikAnak.addTextChangedListener(textWatcher)
        binding.tietTglLahirAnak.addTextChangedListener(textWatcher)
        binding.tietUmurAnak.addTextChangedListener(textWatcher)
        binding.tietCabang.addTextChangedListener(textWatcher)
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

    private fun setTglLahirBumil() {
        binding.tietTglLahirBumil.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                val tglLahir = s.toString()
//                Log.e("TEST FORMAT", tglLahir)
                if (tglLahir.isNotEmpty()) {
                    val umur = calculateAge(tglLahir)
                    binding.tietUmurBumil.setText(umur)
                } else {
                    binding.tietUmurBumil.setText("")
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.tiet_tgl_lahir_bumil -> getDatePickerDialogTglLahir(this)
            R.id.tiet_tgl_lahir_anak -> getDatePickerDialogTglLahirAnak(this)
        }
    }


    private fun getUserProfilePatientsWithBranchRelationByIdFromLocal(userPatientId: Int) {
        viewModel.getUserProfilePatientsWithBranchRelationByIdFromLocal(userPatientId)
            .observe(this) { userProfilePatientsWithBranchRelationByIdFromLocal ->
                Log.d(TAG, "onGetUserProfilePatientsWithBranchRelationByIdFromLocal : ${userProfilePatientsWithBranchRelationByIdFromLocal}")
                val branchEntity = userProfilePatientsWithBranchRelationByIdFromLocal.branch
                val userPatientEntity = userProfilePatientsWithBranchRelationByIdFromLocal.userProfilePatients
                userPatientEntity.forEach { item ->
                    binding.tietNamaBumil.setText(item?.namaBumil)
                    binding.tietCabang.setText(branchEntity.namaCabang)
                }
            }
    }

    private fun getUserProfilePatientsFromApi() {
        viewModel.getUserProfilePatientsResult.observe(this) { result ->
            if (result != null) {
                when(result) {
                    is ResultState.Loading -> {  }
                    is ResultState.Error -> {
//                        Log.d(TAG, "getUserProfilePatientsFromApi Error : ${result.error}")
                    }
                    is ResultState.Success -> {
//                        Log.d(TAG, "getUserProfilePatientsFromApi Success : ${result.data}")
                        getUserProfilePatientsWithBranchRelationByIdFromLocal(userPatientId!!)
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(this@OpeningUserProfilePatientFormActivity, MainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    companion object {
        private val TAG = OpeningUserProfilePatientFormActivity::class.java.simpleName
        const val EXTRA_USER_PATIENT_ID_TO_OPENING_USER_PROFILE_PATIENT_FORM = "extra_user_patient_id_to_opening_user_profile_patient_form"
    }
}