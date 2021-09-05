package com.kotlin.wanandroid.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.kotlin.wanandroid.R
import com.kotlin.wanandroid.base.BaseMVPActivity
import com.kotlin.wanandroid.constant.Constant
import com.kotlin.wanandroid.event.LoginEvent
import com.kotlin.wanandroid.ext.showToast
import com.kotlin.wanandroid.mvp.contract.LoginContract
import com.kotlin.wanandroid.mvp.model.bean.LoginData
import com.kotlin.wanandroid.mvp.presenter.LoginPresenter
import com.kotlin.wanandroid.utils.DialogUtil
import com.kotlin.wanandroid.utils.Preference
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.toolbar.*
import org.greenrobot.eventbus.EventBus

class LoginActivity : BaseMVPActivity<LoginContract.View, LoginContract.Presenter>(), LoginContract.View {

    private var mUserName: String by Preference(Constant.USERNAME_KEY, "")
    private var mPwd: String by Preference(Constant.PASSWORD_KEY, "")
    private var mToken: String by Preference(Constant.TOKEN_KEY, "")

    private val mDialog by lazy {
        DialogUtil.getWaitDialog(this, getString(R.string.login_ing))
    }

    private val mOnClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.btn_login -> {
                login()
            }

            R.id.tv_sign_up -> {
                showMsg("去注册")
            }
        }
    }

    private fun login() {
        if (validate()) {
            mPresenter?.login(
                et_username.text.trim().toString(),
                et_password.text.trim().toString())
        }
    }

    private fun validate(): Boolean {
        var valid = true
        val username: String = et_username.text.toString()
        val password: String = et_password.text.toString()

        if (username.isEmpty()) {
            et_username.error = getString(R.string.username_not_empty)
            return false
        }

        if (password.isEmpty()) {
            et_password.error = getString(R.string.password_not_empty)
            return false
        }

        return valid
    }

    override fun useEventBus(): Boolean = false

    override fun createPrenter(): LoginContract.Presenter = LoginPresenter()

    override fun attachLayoutRes(): Int = R.layout.activity_login

    override fun initData() {

    }

    override fun enableNetworkTip() = false

    override fun start() {
    }

    override fun loginSuccess(data: LoginData) {
        showToast(getString(R.string.login_success))
        mIsLogin = true
        mUserName = data.username
        mPwd = data.password
        mToken = data.token
        EventBus.getDefault().post(LoginEvent(true))
        finish()
    }

    override fun loginFail() {
    }

    override fun showLoading() {
        mDialog.show()
    }

    override fun hideLoading() {
        mDialog.hide()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LoginActivity", "onCreate: wyj")
    }

    override fun initView() {
        super.initView()
        et_username.setText(mUserName)
        toolbar.run {
            title = resources.getString(R.string.login)
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        btn_login.setOnClickListener(mOnClickListener)
        tv_sign_up.setOnClickListener(mOnClickListener)

    }

}