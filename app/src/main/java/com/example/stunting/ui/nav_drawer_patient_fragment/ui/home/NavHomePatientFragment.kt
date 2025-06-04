package com.example.stunting.ui.nav_drawer_patient_fragment.ui.home

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextPaint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.stunting.R
import com.example.stunting.databinding.NavFragmentHomePatientBinding
import com.example.stunting.utils.VectorDrawableTagItems
import com.magicgoop.tagsphere.OnTagTapListener
import com.magicgoop.tagsphere.item.TagItem
import com.magicgoop.tagsphere.item.TextTagItem
import kotlin.random.Random

class NavHomePatientFragment : Fragment() {
    private var _binding: NavFragmentHomePatientBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homePatientViewModel =
            ViewModelProvider(this).get(NavHomePatientViewModel::class.java)

        _binding = NavFragmentHomePatientBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTagSphereView()
    }

    private fun getVectorDrawable(id: Int): Drawable? = ContextCompat.getDrawable(requireContext(), id)

    private fun setupTagSphereView() {
        val tags = mutableListOf<VectorDrawableTagItems>()

        val drawableResList = listOf(
            R.drawable.ic_bumil,
            R.drawable.ic_anak,
            R.drawable.ic_remaja_putri,
            R.drawable.ic_calon_pengantin,
            R.drawable.ic_keluarga,
            R.drawable.ic_cegah_stunting
        )

        (0..0).map {
            drawableResList.forEach { id ->
                getVectorDrawable(id)?.let { drawable ->
                    tags.add(VectorDrawableTagItems(drawable, id))
                }
            }
        }

        binding.tagSphereView.addTagList(tags)
        binding.tagSphereView.setRadius(0.2f)
        binding.tagSphereView.setOnTagTapListener(object : OnTagTapListener {
            override fun onTap(tagItem: TagItem) {
                if (tagItem is VectorDrawableTagItems) {
                    val resName = resources.getResourceEntryName(tagItem.resId)
                    when (resName) {
                        "ic_bumil" -> {

                        }
                        "ic_anak" -> {

                        }
                        "ic_remaja_putri" -> {

                        }
                        "ic_calon_pengantin" -> {

                        }
                        "ic_keluarga" -> {

                        }
                        "ic_cegah_stunting" -> {

                        }
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val TAG = NavHomePatientFragment::class.java.simpleName
    }
}