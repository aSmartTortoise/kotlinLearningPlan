package com.kotlin.wanandroid.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.kotlin.wanandroid.R
import com.kotlin.wanandroid.base.BaseMVPActivity
import com.kotlin.wanandroid.constant.Constant
import com.kotlin.wanandroid.event.LoginEvent
import com.kotlin.wanandroid.ext.showToast
import com.kotlin.wanandroid.mvp.contract.RegisterContract
import com.kotlin.wanandroid.mvp.model.bean.LoginData
import com.kotlin.wanandroid.mvp.presenter.RegistPresenter
import com.kotlin.wanandroid.utils.DialogUtil
import com.kotlin.wanandroid.utils.Preference
import kotlinx.android.synthetic.main.activity_regist.*
import kotlinx.android.synthetic.main.toolbar.*
import org.greenrobot.eventbus.EventBus

class RegistActivity : BaseMVPActivity<RegisterContract.View, RegisterContract.Presenter>(),
    RegisterContract.View {

    private val mDialog by lazy {
        DialogUtil.getWaitDialog(this@RegistActivity, getString(R.string.register_ing))
    }

    private var mUserName: String by Preference(Constant.USERNAME_KEY, "")
    private var mPwd: String by Preference(Constant.PASSWORD_KEY, "")

    private val mOnClickListener = View.OnClickListener { v ->
        when(v.id) {
            R.id.btn_register -> {
                regist()
            }

            R.id.tv_sign_in  -> {
                Intent(this@RegistActivity, LoginActivity::class.java).run {
                    startActivity(this)
                    finish()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            }
        }
    }

    private fun regist() {
        if (validate()) {
            mPresenter?.regist(
                et_username.text.trim().toString(),
                et_password.text.trim().toString(),
                et_password2.text.trim().toString()
            )
        }
    }

    private fun validate(): Boolean {
        val username: String = et_username.text.toString()
        val password: String = et_password.text.toString()
        val password2: String = et_password2.text.toString()
        if (username.isEmpty()) {
            et_username.error = getString(R.string.username_not_empty)
            return false
        }
        if (password.isEmpty()) {
            et_password.error = getString(R.string.password_not_empty)
            return false
        }
        if (password2.isEmpty()) {
            et_password2.error = getString(R.string.confirm_password_not_empty)
            return false
        }
        if (password != password2) {
            et_password2.error = getString(R.string.password_cannot_match)
            return false
        }

        return true
    }

    override fun attachLayoutRes() = R.layout.activity_regist

    override fun useEventBus() = false

    override fun initView() {
        super.initView()
        toolbar.run {
            title = resources.getString(R.string.register)
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        btn_register.setOnClickListener(mOnClickListener)
        tv_sign_in.setOnClickListener(mOnClickListener)
    }

    override fun enableNetworkTip() = false

    override fun initData() {
    }

    override fun start() {
    }

    override fun createPrenter(): RegisterContract.Presenter = RegistPresenter()

    override fun registSuccess(data: LoginData) {
        showToast(getString(R.string.register_success))
        mIsLogin = true
        mUserName = data.username
        mPwd = data.password
        EventBus.getDefault().post(LoginEvent(true))
        finish()
    }

    override fun registFail() {
        mIsLogin = false
    }

    override fun showLoading() {
        mDialog.show()
    }

    override fun hideLoading() {
        mDialog.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("RegistActivity", "onCreate: wyj")

    }
}