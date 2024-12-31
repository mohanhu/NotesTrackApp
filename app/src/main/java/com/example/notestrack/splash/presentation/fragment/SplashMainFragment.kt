package com.example.notestrack.splash.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.notestrack.R
import com.example.notestrack.databinding.FragmentSplashMainBinding
import com.example.notestrack.splash.presentation.viewmodel.SplashUiState
import com.example.notestrack.splash.presentation.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SplashMainFragment : Fragment() {

    private val binding: FragmentSplashMainBinding by lazy { FragmentSplashMainBinding.inflate(layoutInflater) }

    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bindState(
            viewModel.uiState
        )
    }

    private fun FragmentSplashMainBinding.bindState(uiState: StateFlow<SplashUiState>) {

        bindTabLayOut(uiState)

    }

    private fun FragmentSplashMainBinding.bindTabLayOut(uiState: StateFlow<SplashUiState>) {

        val chipList = listOf(chip1,chip2,chip3)

        chipList.forEach { chip ->
            chip.setOnClickListener{
                chipList.forEachIndexed { i, radioButton ->
                    radioButton.isChecked = i == vpFrame.currentItem
                }
            }
        }

        vpFrame.isSaveEnabled = false
        val adapter= SplashViewPagerAdapter(
            childFragmentManager,
            viewLifecycleOwner.lifecycle,
            listOf(
                SplashOneFragment(),
                SplashTwoFragment(),
                Splash3Fragment()
            )
        )
        vpFrame.adapter = adapter
        vpFrame.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                chipList.get(position).isChecked = true
                viewModel.updateCurrentScrollIndex(position)
            }
        })
        val checkedListener = android.widget.CompoundButton.OnCheckedChangeListener { button, isChecked ->
            if (isChecked){
                when (button.id) {
                    R.id.chip1 -> {
                        vpFrame.currentItem = 0
                    }
                    R.id.chip2 -> {
                        vpFrame.currentItem = 1
                    }
                    R.id.chip3 -> {
                        vpFrame.currentItem = 2
                    }
                }
            }
        }
//        chip1.setOnCheckedChangeListener(checkedListener)
//        chip2.setOnCheckedChangeListener(checkedListener)
//        chip3.setOnCheckedChangeListener(checkedListener)

        uiState.map { it.currentScreenIndex }.distinctUntilChanged().onEach {
            tvIndicate.text = when(it){
                0->"Next"
                1->"Next"
                else->"Start"
            }
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        tvIndicate.setOnClickListener {
            when(uiState.value.currentScreenIndex) {
                0-> vpFrame.currentItem = 1
                1-> vpFrame.currentItem = 2
                else-> {
                    viewModel.updateSessionUserId()
                }
            }
        }
    }
}


class SplashViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: androidx.lifecycle.Lifecycle,
    val list:List<Fragment>
):FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list.get(position)
    }
}