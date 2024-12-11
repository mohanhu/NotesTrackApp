package com.example.notestrack.core.presentation

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.notestrack.R
import com.example.notestrack.databinding.ActivityMainBinding
import com.example.notestrack.utils.ViewExtentions.makeGone
import com.example.notestrack.utils.ViewExtentions.makeInVisible
import com.example.notestrack.utils.ViewExtentions.makeVisible
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment }

    private val navController: NavController by lazy { navHostFragment.navController }

    private val navGraph: NavGraph by lazy { navController.navInflater.inflate(R.navigation.main_nav_graph) }

    private val viewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splash = installSplashScreen()
        viewModel.uiState.map { it.splashScreenState }
            .distinctUntilChanged().onEach {
            splash.setKeepOnScreenCondition{
               it
            }
        }

        setContentView(binding.root)

        binding.bindState()

    }

    private fun ActivityMainBinding.bindState() {
        navController.graph = navGraph
        mainNavGraph.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.homeFragment->{
                    mainNavGraph.makeVisible()
                }
                R.id.addMenuFragment->{
                    mainNavGraph.makeGone()
                }
                R.id.profileFragment->{
                    mainNavGraph.makeVisible()
                }
                else->{
                    mainNavGraph.makeGone()
                }
            }
        }
    }
}