package com.example.stunting.ui.nav_drawer_patient_fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.stunting.databinding.NavFragmentSettingsPatientBinding

class NavSettingsPatientFragment : Fragment() {

    private var _binding: NavFragmentSettingsPatientBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val navSettingsPatientViewModel =
            ViewModelProvider(this).get(NavSettingsPatientViewModel::class.java)

        _binding = NavFragmentSettingsPatientBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        navSettingsPatientViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}