package com.example.notestrack.profile.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.notestrack.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val binding: FragmentProfileBinding by lazy { FragmentProfileBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState)

        binding.bindState(
        )
    }

    private fun FragmentProfileBinding.bindState() {

        onClickListener()
    }

    private fun FragmentProfileBinding.onClickListener() {
        cvEdit.setOnClickListener {
            editProfile.isVisible = !editProfile.isVisible
            ivEditUserImage.text = ivUserImage.text.toString()
            evUser.setText(tvUserName.text.toString())
        }
    }
}