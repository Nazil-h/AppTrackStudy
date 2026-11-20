package com.apptrackstudy.ui.home

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GreetingSelectionTest {

    @Test
    fun getRandomGreetingTemplate_forAllTimePeriods_returnsValidMessageWithPlaceholder() {
        val periods = TimePeriod.entries.toTypedArray()

        for (period in periods) {
            val template = HomeViewModel.getRandomGreetingTemplate(period)
            assertNotNull("Template untuk $period tidak boleh null", template)
            assertTrue("Template untuk $period tidak boleh kosong", template.isNotBlank())
            assertTrue(
                "Template untuk $period harus memuat placeholder '{name}'",
                template.contains("{name}")
            )

            // Pengujian formatting nama
            val formatted = template.replace("{name}", "Budi")
            assertTrue("Pesan harus menyebut nama pengguna", formatted.contains("Budi"))
            assertFalse("Placeholder {name} harus sudah tergantikan", formatted.contains("{name}"))
        }
    }

    @Test
    fun timePeriods_haveExpectedDisplayTitlesAndEmojis() {
        assertTrue(TimePeriod.PAGI.title.contains("Pagi"))
        assertTrue(TimePeriod.SIANG.title.contains("Siang"))
        assertTrue(TimePeriod.SORE.title.contains("Sore"))
        assertTrue(TimePeriod.MALAM.title.contains("Malam"))
        assertTrue(TimePeriod.LARUT_MALAM.title.contains("Larut"))

        assertTrue(TimePeriod.PAGI.iconEmoji.isNotBlank())
        assertTrue(TimePeriod.SIANG.iconEmoji.isNotBlank())
        assertTrue(TimePeriod.SORE.iconEmoji.isNotBlank())
        assertTrue(TimePeriod.MALAM.iconEmoji.isNotBlank())
        assertTrue(TimePeriod.LARUT_MALAM.iconEmoji.isNotBlank())
    }
}
