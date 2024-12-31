package com.example.notestrack.splash.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.notestrack.R
import com.example.notestrack.databinding.FragmentSplashTwoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashTwoFragment : Fragment() {

    private val binding: FragmentSplashTwoBinding by lazy { FragmentSplashTwoBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState)

    }
}