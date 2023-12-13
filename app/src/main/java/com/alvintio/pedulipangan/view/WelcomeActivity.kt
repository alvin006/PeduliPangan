package com.alvintio.pedulipangan.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.alvintio.pedulipangan.databinding.ActivityWelcomeBinding
import com.alvintio.pedulipangan.util.ViewUtils

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var appIcon: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        appIcon = findViewById(R.id.iv_welcome_image)
//        appIcon.setAnimation(R.raw.app_logo)
//        appIcon.playAnimation()

        ViewUtils.setupFullScreen(this)
        playAnimation()
        setupAction()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            ViewUtils.moveActivity(this, LoginActivity::class.java)
        }
        binding.btnRegister.setOnClickListener {
            ViewUtils.moveActivity(this, RegisterActivity::class.java)
        }
    }

    private fun playAnimation() {
        val icon = ObjectAnimator.ofFloat(binding.iconDescription, View.ALPHA, 1f).setDuration(300)
        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(300)
        val subtitle =
            ObjectAnimator.ofFloat(binding.tvSubtitle, View.ALPHA, 1f).setDuration(300)
        val register =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(300)
        val login =
            ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(
                icon,
                title,
                subtitle,
                register,
                login
            )
            startDelay = 100
        }.start()
    }
}