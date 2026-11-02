package com.guiachevrolet.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.guiachevrolet.data.repository.ChevroletRepository
import com.guiachevrolet.ui.screens.ErrorLookupScreen
import com.guiachevrolet.ui.screens.HomeScreen
import com.guiachevrolet.ui.screens.OnboardingScreen
import com.guiachevrolet.ui.screens.VehicleManagementScreen
import com.guiachevrolet.ui.viewmodel.ErrorLookupViewModel
import com.guiachevrolet.ui.viewmodel.StartupDestination
import com.guiachevrolet.ui.viewmodel.VehicleViewModel

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val MANAGE_VEHICLES = "manage_vehicles"
    const val LOOKUP = "lookup/{vehicleId}"

    fun lookupRoute(vehicleId: Long) = "lookup/$vehicleId"
}

@Composable
fun AppNavGraph(
    repository: ChevroletRepository
) {
    val vehicleViewModel: VehicleViewModel = viewModel(
        factory = VehicleViewModel.provideFactory(repository)
    )
    val startupDestination by vehicleViewModel.startupDestination.collectAsStateWithLifecycle()
    val vehicles by vehicleViewModel.vehicles.collectAsStateWithLifecycle()

    if (startupDestination == StartupDestination.Loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val startRoute = when (startupDestination) {
        StartupDestination.Onboarding -> Routes.ONBOARDING
        else -> Routes.HOME
    }

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startRoute
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onSaveVehicle = { model, year, nickname ->
                    vehicleViewModel.addVehicle(
                        model = model,
                        year = year,
                        nickname = nickname,
                        onSuccess = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.ONBOARDING) { inclusive = true }
                            }
                        }
                    )
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                vehicles = vehicles,
                onNavigateToManageVehicles = {
                    navController.navigate(Routes.MANAGE_VEHICLES)
                },
                onNavigateToLookup = { vehicleId ->
                    navController.navigate(Routes.lookupRoute(vehicleId))
                }
            )
        }

        composable(Routes.MANAGE_VEHICLES) {
            VehicleManagementScreen(
                vehicles = vehicles,
                onBack = { navController.popBackStack() },
                onAddVehicle = { model, year, nickname ->
                    vehicleViewModel.addVehicle(model, year, nickname)
                },
                onUpdateVehicle = { vehicle, model, year, nickname ->
                    vehicleViewModel.updateVehicle(vehicle, model, year, nickname)
                },
                onDeleteVehicle = { vehicle ->
                    vehicleViewModel.deleteVehicle(vehicle)
                }
            )
        }

        composable(
            route = Routes.LOOKUP,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            val lookupViewModel: ErrorLookupViewModel = viewModel(
                factory = ErrorLookupViewModel.provideFactory(repository)
            )
            val uiState by lookupViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(vehicleId) {
                lookupViewModel.loadVehicle(vehicleId)
            }

            ErrorLookupScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onCodeInputChange = lookupViewModel::onCodeInputChange,
                onClearClick = lookupViewModel::clearCodeInput,
                onSearchClick = { lookupViewModel.searchCode() },
                onQuickCodeClick = { quickCode -> lookupViewModel.searchCode(overrideCode = quickCode) }
            )
        }
    }
}
