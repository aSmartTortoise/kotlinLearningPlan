package com.wyj.mvplayter.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.wyj.mvplayter.R
import com.wyj.mvplayter.base.BaseActivity
import com.wyj.mvplayter.utils.ToolbarManager
import org.jetbrains.anko.find

class AboutActivity : BaseActivity(), ToolbarManager {

    override val mToolbar: Toolbar by lazy {
        find<Toolbar>(R.id.tool_bar)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_about
    }

    override fun initView() {
        super.initView()
        initAboutToolbar()
    }

}
