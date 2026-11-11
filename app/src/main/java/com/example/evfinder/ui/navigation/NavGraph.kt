package com.example.evfinder.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.evfinder.ui.components.AppHeader
import com.example.evfinder.ui.components.AuthDialog
import com.example.evfinder.ui.components.ChargingSimModal
import com.example.evfinder.ui.components.FilterBottomSheet
import com.example.evfinder.ui.components.VehicleSelectorDialog
import com.example.evfinder.ui.screens.FavoritesScreen
import com.example.evfinder.ui.screens.MapScreen
import com.example.evfinder.ui.screens.ProfileScreen
import com.example.evfinder.ui.screens.StationDetailScreen
import com.example.evfinder.ui.theme.EcoGreenPrimary
import com.example.evfinder.viewmodel.MainViewModel

sealed class NavRoute(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Map : NavRoute("map", "Mapa", Icons.Default.Map)
    object Favorites : NavRoute("favorites", "Favoritos", Icons.Default.Favorite)
    object Profile : NavRoute("profile", "Perfil", Icons.Default.Person)
    object StationDetail : NavRoute("station_detail/{stationId}", "Detalle", Icons.Default.Map) {
        fun createRoute(stationId: String) = "station_detail/$stationId"
    }
}

@Composable
fun EvFinderApp(
    viewModel: MainViewModel,
    navController: NavHostController = rememberNavController()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val filteredStations by viewModel.filteredStations.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val selectedStation by viewModel.selectedStation.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val isFilterSheetOpen by viewModel.isFilterSheetOpen.collectAsState()
    val isAuthDialogOpen by viewModel.isAuthDialogOpen.collectAsState()
    val isVehicleSelectorOpen by viewModel.isVehicleSelectorOpen.collectAsState()
    val isChargingModalOpen by viewModel.isChargingModalOpen.collectAsState()
    val chargingSession by viewModel.chargingSession.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // User Message Snackbar Handling
    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        NavRoute.Map,
        NavRoute.Favorites,
        NavRoute.Profile
    )

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        topBar = {
            if (showBottomBar) {
                AppHeader(
                    currentUser = currentUser,
                    onUserClick = {
                        if (currentUser.isGuest) {
                            viewModel.toggleAuthDialog(true)
                        } else {
                            navController.navigate(NavRoute.Profile.route)
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EcoGreenPrimary,
                                selectedTextColor = EcoGreenPrimary,
                                indicatorColor = EcoGreenPrimary.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoute.Map.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Map Screen
            composable(NavRoute.Map.route) {
                MapScreen(
                    stations = filteredStations,
                    filterState = filterState,
                    selectedStation = selectedStation,
                    favorites = favorites,
                    viewModel = viewModel,
                    onViewDetail = { station ->
                        navController.navigate(NavRoute.StationDetail.createRoute(station.id))
                    }
                )
            }

            // Favorites Screen
            composable(NavRoute.Favorites.route) {
                val favoriteList = filteredStations.filter { favorites.contains(it.id) }
                FavoritesScreen(
                    currentUser = currentUser,
                    favoriteStations = favoriteList,
                    viewModel = viewModel,
                    onNavigateToMap = {
                        navController.navigate(NavRoute.Map.route)
                    },
                    onViewDetail = { station ->
                        navController.navigate(NavRoute.StationDetail.createRoute(station.id))
                    }
                )
            }

            // Profile Screen
            composable(NavRoute.Profile.route) {
                ProfileScreen(
                    currentUser = currentUser,
                    viewModel = viewModel
                )
            }

            // Station Detail Screen
            composable(NavRoute.StationDetail.route) { backStackEntry ->
                val stationId = backStackEntry.arguments?.getString("stationId")
                val station = filteredStations.firstOrNull { it.id == stationId }
                if (station != null) {
                    StationDetailScreen(
                        station = station,
                        isFavorite = favorites.contains(station.id),
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }

        // Global Modals
        if (isFilterSheetOpen) {
            FilterBottomSheet(
                filter = filterState,
                onPowerSelected = { viewModel.onPowerCategorySelected(it) },
                onConnectorToggled = { viewModel.onConnectorTypeToggled(it) },
                onStatusSelected = { viewModel.onStatusFilterSelected(it) },
                onResetFilters = { viewModel.resetFilters() },
                onDismiss = { viewModel.toggleFilterSheet(false) }
            )
        }

        if (isAuthDialogOpen) {
            AuthDialog(
                isLoading = isLoading,
                onLogin = { email, pass -> viewModel.login(email, pass) {} },
                onRegister = { name, email, pass -> viewModel.register(name, email, pass) {} },
                onGuestMode = { viewModel.loginAsGuest() },
                onDismiss = { viewModel.toggleAuthDialog(false) }
            )
        }

        if (isVehicleSelectorOpen) {
            VehicleSelectorDialog(
                selectedVehicle = currentUser.selectedVehicle,
                onVehicleSelected = { viewModel.selectVehicle(it) },
                onDismiss = { viewModel.toggleVehiclePicker(false) }
            )
        }

        if (isChargingModalOpen) {
            ChargingSimModal(
                session = chargingSession,
                onStopSession = { viewModel.stopChargingSession() },
                onClose = { viewModel.closeChargingModal() }
            )
        }
    }
}
