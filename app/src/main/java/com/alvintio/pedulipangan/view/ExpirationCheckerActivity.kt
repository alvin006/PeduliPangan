package com.alvintio.pedulipangan.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alvintio.pedulipangan.databinding.ActivityExpirationCheckerBinding
import com.alvintio.pedulipangan.util.ViewUtils

class ExpirationCheckerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExpirationCheckerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpirationCheckerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBreadChecker.setOnClickListener {
            val intent = Intent(this, ExpirationCheckerBreadActivity::class.java)
            startActivity(intent)
        }

        binding.btnFruitChecker.setOnClickListener {
            val intent = Intent(this, ExpirationCheckerFruitActivity::class.java)
            startActivity(intent)
        }

        ViewUtils.setupFullScreen(this)
    }
}
