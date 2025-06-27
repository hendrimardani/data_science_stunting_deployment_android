package com.example.stunting.ui.nav_drawer_patient_fragment.user_profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.stunting.databinding.NavFragmentUserProfilePatientBinding
import com.example.stunting.ui.ViewModelFactory

class NavUserProfilePatientFragment : Fragment() {
    private var _binding: NavFragmentUserProfilePatientBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<NavUserProfilePatientViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private var userPatientId: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = NavFragmentUserProfilePatientBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPatientId = arguments?.getInt(EXTRA_USER_PATIENT_ID_TO_NAV_USER_PROFILE_FRAGMENT)
        getUserProfilePatientsWithBranchRelationByIdFromLocal()
        getUserProfilePatientWithUserRelationByIdFromLocal()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val TAG = NavUserProfilePatientFragment::class.java.simpleName
        const val EXTRA_USER_PATIENT_ID_TO_NAV_USER_PROFILE_FRAGMENT = "extra_user_patient_id_to_nav_user_profile_fragment"
    }
}