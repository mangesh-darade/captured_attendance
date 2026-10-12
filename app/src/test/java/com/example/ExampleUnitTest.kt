package com.example

import com.example.data.model.DeriveLocationPreviewResponse
import com.example.data.model.LocationOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testLocationOptionDisplayLabel() {
        val opt1 = LocationOption(name = "Headquarters", distance = "12.3m")
        assertEquals("Headquarters (12.3m)", opt1.displayLabel())
        assertEquals("Headquarters", opt1.pureName())

        val opt2 = LocationOption(name = "Branch Office (50m)", distance = null)
        assertEquals("Branch Office", opt2.pureName())
    }

    @Test
    fun testDeriveLocationPreviewResponse() {
        val res1 = DeriveLocationPreviewResponse(
            requiresLocationSelection = true,
            suggestedAttendanceType = "check_in"
        )
        assertTrue(res1.isLocationSelectionRequired())

        val res2 = DeriveLocationPreviewResponse(
            requiresLocationSelection = "1"
        )
        assertTrue(res2.isLocationSelectionRequired())

        val res3 = DeriveLocationPreviewResponse(
            requiresLocationSelection = false
        )
        assertFalse(res3.isLocationSelectionRequired())
    }

    @Test
    fun testDescriptorAverageMath() {
        val d1 = listOf(0.1f, 0.2f, 0.3f)
        val d2 = listOf(0.3f, 0.4f, 0.5f)
        val buffer = listOf(d1, d2)

        val size = buffer[0].size
        val count = buffer.size.toFloat()
        val result = FloatArray(size)
        for (desc in buffer) {
            for (i in 0 until minOf(size, desc.size)) {
                result[i] += desc[i]
            }
        }
        val averaged = result.map { it / count }
        assertEquals(0.2f, averaged[0], 0.001f)
        assertEquals(0.3f, averaged[1], 0.001f)
        assertEquals(0.4f, averaged[2], 0.001f)
    }
}
