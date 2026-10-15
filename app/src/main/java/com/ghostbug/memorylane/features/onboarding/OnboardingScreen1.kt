package com.ghostbug.memorylane.features.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ghostbug.memorylane.features.profile.presentation.ProfileEvent
import com.ghostbug.memorylane.features.profile.presentation.ProfileState
import com.ghostbug.memorylane.features.profile.presentation.ProfileUiEvent
import com.ghostbug.memorylane.features.profile.presentation.ProfileViewModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun OnboardingScreen1(
    onNext: () -> Unit,
    profileViewModel: ProfileViewModel = koinViewModel(),
) {
    val profileState by profileViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        profileViewModel.uiEvent.collect { uiEvent ->
            when (uiEvent) {
                ProfileUiEvent.onNavHomeSceen,
                ProfileUiEvent.onNext -> onNext()
                else -> Unit
            }
        }
    }

    OnBoardingScreenContent(
        state = profileState,
        event = profileViewModel::onEvent,
    )
}

@Composable
fun OnBoardingScreenContent(
    state: ProfileState,
    event: (ProfileEvent) -> Unit,
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TextField(
            value = state.name,
            onValueChange = { event(ProfileEvent.onNameChanged(it)) },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))

        TextField(
            value = state.username,
            onValueChange = { event(ProfileEvent.onUsernameChanged(it)) },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = { showPicker = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(state.dob?.let { formatDob(it) } ?: "Date of birth")
        }

        if (showPicker) {
            BirthDatePickerDialog(
                onDateSelected = { millis ->
                    val localDate = Instant.fromEpochMilliseconds(millis)
                        .toLocalDateTime(TimeZone.UTC).date
                    event(ProfileEvent.onDobChanged(localDate))
                },
                onDismiss = { showPicker = false },
            )
        }

        if (state.error.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(state.error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                event(ProfileEvent.onSave)
            },
            enabled = !state.loading &&
                    state.name.isNotBlank() &&
                    state.username.isNotBlank() &&
                    state.dob != null,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (state.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Next")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthDatePickerDialog(
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val today = remember { System.currentTimeMillis() }
    val currentYear = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
    }

    val pickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Input,
        yearRange = 1920..currentYear,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= today
            override fun isSelectableYear(year: Int) = year <= currentYear
        },
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = pickerState.selectedDateMillis != null,
                onClick = {
                    pickerState.selectedDateMillis?.let(onDateSelected)
                    onDismiss()
                },
            ) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    ) {
        DatePicker(state = pickerState)
    }
}

private fun formatDob(date: LocalDate): String {
    val yy = (date.year % 100).toString().padStart(2, '0')
    val mm = date.monthNumber.toString().padStart(2, '0')
    val dd = date.dayOfMonth.toString().padStart(2, '0')
    return "$yy/$mm/$dd"
}