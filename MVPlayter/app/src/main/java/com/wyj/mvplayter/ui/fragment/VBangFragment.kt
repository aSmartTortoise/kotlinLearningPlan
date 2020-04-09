package com.wyj.mvplayter.ui.fragment


import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.wyj.mvplayter.R
import com.wyj.mvplayter.base.BaseFragment
import org.jetbrains.anko.textColor

/**
 * A simple [Fragment] subclass.
 */
class VBangFragment : BaseFragment() {
    override fun getContentView(): View {
        var tv = TextView(context)
        tv.gravity = Gravity.CENTER
        tv.text = javaClass.simpleName
        tv.textColor = Color.RED
        return tv
    }


}
