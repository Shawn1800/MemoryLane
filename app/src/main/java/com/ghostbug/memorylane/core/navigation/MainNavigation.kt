package com.ghostbug.memorylane.core.navigation

import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.ghostbug.memorylane.MyApplication
import com.ghostbug.memorylane.features.location.presentation.LocationViewModel
import com.ghostbug.memorylane.features.location.presentation.MapScreen
import com.ghostbug.memorylane.features.signUp.auth.domain.repository.AuthRepository
import com.ghostbug.memorylane.features.signUp.presentation.SignUpRoute
import com.ghostbug.memorylane.features.signUp.presentation.SignUpUiEvent
import com.ghostbug.memorylane.features.signUp.presentation.SignUpViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    authRepository: AuthRepository = koinInject()

) {
    var startRoute by remember { mutableStateOf<Route?>(null) }


    LaunchedEffect(Unit) {
        startRoute = if (authRepository.isEmailInPublicUsersTable().isSuccess) {
            Route.LocationScreen
        } else {
            Route.SignUpScreen
        }
    }
    val resolvedStartRoute = startRoute ?: return
    val navigationState = rememberNavigationState(
        startRoute = resolvedStartRoute,
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
                    entry <Route.SignUpScreen>{
                        val signUpViewModel : SignUpViewModel = koinViewModel ()
                        SignUpRoute (
                            signUpViewModel = signUpViewModel,
                            uiEvent = signUpViewModel.signUpUiEvent,
                            onSignUp = {
                                navigator.navigate(Route.LocationScreen)
                            },
                            modifier = Modifier
                        )
                    }
                    entry<Route.LocationScreen> {
                        val locationViewModel : LocationViewModel = koinViewModel()
                        MapScreen(
                            locationViewModel = locationViewModel,
                            onBack= navigator.clearCurrentStack()
                        )
                    }

                }
            )
        )
    }
}
