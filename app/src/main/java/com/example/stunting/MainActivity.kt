package com.example.stunting

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stunting.databinding.ActivityMainBinding
import com.example.stunting.ml.ModelStunting
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.IOException
import java.nio.BufferOverflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etUmur = binding.etUmur.text
        val etJk = binding.etJk.text
        val etTinggi = binding.etTinggi.text

        binding.btnSubmit.setOnClickListener {
            if ( etUmur.isNotEmpty() && etJk.isNotEmpty() && etTinggi.isNotEmpty() ) {
                // Get data
                val umur = etUmur.toString().toFloat()
                val jk = etJk.toString().toFloat()
                val tinggi = etTinggi.toString().toFloat()
                prediction(umur, jk, tinggi)
            } else {
                toastInfo("Gagal ☹️",
                    "Tidak boleh ada data yang kosong !", MotionToastStyle.ERROR)
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun prediction(umur: Float, jk: Float, tinggi: Float) {
        // Normalisasi data with formula => (Xi - Xmin) / (Xmax - Xmin)
        val umurNormalized = (umur - 0f) / (60f - 0f)
        val tinggiNormalized = (tinggi - 40.01f) / (128f - 40.0104370037594f)

        try { // this is apropriated by input and output value, 3 * 4 It depends on memory allocated
            val byteBuffer = ByteBuffer.allocateDirect(3 * 4)
                byteBuffer.order(ByteOrder.nativeOrder())
            byteBuffer.putFloat(umurNormalized)
            byteBuffer.putFloat(jk)
            byteBuffer.putFloat(tinggiNormalized)

            val model = ModelStunting.newInstance(this)

            // Creates inputs for reference.
            val inputFeature0 =
                TensorBuffer.createFixedSize(intArrayOf(1, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(byteBuffer)

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

            // If the result value is less than 0.5 then Normal, otherwise is Stunting
            if ( Math.round(outputFeature0[0]) < 0.5 ) {
                toastInfo("Selamat kondisi bayi anda normal 😍",
                    "Pertahankan gizinya !!!.", MotionToastStyle.SUCCESS)
                binding.tvHasil.text = "Hasil: Normal"
            } else {
                toastInfo("Kondisi bayi anda terkena stunting ☹️",
                    "Perbaiki lagi asupan gizi anaknya !!!", MotionToastStyle.ERROR)
                binding.tvHasil.text = "Hasil: Stunting"
            }
            // Releases model resources if no longer used.
            model.close()
        } catch (e: IOException) {
            // TODO Handle the exception
        }
    }

    private fun toastInfo(title: String, description: String, info: MotionToastStyle) {
        MotionToast.createToast(this,
            title,
            description,
            info,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helveticabold))
    }
}