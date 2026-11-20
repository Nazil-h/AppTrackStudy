package com.apptrackstudy.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apptrackstudy.ui.components.EmptyStateView
import com.apptrackstudy.ui.components.JejakIlmuTopAppBar
import com.apptrackstudy.ui.components.OnboardingDialog
import com.apptrackstudy.ui.home.components.GreetingCard
import com.apptrackstudy.ui.home.components.SubjectSummaryCard
import com.apptrackstudy.ui.home.components.UpcomingAssignmentsCard

/**
 * Tab 1: Beranda
 * Menampilkan:
 * 1. GreetingCard dengan sapaan hangat acak sesuai waktu & nama pengguna
 * 2. Kartu Deadline Terdekat (maks 3 tugas)
 * 3. Ringkasan To-Do / Progress seluruh Mata Kuliah
 * 4. Empty State jika data kosong
 * 5. Dialog Onboarding nama panggilan pertama kali
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToTrack: () -> Unit = {},
    onNavigateToAssignments: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            JejakIlmuTopAppBar(title = "Beranda")
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 16.dp)
                ) {
                    // 1. Kartu Sapaan (GreetingCard)
                    item {
                        GreetingCard(
                            greetingTitle = uiState.greetingTitle,
                            greetingEmoji = uiState.greetingEmoji,
                            greetingMessage = uiState.greetingMessage,
                            stats = uiState.stats
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // 2. Kartu Deadline Terdekat (Fitur 7)
                    item {
                        UpcomingAssignmentsCard(
                            assignments = uiState.upcomingAssignments,
                            onViewAllClick = onNavigateToAssignments
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // 3. Header Bagian Ringkasan
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ringkasan Target Belajar",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (uiState.subjects.isNotEmpty()) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ) {
                                    Text(
                                        text = "${uiState.subjects.size} Mata Kuliah",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // 4. Daftar Subject atau Empty State
                    if (uiState.subjects.isEmpty()) {
                        item {
                            EmptyStateView(
                                title = "Belum Ada Target Belajar",
                                description = "Mata kuliah yang kamu tambahkan di Peta Belajar akan tampil di sini sebagai ringkasan progres harianmu.",
                                icon = Icons.AutoMirrored.Outlined.MenuBook,
                                actionButtonText = "Tambah materi pertama",
                                onActionClick = onNavigateToTrack,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    } else {
                        items(
                            items = uiState.subjects,
                            key = { it.subject.id }
                        ) { subjectProgress ->
                            SubjectSummaryCard(
                                subjectProgress = subjectProgress,
                                onClick = onNavigateToTrack,
                                modifier = Modifier.padding(vertical = 5.dp)
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            // Dialog Onboarding nama panggilan pertama kali
            if (uiState.showOnboardingDialog) {
                OnboardingDialog(
                    onConfirm = { name -> viewModel.completeOnboarding(name) },
                    onDismiss = { viewModel.dismissOnboardingDialog() }
                )
            }
        }
    }
}
