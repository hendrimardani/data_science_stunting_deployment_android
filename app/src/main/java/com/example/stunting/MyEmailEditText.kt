package com.example.stunting

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat

class MyEmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var showImage: Drawable

    init {
        showImage = ContextCompat.getDrawable(context, R.drawable.ic_error_outline_24) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val pattern = context.getString(R.string.pattern)
                if (!s.contains(Regex(pattern))) {
                    error = resources.getString(R.string.error_description_email_validation)
                }
            }
            override fun afterTextChanged(s: Editable) { }
        })
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }
}