package com.dentaltracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dentaltracker.data.DentalDatabase
import com.dentaltracker.repository.DentalPhotoRepository
import com.dentaltracker.ui.CameraScreen
import com.dentaltracker.ui.MainScreen
import com.dentaltracker.ui.ProgressScreen
import com.dentaltracker.ui.theme.DentalTrackerTheme
import com.dentaltracker.viewmodel.DentalTrackerViewModel
import com.dentaltracker.viewmodel.DentalTrackerViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize database and repository
        val database = DentalDatabase.getDatabase(this)
        val repository = DentalPhotoRepository(database.dentalPhotoDao())
        val viewModelFactory = DentalTrackerViewModelFactory(repository, this)
        
        setContent {
            DentalTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DentalTrackerApp(viewModelFactory)
                }
            }
        }
    }
}

@Composable
fun DentalTrackerApp(viewModelFactory: DentalTrackerViewModelFactory) {
    val navController = rememberNavController()
    val viewModel: DentalTrackerViewModel = viewModel(factory = viewModelFactory)
    
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                onNavigateToCamera = {
                    navController.navigate("camera")
                },
                onNavigateToProgress = {
                    navController.navigate("progress")
                }
            )
        }
        
        composable("camera") {
            CameraScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("progress") {
            ProgressScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}