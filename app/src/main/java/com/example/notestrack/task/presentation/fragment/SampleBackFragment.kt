package com.example.notestrack.task.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.example.notestrack.databinding.FragmentSampleBackBinding
import com.example.notestrack.utils.FRAGMENT_RESULT
import com.example.notestrack.utils.RESULT_CANCELED
import com.example.notestrack.utils.RESULT_OK
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SampleBackFragment : Fragment() {

    private val binding: FragmentSampleBackBinding by lazy { FragmentSampleBackBinding.inflate(layoutInflater) }

    private var from :String? = ""
    private var savedStateHandle: SavedStateHandle? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        from = arguments?.getString("EXTRA","unknown")

        println("findNavController().currentBackStackEntry?.savedStateHandle sample >$from")

//        savedStateHandle = when(from){
//            "popup"-> findNavController().previousBackStackEntry?.savedStateHandle
//            else->null
//        }
//        savedStateHandle?.set(FRAGMENT_RESULT, RESULT_CANCELED)

        binding.root.setOnClickListener{
            when(from){
//                "popup"-> savedStateHandle?.set(FRAGMENT_RESULT, RESULT_OK)
            }
            findNavController().popBackStack()
        }
    }
}