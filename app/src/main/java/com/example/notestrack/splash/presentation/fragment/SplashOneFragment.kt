package com.example.notestrack.splash.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.notestrack.R
import com.example.notestrack.databinding.FragmentSplashOneBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashOneFragment : Fragment() {

    private val binding:FragmentSplashOneBinding by lazy { FragmentSplashOneBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState)

    }
}