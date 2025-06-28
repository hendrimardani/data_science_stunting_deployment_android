package com.example.stunting.ui.nav_drawer_patient_fragment.daftar_anak

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.stunting.R
import com.example.stunting.database.with_api.entities.children_patient.ChildrenPatientEntity
import com.example.stunting.databinding.NavFragmentDaftarAnakPatientBinding
import com.example.stunting.ui.ViewModelFactory
import com.example.stunting.ui.detail_anak_patient.DetailAnakPatient
import com.example.stunting.ui.detail_anak_patient.DetailAnakPatient.Companion.EXTRA_CHILDREN_PATIENT_ID_TO_DETAIL_ANAK_PATIENT_ACTIVITY


class NavDaftarAnakPatientFragment : Fragment() {
    private var _binding: NavFragmentDaftarAnakPatientBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<NavDaftarAnakPatientViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private var userPatientId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = NavFragmentDaftarAnakPatientBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPatientId = arguments?.getInt(EXTRA_USER_PATIENT_ID_TO_NAV_DAFTAR_ANAK_PATIENT_FRAGMENT)
        getChildrenPatientByUserPatientIdFromLocal()
    }

    private fun getChildrenPatientByUserPatientIdFromLocal() {
        viewModel.getChildrenPatientByUserPatientIdFromLocal(userPatientId!!)
            .observe(requireActivity()) { childrenPatientList ->
                setupTableLayout(childrenPatientList)
            }
    }

    @SuppressLint("SetTextI18n")
    private fun setupTableLayout(childrenPatientList: List<ChildrenPatientEntity>) {
        childrenPatientList.forEach { item ->
            val tableRow = TableRow(requireActivity())

            val namaTextView = TextView(requireActivity()).apply {
                text = item.namaAnak
                setPadding(8, 8, 8, 8)
            }
            val nikTextView = TextView(requireActivity()).apply {
                text = item.nikAnak
                setPadding(8, 8, 8, 8)
            }
            val jenisKelaminTextView = TextView(requireActivity()).apply {
                text = item.jenisKelaminAnak
                setPadding(8, 8, 8, 8)
            }
            val tglLahirTextView = TextView(requireActivity()).apply {
                text = item.tglLahirAnak
                setPadding(8, 8, 8, 8)
            }
            val umurTextView = TextView(requireActivity()).apply {
                text = item.umurAnak
                setPadding(8, 8, 64, 8)
            }
            val editDataTextView = TextView(requireContext()).apply {
                text = "Edit"
                setTextColor(ContextCompat.getColor(context, R.color.orange))
                setPadding(8, 8, 8, 8)
                background = ContextCompat.getDrawable(requireContext(), R.drawable.stroke_orange_background)
                setOnClickListener {
                    val intent = Intent(requireActivity(), DetailAnakPatient::class.java)
                    intent.putExtra(EXTRA_CHILDREN_PATIENT_ID_TO_DETAIL_ANAK_PATIENT_ACTIVITY, item.id)
                    startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle())
                }
            }
            tableRow.addView(namaTextView)
            tableRow.addView(nikTextView)
            tableRow.addView(jenisKelaminTextView)
            tableRow.addView(tglLahirTextView)
            tableRow.addView(umurTextView)
            tableRow.addView(editDataTextView)
            binding.tableLayout.addView(tableRow)
        }
    }

    companion object {
        private val TAG = NavDaftarAnakPatientFragment::class.java.simpleName
        const val EXTRA_USER_PATIENT_ID_TO_NAV_DAFTAR_ANAK_PATIENT_FRAGMENT = "extra_user_patient_id_to_nav_anak_patient_fragment"
    }
}