package com.example.reap_service.agreement_conditions

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reap_service.MainActivity
import com.example.reap_service.databinding.ActivityAgreementConditionsBinding

private lateinit var binding: ActivityAgreementConditionsBinding

class AgreementConditionActivity  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgreementConditionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.agreeRadioButton.setOnCheckedChangeListener { group, checkedId ->
            // Enable button when any radio button is selected
            binding.submitConsentButton.isEnabled = (checkedId == true)
        }

        binding.submitConsentButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}