package com.ghostbug.memorylane.core.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.ghostbug.memorylane.core.permissions.PermissionsViewModel
import com.ghostbug.memorylane.features.capture.domain.model.Memories
import com.ghostbug.memorylane.features.capture.presentation.CameraScreen
import com.ghostbug.memorylane.features.capture.presentation.CameraViewModel
import com.ghostbug.memorylane.features.capture.presentation.DisplayImageAfterClickScreen
import com.ghostbug.memorylane.features.gallery.presentat.GalleryScreen
import com.ghostbug.memorylane.features.gallery.presentation.GalleryImageViewerScreen
import com.ghostbug.memorylane.features.gallery.presentation.GalleryViewModel
import com.ghostbug.memorylane.features.location.presentation.LocationViewModel
import com.ghostbug.memorylane.features.location.presentation.MapScreen
import com.ghostbug.memorylane.features.onboarding.OnboardingScreen1
import com.ghostbug.memorylane.features.profile.domain.repository.ProfileRepository
import com.ghostbug.memorylane.features.profile.presentation.ProfileScreen
import com.ghostbug.memorylane.features.profile.presentation.ProfileViewModel
import com.ghostbug.memorylane.features.signUp.auth.domain.repository.AuthRepository
import com.ghostbug.memorylane.features.signUp.presentation.SignUpRoute
import com.ghostbug.memorylane.features.signUp.presentation.SignUpViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.collections.plus

private const val DURATION = 320
private val easing = FastOutSlowInEasing
@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    authRepository: AuthRepository = koinInject(),
    profileRepository: ProfileRepository = koinInject()
) {
    var startRoute by remember { mutableStateOf<Route?>(null) }




    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        startRoute = if (authRepository.isEmailInPublicUsersTable().isSuccess) {
            Route.LocationScreen
        } else {
            Route.SignUpScreen
        }
    }

    val signUpViewModel: SignUpViewModel = koinViewModel()
    val galleryViewModel: GalleryViewModel = koinViewModel()

    val resolvedStartRoute = startRoute ?: return
    val navigationState = rememberNavigationState(
        startRoute = resolvedStartRoute,
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys
    )

    val navigator = remember(navigationState) {
        Navigator(navigationState)
    }
    val shouldShowBottomBar = when (navigationState.currentRoute) {
        is Route.LocationScreen,
        is Route.GalleryScreen,
        is Route.ProfileScreen -> true // SHOW navbar on these screens

        else -> false // HIDE navbar on SignUp, Onboarding, Camera, DisplayImageAfterClick, etc.
    }
    val crossfadeSpec = fadeIn(tween(200)) togetherWith fadeOut(tween(200))

    Scaffold(
        modifier = modifier,
        containerColor= MaterialTheme.colorScheme.background,
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomNavigationBar(
                    selectedKey = navigationState.topLevelRoute,
                    onSelectKey = {
                        navigator.navigate(it)
                    }
                )
            }
        }
    ) {  paddingValues ->
        SharedTransitionLayout{
        NavDisplay(
            onBack = navigator::goBack,
            transitionSpec = { crossfadeSpec },
            popTransitionSpec = { crossfadeSpec },
            predictivePopTransitionSpec = { crossfadeSpec },
        entries = navigationState.toEntries(
                entryProvider {
                    val locationViewModel: LocationViewModel = koinViewModel()
                    entry<Route.SignUpScreen> {
                        SignUpRoute(
                            signUpViewModel = signUpViewModel,
                            uiEvent = signUpViewModel.signUpUiEvent,
                            onSignUp = {
                                coroutineScope.launch {
                                    val profileExists = profileRepository.getProfile().isSuccess
                                    if (profileExists) {
                                        navigator.clearAll()
                                        navigator.navigate(Route.LocationScreen)
                                    } else {
                                        navigator.clearAll()
                                        navigator.navigate(Route.onBoardingScreen1)
                                    }
                                }
                            },
                            modifier = Modifier
                        )
                    }
                    entry<Route.LocationScreen> {
                        MapScreen(
                            locationViewModel = locationViewModel,
                            galleryViewModel = galleryViewModel,
                            onBack = { navigator.goBack() },
                        )
                    }
                    entry<Route.CameraScreen> {
                        val cameraViewModel: CameraViewModel = koinViewModel()
                        CameraScreen(
                            locationViewModel = locationViewModel,
                            cameraViewModel = cameraViewModel,
                            galleryViewModel = galleryViewModel,
                            onCaptured = {},
                            modifier = modifier,
                            onBack = { navigator.navigate(Route.LocationScreen) }
                        )
                    }
                    entry<Route.GalleryScreen> {
                        GalleryScreen(
                            galleryViewModel = galleryViewModel,
                            onBack = { navigator.goBack() },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                            onImageClick = { memoryId ->
                                navigator.navigate(Route.GalleryImageViewerScreen(memoryId))
                            },
                        )
                    }
                    entry<Route.onBoardingScreen1> {
                        val profileViewModel: ProfileViewModel = koinViewModel ()
                        OnboardingScreen1(
                            onNext = {
                                navigator.clearAll()
                                navigator.navigate(Route.LocationScreen)
                            },
                            profileViewModel = profileViewModel
                        )
                    }
                    entry<Route.ProfileScreen> {
                        ProfileScreen(
                            signUpViewModel = signUpViewModel,
                            onSignOut = {
                                navigator.clearAll()
                                navigator.navigate(Route.SignUpScreen)
                            }
                        )
                    }
                    entry<Route.GalleryImageViewerScreen> { route ->
                        val galleryState by galleryViewModel.state.collectAsStateWithLifecycle()
                        val memory = galleryState.memories.find { it.id == route.memoryId }
                        if (memory != null) {
                            GalleryImageViewerScreen(
                                id = route.memoryId,
                                memory = memory,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                onBackPressed = { navigator.goBack() }
                            )
                        }
                    }

                }
            )  )
    }
}}
