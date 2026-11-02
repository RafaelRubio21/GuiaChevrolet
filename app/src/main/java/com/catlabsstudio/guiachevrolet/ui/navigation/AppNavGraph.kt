package com.catlabsstudio.guiachevrolet.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.catlabsstudio.guiachevrolet.data.local.entity.VehicleEntity
import com.catlabsstudio.guiachevrolet.data.repository.ChevroletRepository
import com.catlabsstudio.guiachevrolet.ui.ads.RewardedManualAdController
import com.catlabsstudio.guiachevrolet.ui.ads.UnlockManualRewardedDialog
import com.catlabsstudio.guiachevrolet.ui.ads.findActivity
import com.catlabsstudio.guiachevrolet.ui.screens.CreditsScreen
import com.catlabsstudio.guiachevrolet.ui.screens.ErrorLookupScreen
import com.catlabsstudio.guiachevrolet.ui.screens.HomeScreen
import com.catlabsstudio.guiachevrolet.ui.screens.ManualPdfScreen
import com.catlabsstudio.guiachevrolet.ui.screens.OnboardingScreen
import com.catlabsstudio.guiachevrolet.ui.screens.VehicleManagementScreen
import com.catlabsstudio.guiachevrolet.ui.viewmodel.ErrorLookupViewModel
import com.catlabsstudio.guiachevrolet.ui.viewmodel.ManualViewerViewModel
import com.catlabsstudio.guiachevrolet.ui.viewmodel.StartupDestination
import com.catlabsstudio.guiachevrolet.ui.viewmodel.VehicleViewModel

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val MANAGE_VEHICLES = "manage_vehicles"
    const val LOOKUP = "lookup/{vehicleId}"
    const val MANUAL = "manual/{vehicleId}"
    const val CREDITS = "credits"

    fun lookupRoute(vehicleId: Long) = "lookup/$vehicleId"
    fun manualRoute(vehicleId: Long) = "manual/$vehicleId"
}

@Composable
fun AppNavGraph(
    repository: ChevroletRepository
) {
    val context = LocalContext.current
    val rewardedAdController = remember { RewardedManualAdController(context) }
    var pendingManualVehicle by remember { mutableStateOf<VehicleEntity?>(null) }

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

    fun requestOpenManual(vehicle: VehicleEntity) {
        if (rewardedAdController.isManualUnlocked(vehicle.model)) {
            navController.navigate(Routes.manualRoute(vehicle.id))
        } else {
            rewardedAdController.preloadAd()
            pendingManualVehicle = vehicle
        }
    }

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
                },
                onNavigateToManual = { vehicleId ->
                    vehicles.firstOrNull { it.id == vehicleId }?.let { vehicle ->
                        requestOpenManual(vehicle)
                    }
                },
                onNavigateToCredits = {
                    navController.navigate(Routes.CREDITS)
                }
            )
        }

        composable(Routes.CREDITS) {
            CreditsScreen(
                onBack = { navController.popBackStack() }
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
                onQuickCodeClick = { quickCode -> lookupViewModel.searchCode(overrideCode = quickCode) },
                onOpenManualClick = {
                    uiState.activeVehicle?.let { vehicle ->
                        requestOpenManual(vehicle)
                    }
                }
            )
        }

        composable(
            route = Routes.MANUAL,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            val manualViewModel: ManualViewerViewModel = viewModel(
                factory = ManualViewerViewModel.provideFactory(repository)
            )
            val uiState by manualViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(vehicleId) {
                manualViewModel.loadManualForVehicle(context.applicationContext, vehicleId)
            }

            ManualPdfScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onRetry = {
                    manualViewModel.loadManualForVehicle(
                        context = context.applicationContext,
                        vehicleId = vehicleId,
                        forceRedownload = true
                    )
                },
                renderPage = manualViewModel::renderPage
            )
        }
    }

    // Modal de confirmação do Anúncio Recompensado (Rewarded Ad) para desbloquear o PDF do manual por 7 dias
    pendingManualVehicle?.let { vehicleToUnlock ->
        UnlockManualRewardedDialog(
            vehicleModel = vehicleToUnlock.model,
            isLoadingAd = rewardedAdController.isLoadingAd,
            onDismiss = { pendingManualVehicle = null },
            onWatchVideoClick = {
                val targetId = vehicleToUnlock.id
                val targetModel = vehicleToUnlock.model
                rewardedAdController.showRewardedAd(
                    activity = context.findActivity(),
                    vehicleModel = targetModel,
                    onUnlocked = {
                        pendingManualVehicle = null
                        navController.navigate(Routes.manualRoute(targetId))
                    }
                )
            }
        )
    }
}
