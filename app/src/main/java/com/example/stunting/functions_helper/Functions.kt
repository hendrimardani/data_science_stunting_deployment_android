package com.example.stunting.functions_helper

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.LeadingMarginSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import com.example.stunting.R
import com.example.stunting.databinding.DialogCustomeInfoBinding
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Functions {
    private lateinit var cal: Calendar
    private lateinit var dataSetListenerTgllahir: DatePickerDialog.OnDateSetListener

    fun parseTextWithStylesAndRemoveSymbols(input: String): SpannableString {
        val spannableBuilder = SpannableStringBuilder(input)

        // Proses untuk Unordered List dengan simbol •
        val unorderedListRegex = "(?m)^\\*\\s(.*?)$".toRegex() // Regex untuk mendeteksi simbol * di awal baris
        val unorderedListMatches = unorderedListRegex.findAll(spannableBuilder).toList()

        unorderedListMatches.reversed().forEach { match -> // Proses dari belakang ke depan untuk menghindari konflik indeks
            val listItemText = match.groupValues[1] // Mengambil teks setelah simbol *
            val start = match.range.first
            val end = match.range.last + 1

            // Format teks dengan simbol bullet (•), tambahkan \n untuk memastikan pindah baris
            val bulletText = "• $listItemText\n"

            // Ganti simbol * dengan teks yang berisi bullet
            spannableBuilder.replace(start, end, bulletText)

            // Terapkan LeadingMarginSpan untuk memberikan indentasi pada list item
            spannableBuilder.setSpan(
                LeadingMarginSpan.Standard(40), // Memberikan indentasi ke kiri
                start,
                start + bulletText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Proses untuk Bold (**)
        val boldRegex = "\\*\\*(.*?)\\*\\*".toRegex()
        val boldMatches = boldRegex.findAll(spannableBuilder).toList()
        boldMatches.reversed().forEach { match ->
            val boldText = match.groupValues[1]
            val start = match.range.first
            val end = match.range.last + 1

            spannableBuilder.replace(start, end, boldText)
            spannableBuilder.setSpan(
                StyleSpan(Typeface.BOLD),
                start,
                start + boldText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Proses untuk Italic dengan simbol _
        val italicRegex = "_(.*?)_".toRegex()
        val italicMatches = italicRegex.findAll(spannableBuilder).toList()
        italicMatches.reversed().forEach { match ->
            val italicText = match.groupValues[1]
            val start = match.range.first
            val end = match.range.last + 1

            spannableBuilder.replace(start, end, italicText)
            spannableBuilder.setSpan(
                StyleSpan(Typeface.ITALIC),
                start,
                start + italicText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Kembalikan hasil sebagai SpannableString
        return SpannableString(spannableBuilder)
    }

    fun showCustomeInfoDialog(context: Context, layoutInflater: LayoutInflater) {
        val bindingBumilBottomSheetDialog = DialogCustomeInfoBinding.inflate(layoutInflater)
        val infoDialog = Dialog(context)
        // Check if the view already has a parent
        val viewBottomSheetDialog: View = bindingBumilBottomSheetDialog.root

        infoDialog.setContentView(viewBottomSheetDialog)
        infoDialog.setCanceledOnTouchOutside(false)
        infoDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Set content
        bindingBumilBottomSheetDialog.tvDescription.text = context.getString(R.string.description_detail_info)
        bindingBumilBottomSheetDialog.tvYes.setOnClickListener {
            infoDialog.dismiss()
        }
        infoDialog.show()
    }

    fun linkToDirectory(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        activity.startActivityForResult(intent, 101) // Replace REQUEST_CODE with a unique code (free numeric)
    }

    fun toastInfo(activity: Activity, title: String, description: String, info: MotionToastStyle) {
        MotionToast.createToast(activity,
            title,
            description,
            info,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(activity, www.sanju.motiontoast.R.font.helveticabold)
        )
    }

    fun setCalendarTglLahir(etTanggal: EditText) {
        cal = Calendar.getInstance()
        dataSetListenerTgllahir = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInViewTglLahir(etTanggal)
        }
        updateDateInViewTglLahir(etTanggal)
    }

    private fun updateDateInViewTglLahir(etTanggal: EditText) {
        val myFormat = "yyyy/MM/dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        etTanggal.setText(sdf.format(cal.time).toString())
    }

    fun getDatePickerDialogTglLahir(context: Context) {
        DatePickerDialog(context, dataSetListenerTgllahir, cal.get(Calendar.YEAR), cal.get(
            Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun getDateTimePrimaryKey(): String {
        val c = Calendar.getInstance()
        val dateTime = c.time

        // 10-03-2024 Min 14:59:11
        val sdf = SimpleDateFormat("dd-MM-yyyy EEE HH:mm:ss", Locale.getDefault())
        val date = sdf.format(dateTime)
        return date
    }

}