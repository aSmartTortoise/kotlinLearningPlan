package com.kotlin.wanandroid.widget

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.kotlin.wanandroid.R

class CustomToast {
    private var mToast: Toast
    private var mTextView: TextView

    constructor(context: Context?, message: String): this(context, message, Toast.LENGTH_SHORT)
    constructor(context: Context?, message: String, duration: Int) {
        mToast = Toast(context)
        mToast.duration = duration
        val view = View.inflate(context, R.layout.toast_custom, null)
        mTextView = view.findViewById(R.id.tv_prompt)
        mTextView.text = message
        mToast.view = view
        mToast.setGravity(Gravity.CENTER, 0, 0)
    }

    fun show() = mToast.show()
}