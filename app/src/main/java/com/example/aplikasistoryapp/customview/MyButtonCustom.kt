package com.example.aplikasistoryapp.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.example.aplikasistoryapp.R
import com.google.android.material.button.MaterialButton

class MyButtonCustom @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialButton(context, attrs, defStyleAttr) {

    private lateinit var enabledBackground: Drawable
    private lateinit var disabledBackground: Drawable
    private var enabledTextColor: Int = 0
    private var disabledTextColor: Int = 0

    init {
        initAttributes(context, attrs)
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isEnabled) enabledBackground else disabledBackground
        setTextColor(if (isEnabled) enabledTextColor else disabledTextColor)
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyButtonCustom)
        enabledTextColor = typedArray.getColor(
            R.styleable.MyButtonCustom_enabledTextColor,
            ContextCompat.getColor(context, android.R.color.background_light)
        )
        disabledTextColor = typedArray.getColor(
            R.styleable.MyButtonCustom_disabledTextColor,
            ContextCompat.getColor(context, android.R.color.darker_gray)
        )
        typedArray.recycle()
    }

    private fun init() {
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.button_background) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.button_disabled_background) as Drawable
    }
}
