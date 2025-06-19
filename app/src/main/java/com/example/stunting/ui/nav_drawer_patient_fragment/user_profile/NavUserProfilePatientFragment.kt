package com.example.stunting.ui.nav_drawer_patient_fragment.user_profile

import android.os.Bundle
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = NavFragmentUserProfilePatientBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}