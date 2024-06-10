package com.example.reap_service

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reap_service.databinding.ActivityMainBinding
import com.example.reap_service.home.HomeFragment
import com.example.reap_service.login.LoginActivity
import com.example.reap_service.recording.Recording

class MainActivity : AppCompatActivity(), ResetListener{
    private lateinit var binding : ActivityMainBinding
    private var inputRole : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        /*TODO
        *  몽고 DB로 ID 가져오기
        *
        */
        val userData : Int


        binding.navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commit()
                    return@setOnItemSelectedListener true
                }
                else -> {
                    return@setOnItemSelectedListener true
                }
            }
        }
        binding.navigation.selectedItemId = R.id.menu_home
    }

    override fun onReset() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

}
interface ResetListener {
    fun onReset()
}

