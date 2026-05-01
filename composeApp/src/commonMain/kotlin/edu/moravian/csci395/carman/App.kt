package edu.moravian.csci395.carman

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import edu.moravian.csci395.carman.data.CarManDatabase
import edu.moravian.csci395.carman.theme.AppTheme
import edu.moravian.csci395.carman.screens.AddCar
import edu.moravian.csci395.carman.screens.AddCarScreen
import edu.moravian.csci395.carman.screens.AddEvent
import edu.moravian.csci395.carman.screens.EditEvent
import edu.moravian.csci395.carman.screens.AddEventScreen
import edu.moravian.csci395.carman.screens.CarDetail
import edu.moravian.csci395.carman.screens.CarDetailScreen
import edu.moravian.csci395.carman.screens.Cars
import edu.moravian.csci395.carman.screens.CarsScreen
import edu.moravian.csci395.carman.screens.Home
import edu.moravian.csci395.carman.screens.HomeScreen
import edu.moravian.csci395.carman.screens.LogMileage
import edu.moravian.csci395.carman.screens.LogMileageScreen
import edu.moravian.csci395.carman.screens.MapScreen
import edu.moravian.csci395.carman.screens.MechanicsMap
import edu.moravian.csci395.carman.screens.Settings
import edu.moravian.csci395.carman.screens.SettingsScreen

/** Entry point composable. Sets up theme, navigation, and the bottom tab bar. */
@Composable
fun App(database: CarManDatabase) {
    AppTheme {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination: NavDestination? = backStackEntry?.destination

        // Bottom bar visible only on the 4 top-level tabs.
        val isOnTab = TopLevelTabs.any { tab ->
            currentDestination?.hasRoute(tab.route::class) == true
        }

        Scaffold(
            bottomBar = {
                if (isOnTab) {
                    NavigationBar {
                        TopLevelTabs.forEach { tab ->
                            val selected = currentDestination?.hasRoute(tab.route::class) == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(tab.route) {
                                        popUpTo(Home) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(tab.icon, contentDescription = tab.label) },
                                label = { Text(tab.label) },
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Home,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable<Home> {
                    HomeScreen(
                        carDao = database.carDao(),
                        eventDao = database.maintenanceEventDao(),
                        onEventClick = { id -> navController.navigate(EditEvent(id)) }
                    )
                }
                composable<Cars> {
                    CarsScreen(
                        carDao = database.carDao(),
                        onCarClick = { carId -> navController.navigate(CarDetail(carId)) },
                        onAddCarClick = { navController.navigate(AddCar) },
                    )
                }
                composable<MechanicsMap> { MapScreen() }
                composable<Settings> { SettingsScreen() }

                composable<CarDetail> { entry ->
                    val route = entry.toRoute<CarDetail>()
                    CarDetailScreen(
                        carId = route.carId,
                        carDao = database.carDao(),
                        eventDao = database.maintenanceEventDao(),
                        onBack = { navController.popBackStack() },
                        onLogMileageClick = { id -> navController.navigate(LogMileage(id)) },
                        onAddEventClick = { id -> navController.navigate(AddEvent(id)) },
                        onEventClick = { id -> navController.navigate(EditEvent(id)) },
                    )
                }
                composable<AddCar> {
                    AddCarScreen(
                        carDao = database.carDao(),
                        onSaved = { navController.popBackStack() },
                        onCancel = { navController.popBackStack() },
                    )
                }
                composable<AddEvent> { entry ->
                    val route = entry.toRoute<AddEvent>()
                    AddEventScreen(
                        carId = route.carId,
                        carDao = database.carDao(),
                        eventDao = database.maintenanceEventDao(),
                        onSaved = { navController.popBackStack() },
                        onCancel = { navController.popBackStack() },
                    )
                }
                composable<EditEvent> { entry ->
                    val route = entry.toRoute<EditEvent>()
                    AddEventScreen(
                        carId = -1, // Not needed for edit
                        carDao = database.carDao(),
                        eventDao = database.maintenanceEventDao(),
                        eventId = route.eventId,
                        onSaved = { navController.popBackStack() },
                        onCancel = { navController.popBackStack() },
                    )
                }
                composable<LogMileage> { entry ->
                    val route = entry.toRoute<LogMileage>()
                    LogMileageScreen(
                        carId = route.carId,
                        carDao = database.carDao(),
                        onSaved = { navController.popBackStack() },
                        onCancel = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}

/** Definition of one bottom-nav tab. */
private data class TopLevelTab(
    val route: Any,
    val label: String,
    val icon: ImageVector,
)

/** The four top-level tabs displayed in the bottom navigation bar. */
private val TopLevelTabs = listOf(
    TopLevelTab(Home, "Home", Icons.Default.Home),
    TopLevelTab(Cars, "Cars", Icons.Default.List),
    TopLevelTab(MechanicsMap, "Map", Icons.Default.LocationOn),
    TopLevelTab(Settings, "Settings", Icons.Default.Settings),
)