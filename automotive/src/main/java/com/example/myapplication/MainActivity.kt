package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.shared.model.ResponseMode
import com.example.myapplication.ui.HomeScreen
import com.example.myapplication.ui.OnboardingScreen
import com.example.myapplication.ui.ResponseFlowScreen
import com.example.myapplication.ui.SettingsScreen
import com.example.myapplication.ui.theme.SagohanjangTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SagohanjangTheme {
                Surface(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
                    HSafeApp()
                }
            }
        }
    }
}

private object Routes {
    const val Onboarding = "onboarding"
    const val Home = "home"
    const val Flow = "flow"
    const val Settings = "settings"
}

@Composable
private fun HSafeApp(viewModel: SagohanjangViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val start = if (state.profile.onboardingCompleted) Routes.Home else Routes.Onboarding

    fun openMode(mode: ResponseMode) {
        viewModel.startResponse(mode)
        navController.navigate(Routes.Flow)
    }

    NavHost(navController = navController, startDestination = start) {
        composable(Routes.Onboarding) {
            OnboardingScreen(
                initial = state.profile,
                restricted = state.drivingRestricted,
                vehicleApiHint = state.vehicleApiHint,
                onSave = {
                    viewModel.saveProfile(it)
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Onboarding) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Home) {
            HomeScreen(
                state = state,
                onEmergency119 = { openMode(ResponseMode.EMERGENCY_119) },
                onFire119 = { openMode(ResponseMode.FIRE_119) },
                onTraffic = { openMode(ResponseMode.TRAFFIC) },
                onSettings = { navController.navigate(Routes.Settings) },
                onResume = { navController.navigate(Routes.Flow) }
            )
        }
        composable(Routes.Flow) {
            ResponseFlowScreen(
                state = state,
                messagePreview = viewModel.messagePreview(),
                insurerPhone = viewModel.insurerPhone(),
                onBack = { navController.popBackStack() },
                onSelectKind = viewModel::selectAccidentKind,
                onUpdateMovableTow = { movable, tow -> viewModel.updateMovableTow(movable, tow) },
                onDial = viewModel::dialInsurerRepresentative,
                onDial119 = viewModel::dial119,
                onOnline = viewModel::openOnlineIntakePlaceholder,
                onMessageIntake = viewModel::openMessageIntakePlaceholder,
                onSend119Emergency = viewModel::send119EmergencyMessage,
                onSend119Fire = viewModel::send119FireMessage,
                onDemo = viewModel::applyDemoHighway
            )
        }
        composable(Routes.Settings) {
            SettingsScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Routes.Onboarding) },
                onClear = {
                    viewModel.clearProfile()
                    navController.navigate(Routes.Onboarding) {
                        popUpTo(Routes.Home) { inclusive = true }
                    }
                }
            )
        }
    }
}
