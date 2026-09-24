package com.ghostbug.memorylane.core.navigation

import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.ghostbug.memorylane.MyApplication
import com.ghostbug.memorylane.features.location.presentation.LocationViewModel
import com.ghostbug.memorylane.features.location.presentation.MapScreen
import com.ghostbug.memorylane.features.signUp.presentation.SignUpRoute
import com.ghostbug.memorylane.features.signUp.presentation.SignUpViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as MyApplication

    val locationViewModel : LocationViewModel = koinViewModel()
    val signUpViewModel : SignUpViewModel = koinViewModel ()

    val navigationState = rememberNavigationState(
        startRoute = Route.LocationScreen,
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys
    )

    val navigator = remember(navigationState) {
        Navigator(navigationState)
    }



    Scaffold(
        modifier = modifier,
        bottomBar = {
                BottomNavigationBar(
                    selectedKey = navigationState.topLevelRoute,
                    onSelectKey = {
                        navigator.navigate(it)
                    }
                )
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),

            onBack = navigator::goBack,

            transitionSpec = {
                slideInHorizontally(initialOffsetX = { it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { -it / 3 }) + fadeOut()
            },
            popTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it / 3 }) + fadeOut()
            },
            predictivePopTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it / 3 }) + fadeOut()
            },

            entries = navigationState.toEntries(
                entryProvider {
                    entry<Route.LocationScreen> {
                        MapScreen(
                            locationViewModel = locationViewModel,
                        )
                    }
                    entry <Route.SignUpScreen>{
                        SignUpRoute (
                           signUpViewModel = signUpViewModel,
                            onSignUp = {
                                navigator.navigate(Route.LocationScreen)
                            },
                            modifier = Modifier
                        )
                    }
//                    entry<Route.ProfileScreen> {
//
//                    }
                }
            )
        )
    }
}
