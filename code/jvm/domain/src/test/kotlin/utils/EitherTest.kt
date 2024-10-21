package utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EitherTest {
    @Test
    fun `success returns Right with correct value`() {
        val result = success("Success")
        assertTrue(result is Either.Right)
        assertEquals("Success", (result).value)
    }

    @Test
    fun `failure returns Left with correct error`() {
        val result = failure("Error")
        assertTrue(result is Either.Left)
        assertEquals("Error", (result).value)
    }
}