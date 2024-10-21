package utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EitherTest {
    @Test
    fun `success returns Right with correct value`() {
        val result = success("Success")
        assertEquals("Success", (result).value)
    }

    @Test
    fun `failure returns Left with correct error`() {
        val result = failure("Error")
        assertEquals("Error", (result).value)
    }
}