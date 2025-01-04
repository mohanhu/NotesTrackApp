package com.example.notestrack.core.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.notestrack.R
import com.example.notestrack.databinding.ActivityMainBinding
import com.example.notestrack.profile.presentation.viewmodel.PhoneDarkState
import com.example.notestrack.richlib.LoadBottomGlide
import com.example.notestrack.richlib.LoadBottomGlide.loadGlideMenu
import com.example.notestrack.richlib.LoadBottomGlide.resizeMenuIcon
import com.example.notestrack.utils.ViewExtentions.makeGone
import com.example.notestrack.utils.ViewExtentions.makeVisible
import com.example.notestrack.utils.theme.ThemeSwitch
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
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

    private lateinit var navGraph: NavGraph

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

        binding.bindState(
            viewModel.uiState
        )

        val sum = { a:Int, b :Int-> a+b}

        val ans = addNew(3,4, sum = {
            a,b->
            a+b
        })
        println("Main sum launch > $ans")
    }

    private fun addNew(a:Int,b:Int,sum:(Int,Int)->Int):Int{
        return sum.invoke(a,b)
    }

    private fun ActivityMainBinding.bindState(uiState: StateFlow<MainUiState>) {

        bindUiDetails(uiState)

        navControllerState()
    }

    private fun ActivityMainBinding.bindUiDetails(uiState: StateFlow<MainUiState>) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                uiState.map { it.userId }.collectLatest {
                    delay(200)
                    if (it==0L){
                        navGraph = navController.navInflater.inflate(R.navigation.main_nav_graph)
                        navGraph.setStartDestination(R.id.taskSampleFragment)
                        navController.graph = navGraph
                        mainNavGraph.setupWithNavController(navController)
                    }else{
                        navGraph = navController.navInflater.inflate(R.navigation.main_nav_graph)
                        navGraph.setStartDestination(R.id.homeFragment)
                        navController.graph = navGraph
                        mainNavGraph.setupWithNavController(navController)
                    }
                }
            }
        }

        uiState.map { it.isLightTheme }.distinctUntilChanged().onEach {
            when(it){
                PhoneDarkState.Default.name->{
                    ThemeSwitch.switchToSystemDefaultTheme()
                }
                PhoneDarkState.Light.name->{
                    ThemeSwitch.switchToLightTheme()
                }
                PhoneDarkState.Dark.name->{
                    ThemeSwitch.switchToDarkTheme()
                }
            }
        }.flowWithLifecycle(lifecycle,Lifecycle.State.STARTED)
            .launchIn(lifecycleScope)
    }

    private fun ActivityMainBinding.navControllerState() {

        LoadBottomGlide.also {
            mainNavGraph.loadGlideMenu(this@MainActivity,R.id.profileFragment)
            mainNavGraph.resizeMenuIcon()
        }
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