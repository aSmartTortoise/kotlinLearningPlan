package com.wyj.mvplayter.ui.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import com.wyj.mvplayter.R
import com.wyj.mvplayter.base.BaseActivity
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity

class SplashActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun initData() {
        super.initData()
        val animatorX = ObjectAnimator.ofFloat(iv, "scaleX", 1.5f, 1.0f).setDuration(500)
        val animatorY = ObjectAnimator.ofFloat(iv, "scaleY", 1.5f, 1.0f).setDuration(500)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animatorX, animatorY)

        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
                startActivityAndFinish<MainActivity>()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })
        animatorSet.start()

    }

}
