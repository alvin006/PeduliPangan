package com.alvintio.pedulipangan.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alvintio.pedulipangan.databinding.ActivityOnboardingBinding
import com.alvintio.pedulipangan.util.ViewUtils

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewUtils.setupFullScreen(this)

        binding.btnSkip.setOnClickListener {
            ViewUtils.moveActivityNoHistory(this, WelcomeActivity::class.java)
        }

        binding.btnStart.setOnClickListener {
            ViewUtils.moveActivityNoHistory(this, WelcomeActivity::class.java)
        }
    }
}