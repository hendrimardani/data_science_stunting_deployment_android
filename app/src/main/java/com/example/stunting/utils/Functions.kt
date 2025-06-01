package com.example.stunting.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.LeadingMarginSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.stunting.BuildConfig
import com.example.stunting.R
import com.example.stunting.databinding.DialogCustomeInfoBinding
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Functions {
    private lateinit var cal: Calendar
    private lateinit var dataSetListenerTgllahir: DatePickerDialog.OnDateSetListener

    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
    private const val MAXIMAL_SIZE = 1000000

    // Supaya tidak melakukan observe secara terus meneurs infinite loop
    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value)
                removeObserver(this)
            }
        })
    }

    private fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        return File.createTempFile(timeStamp, ".jpg", filesDir)
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height, matrix, true
        )
    }

    fun File.reduceFileImage(): File {
        val file = this
        val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }


    private fun Bitmap.getRotatedBitmap(file: File): Bitmap? {
        val orientation = ExifInterface(file).getAttributeInt(
            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
        )
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
            ExifInterface.ORIENTATION_NORMAL -> this
            else -> this
        }
    }

    fun uriToFile(imageUri: Uri, context: Context): File {
        val myFile = createCustomTempFile(context)
        val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024) // Max 1 MB
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
        outputStream.close()
        inputStream.close()
        return myFile
    }

    fun getImageUri(context: Context): Uri {
        var uri: Uri? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
            }
            uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
        }
        return uri ?: getImageUriForPreQ(context)
    }

    private fun getImageUriForPreQ(context: Context): Uri {
        val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
        if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            imageFile
        )
    }

    fun formatToHourMinute(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val date: Date? = inputFormat.parse(dateString)
        return date?.let { outputFormat.format(it) } ?: ""
    }

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
        return SpannableString(spannableBuilder)
    }

    fun showCustomeInfoDialog(context: Context, layoutInflater: LayoutInflater) {
        val bindingBumilBottomSheetDialog = DialogCustomeInfoBinding.inflate(layoutInflater)
        val infoDialog = Dialog(context)
        val viewBottomSheetDialog: View = bindingBumilBottomSheetDialog.root

        infoDialog.setContentView(viewBottomSheetDialog)
        infoDialog.setCanceledOnTouchOutside(false)
        infoDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bindingBumilBottomSheetDialog.tvDescription.text = context.getString(R.string.description_detail_info)
        bindingBumilBottomSheetDialog.tvYes.setOnClickListener {
            infoDialog.dismiss()
        }
        infoDialog.show()
    }

    fun linkToDirectory(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        activity.startActivityForResult(intent, 101)
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

    fun calculateAge(birthDateString: String): String {
        // Format tanggal (ubah format jika perlu)
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val birthDate = LocalDate.parse(birthDateString, formatter)

        // Tanggal hari ini
        val today = LocalDate.now()

        // Hitung umur
        val period = Period.between(birthDate, today)
//        Log.e("TEST RESULT PERIOD ", period.toString())

        return "${period.years} tahun, ${period.months} bulan"
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