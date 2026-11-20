package com.apptrackstudy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptrackstudy.data.preferences.UserPreferencesRepository
import com.apptrackstudy.domain.model.Assignment
import com.apptrackstudy.domain.model.StudyStats
import com.apptrackstudy.domain.model.SubjectProgress
import com.apptrackstudy.domain.repository.AssignmentRepository
import com.apptrackstudy.domain.repository.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

enum class TimePeriod(val title: String, val iconEmoji: String) {
    PAGI("Selamat Pagi", "🌅"),
    SIANG("Selamat Siang", "☀️"),
    SORE("Selamat Sore", "🌇"),
    MALAM("Selamat Malam", "🌙"),
    LARUT_MALAM("Larut Malam", "🌌")
}

data class HomeUiState(
    val userName: String = UserPreferencesRepository.DEFAULT_USER_NAME,
    val greetingTitle: String = "Selamat Datang",
    val greetingEmoji: String = "👋",
    val greetingMessage: String = "",
    val subjects: List<SubjectProgress> = emptyList(),
    val stats: StudyStats = StudyStats(),
    val upcomingAssignments: List<Assignment> = emptyList(),
    val isOnboardingCompleted: Boolean = true,
    val showOnboardingDialog: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val studyRepository: StudyRepository,
    private val assignmentRepository: AssignmentRepository
) : ViewModel() {

    // Simpan pesan acak di ViewModel agar TIDAK berubah saat recomposition atau rotasi layar
    private val selectedGreetingMessage: String
    private val currentPeriod: TimePeriod

    private val _dismissedOnboarding = MutableStateFlow(false)

    init {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        currentPeriod = when (hour) {
            in 4..10 -> TimePeriod.PAGI
            in 11..14 -> TimePeriod.SIANG
            in 15..17 -> TimePeriod.SORE
            in 18..21 -> TimePeriod.MALAM
            else -> TimePeriod.LARUT_MALAM
        }
        selectedGreetingMessage = getRandomGreetingTemplate(currentPeriod)
    }

    private val preferencesFlow = combine(
        preferencesRepository.userName,
        preferencesRepository.isOnboardingCompleted,
        _dismissedOnboarding
    ) { name, isOnboarded, dismissed ->
        Triple(name, isOnboarded, dismissed)
    }

    private val dataFlow = combine(
        studyRepository.getSubjectsWithProgress(),
        studyRepository.getStudyStats(),
        assignmentRepository.getUpcomingAssignments(3)
    ) { subjects, stats, upcoming ->
        Triple(subjects, stats, upcoming)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        preferencesFlow,
        dataFlow
    ) { (userName, isOnboarded, dismissedOnboarding), (subjects, stats, upcoming) ->
        val formattedMessage = selectedGreetingMessage.replace("{name}", userName)

        HomeUiState(
            userName = userName,
            greetingTitle = "${currentPeriod.title}, $userName!",
            greetingEmoji = currentPeriod.iconEmoji,
            greetingMessage = formattedMessage,
            subjects = subjects,
            stats = stats,
            upcomingAssignments = upcoming,
            isOnboardingCompleted = isOnboarded,
            showOnboardingDialog = !isOnboarded && !dismissedOnboarding,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun completeOnboarding(name: String) {
        viewModelScope.launch {
            preferencesRepository.setUserName(name)
            preferencesRepository.setOnboardingCompleted(true)
            _dismissedOnboarding.value = true
        }
    }

    fun dismissOnboardingDialog() {
        _dismissedOnboarding.value = true
    }

    companion object {
        // Minimal 5-6 variasi pesan hangat & memotivasi per waktu
        private val PagiGreetings = listOf(
            "Awali hari dengan secangkir semangat untuk menggapai impianmu, {name}.",
            "Pagi yang cerah untuk belajar hal baru, {name}! Satu langkah kecil hari ini berarti besar esok hari.",
            "Otak masih segar, yuk selesaikan beberapa target materi belajarmu hari ini, {name}!",
            "Jadikan hari ini lebih produktif dan membanggakan dari kemarin ya, {name}!",
            "Jangan lupa sarapan dan siapkan fokus terbaikmu untuk sesi belajar pagi ini, {name}.",
            "Konsistensi kecil setiap pagi adalah kunci sukses mahasiswa hebat seperti dirimu, {name}."
        )

        private val SiangGreetings = listOf(
            "Tetap semangat ya, {name}! Istirahat sejenak lalu gas belajar lagi.",
            "Jangan lupa makan siang dan minum air putih agar konsentrasi belajarmu tetap prima, {name}!",
            "Masih ada banyak waktu di paruh hari ini untuk menuntaskan materi kuliahmu, {name}.",
            "Energi siang mungkin sedikit turun, tapi semangat belajarmu jangan sampai pudar ya, {name}!",
            "Yuk sempatkan sedikit waktu untuk mereview ringkasan materi sebelum lanjut aktivitas, {name}.",
            "Sedikit demi sedikit, semua tugas dan materi pasti bisa kamu tuntasin dengan baik, {name}!"
        )

        private val SoreGreetings = listOf(
            "Waktu yang pas untuk mereview kembali materi kuliah yang kamu dapat hari ini, {name}.",
            "Sore santai! Mari luangkan waktu sejenak untuk checklist target belajarmu, {name}.",
            "Menjelang senja, yuk cicil materi sedikit biar nanti malam kamu bisa istirahat lebih tenang, {name}.",
            "Kamu sudah berjuang hebat hari ini, {name}. Tuntaskan sedikit lagi ya!",
            "Fokus 25 menit di sore hari ini akan membuat progres belajarmu tetap terjaga, {name}.",
            "Jadikan sore ini momen produktif untuk mempersiapkan pemahaman kuliah esok hari, {name}."
        )

        private val MalamGreetings = listOf(
            "Selamat malam, {name}. Review materi hari ini sebentar sebelum tidur ya.",
            "Malam yang tenang adalah sahabat terbaik untuk mendalami konsep yang rumit, {name}.",
            "Tutup hari dengan pencapaian yang memuaskan. Hebat kamu sudah bertahan hari ini, {name}!",
            "Yuk tuntaskan checklist terakhir hari ini agar besok bisa melangkah lebih ringan, {name}.",
            "Belajar malam ini adalah investasi berharga untuk masa depan gemilangmu, {name}.",
            "Kerja kerasmu hari ini sangat berharga, {name}. Semangat menuntaskan sisa materi!"
        )

        private val LarutMalamGreetings = listOf(
            "Masih terjaga, {name}? Luar biasa dedikasimu, tapi jangan lupa istirahat yang cukup ya.",
            "Kesehatanmu tetap nomor satu, {name}. Cicil sedikit saja lalu tidur ya.",
            "Belajar larut malam jangan sampai mengorbankan tidurmu ya, {name}. Besok kita lanjut lagi!",
            "Pejuang skripsi dan tugas malam hari! Semangat terus, tapi jaga tubuhmu tetap bugar, {name}.",
            "Malam semakin larut, {name}. Selesaikan materi ini dan berikan istirahat terbaik untuk otakmu.",
            "Kerja keras di keheningan malam akan membuahkan hasil manis di masa depan, {name}."
        )

        fun getRandomGreetingTemplate(period: TimePeriod): String {
            val list = when (period) {
                TimePeriod.PAGI -> PagiGreetings
                TimePeriod.SIANG -> SiangGreetings
                TimePeriod.SORE -> SoreGreetings
                TimePeriod.MALAM -> MalamGreetings
                TimePeriod.LARUT_MALAM -> LarutMalamGreetings
            }
            return list.random()
        }
    }
}
