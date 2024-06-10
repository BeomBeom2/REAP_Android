package com.example.reap_service.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reap_service.databinding.FragmentCalendarBinding

class CalendarFragment  : Fragment(){
    lateinit var binding: FragmentCalendarBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(layoutInflater)

        return binding.root
    }
}