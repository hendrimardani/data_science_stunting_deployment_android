package com.example.stunting.ui.nav_drawer_patient_fragment.user_profile

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.stunting.MyNamaEditText.Companion.MIN_CHARACTER_NAMA
import com.example.stunting.MyNikEditText.Companion.CHARACTER_16
import com.example.stunting.R
import com.example.stunting.ResultState
import com.example.stunting.database.with_api.entities.user_profile_patient.UserProfilePatientEntity
import com.example.stunting.databinding.NavFragmentUserProfilePatientBinding
import com.example.stunting.ui.ContainerMainActivity
import com.example.stunting.ui.ContainerMainActivity.Companion.EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY
import com.example.stunting.ui.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class NavUserProfilePatientFragment : Fragment() {
    private var _binding: NavFragmentUserProfilePatientBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<NavUserProfilePatientViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private var sweetAlertDialog: SweetAlertDialog? = null
    private var userPatientId: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = NavFragmentUserProfilePatientBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPatientId = arguments?.getInt(EXTRA_USER_PATIENT_ID_TO_NAV_USER_PROFILE_PATIENT_FRAGMENT)
        getUserProfilePatientsWithBranchRelationByIdFromLocal()
        getUserProfilePatientWithUserRelationByIdFromLocal()
        textWatcher()
        btnUpdate()
    }

    private fun getUserProfilePatientsWithBranchRelationByIdFromLocal() {
        viewModel.getUserProfilePatientsWithBranchRelationByIdFromLocal(userPatientId!!)
            .observe(requireActivity()) { userProfilePatientsWithBranchRelation ->
                val branch = userProfilePatientsWithBranchRelation.branch
                binding.tietCabang.setText(branch.namaCabang)
        }
    }

    private fun getUserProfilePatientWithUserRelationByIdFromLocal() {
        viewModel.getUserProfilePatientWithUserRelationByIdFromLocal(userPatientId!!)
                .observe(requireActivity()) { userProfilePatientWithUserRelation ->
                val usersEntity = userProfilePatientWithUserRelation.users
                val userProfilePatientEntity = userProfilePatientWithUserRelation.userProfilePatient

                if (usersEntity != null && userProfilePatientEntity != null) {
                    binding.tietNamaBumil.setText(userProfilePatientEntity.namaBumil)
                    binding.tietNikBumil.setText(userProfilePatientEntity.nikBumil)
                    binding.tietTglLahirBumil.setText(userProfilePatientEntity.tglLahirBumil)
                    binding.tietUmurBumil.setText(userProfilePatientEntity.umurBumil)
                    binding.tietNamaAyah.setText(userProfilePatientEntity.namaAyah)
                    binding.tietAlamat.setText(userProfilePatientEntity.alamat)
                }
            }
    }

    private fun textWatcher() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val cabang = binding.tietCabang.text.toString().trim()
                val namaBumil = binding.tietNamaBumil.text.toString().trim()
                val nikBumil = binding.tietNikBumil.text.toString().trim()
                val tglLahirBumil = binding.tietTglLahirBumil.text.toString().trim()
                val alamat = binding.tietAlamat.text.toString().trim()
                val namaAyah = binding.tietNamaAyah.text.toString().trim()

                val cabangIsNotEmpty = cabang.isNotEmpty()
                val isNamaBumilValid = namaBumil.length >= MIN_CHARACTER_NAMA
                val isNikBumilValid = nikBumil.length == CHARACTER_16
                val isTglLahirBumilNotEmpty = tglLahirBumil.isNotEmpty()
                val alamatIsNotEmpty = alamat.isNotEmpty()
                val namaAyahIsValid = namaAyah.length >= MIN_CHARACTER_NAMA

                binding.btnUpdate.isEnabled = cabangIsNotEmpty && isNamaBumilValid &&
                        isNikBumilValid && isTglLahirBumilNotEmpty &&
                        alamatIsNotEmpty && namaAyahIsValid

                if (binding.btnUpdate.isEnabled) {
                    binding.btnUpdate.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.blueSecond))
                } else {
                    binding.btnUpdate.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.buttonDisabledColor))
                    binding.btnUpdate.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        }
        binding.tietCabang.addTextChangedListener(textWatcher)
        binding.tietNamaBumil.addTextChangedListener(textWatcher)
        binding.tietNikBumil.addTextChangedListener(textWatcher)
        binding.tietTglLahirBumil.addTextChangedListener(textWatcher)
        binding.tietAlamat.addTextChangedListener(textWatcher)
        binding.tietNamaAyah.addTextChangedListener(textWatcher)
    }

    private fun btnUpdate() {
        binding.btnUpdate.setOnClickListener {
            val namaCabang = binding.tietCabang.text.toString().trim()
            val namaBumil = binding.tietNamaBumil.text.toString().trim()
            val nikBumil = binding.tietNikBumil.text.toString().trim()
            val tglLahirBumil = binding.tietTglLahirBumil.text.toString().trim()
            val umurBumil = binding.tietUmurBumil.text.toString().trim()
            val alamat = binding.tietAlamat.text.toString().trim()
            val namaAyah = binding.tietNamaAyah.text.toString().trim()

            updateUserProfilePatientByIdFromApi(
                namaCabang, namaBumil, nikBumil, tglLahirBumil, umurBumil, alamat, namaAyah
            )
        }
    }

    private fun updateUserProfilePatientByIdFromApi(
        namaCabang: String, namaBumil: String, nikBumil: String, tglLahirBumil: String,
        umurBumil: String, alamat: String, namaAyah: String
    ) {
        val progressBar = SweetAlertDialog(requireActivity(), SweetAlertDialog.PROGRESS_TYPE)
        progressBar.setTitleText(getString(R.string.title_loading))
        progressBar.setContentText(getString(R.string.description_loading))
            .progressHelper.barColor = Color.parseColor("#73D1FA")
        progressBar.setCancelable(false)

        viewModel.updateUserProfilePatientByIdFromApi(
            userPatientId!!, namaCabang, namaBumil, nikBumil, tglLahirBumil, umurBumil, alamat, namaAyah
        ).observe(requireActivity()) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> progressBar.show()
                    is ResultState.Error -> {
                        progressBar.dismiss()
//                        Log.d(TAG, "onUpdateUserProfilePatientByIdFromApi : Error ${result.error}")
                        sweetAlertDialog = SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                        sweetAlertDialog!!.setTitleText(getString(R.string.title_validation_error))
                        sweetAlertDialog!!.setContentText(result.error)
                        sweetAlertDialog!!.setCancelable(false)
                        sweetAlertDialog!!.show()
                    }
                    is ResultState.Success -> {
                        progressBar.dismiss()
//                            Log.d(TAG, "onUpdateUserProfilePatientByIdFromApi : Success ${result.data}")
                        val dataUpdateUserProfilePatientById = result.data?.dataUpdateUserProfilePatientById
                        if (dataUpdateUserProfilePatientById != null) {
                            viewModel.updateUserProfilePatientByIdFromLocal(
                                UserProfilePatientEntity(
                                    id = dataUpdateUserProfilePatientById.id,
                                    userPatientId = dataUpdateUserProfilePatientById.userPatientId,
                                    branchId = dataUpdateUserProfilePatientById.branchId,
                                    namaBumil = dataUpdateUserProfilePatientById.namaBumil,
                                    nikBumil = dataUpdateUserProfilePatientById.nikBumil,
                                    tglLahirBumil = dataUpdateUserProfilePatientById.tglLahirBumil,
                                    umurBumil = dataUpdateUserProfilePatientById.umurBumil,
                                    alamat = dataUpdateUserProfilePatientById.alamat,
                                    namaAyah = dataUpdateUserProfilePatientById.namaAyah,
                                    gambarProfile = dataUpdateUserProfilePatientById.gambarProfile,
                                    gambarBanner = dataUpdateUserProfilePatientById.gambarBanner,
                                    createdAt = dataUpdateUserProfilePatientById.createdAt,
                                    updatedAt = dataUpdateUserProfilePatientById.updatedAt
                                )
                            )
                            Snackbar.make(binding.root, "Data berhasil diubah", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    is ResultState.Unauthorized -> {
                        viewModel.logout()
                        val intent = Intent(requireActivity(), ContainerMainActivity::class.java)
                        intent.putExtra(EXTRA_FRAGMENT_TO_CONTAINER_MAIN_ACTIVITY, "LoginFragment")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val TAG = NavUserProfilePatientFragment::class.java.simpleName
        const val EXTRA_USER_PATIENT_ID_TO_NAV_USER_PROFILE_PATIENT_FRAGMENT = "extra_user_patient_id_to_nav_user_profile_patient_fragment"
    }
}