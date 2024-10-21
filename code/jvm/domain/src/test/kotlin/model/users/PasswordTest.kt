package model.users

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PasswordTest {
    @Test
    fun `valid password is accepted`() {
        val password = Password("Valid123")
        assertTrue(password.matches("Valid123"))
    }

    @Test
    fun `password with less than eight characters is rejected`() {
        assertFailsWith<IllegalArgumentException> {
            Password("Val1d")
        }
    }

    @Test
    fun `password without digits is rejected`() {
        assertFailsWith<IllegalArgumentException> {
            Password("Invalid")
        }
    }

    @Test
    fun `password without uppercase letters is rejected`() {
        assertFailsWith<IllegalArgumentException> {
            Password("invalid123")
        }
    }

    @Test
    fun `password without lowercase letters is rejected`() {
        assertFailsWith<IllegalArgumentException> {
            Password("INVALID123")
        }
    }

    @Test
    fun `password does not match different password`() {
        val password = Password("Valid123")
        assertFalse(password.matches("Invalid123"))
    }

    @Test
    fun `is valid password returns true for valid password`() {
        assertTrue(Password.isValidPassword("Valid123"))
    }

    @Test
    fun `is valid password returns false for invalid password`() {
        assertFalse(Password.isValidPassword("invalid"))
    }

    @Test
    fun `toString returns password value`() {
        val password = Password("Valid123")
        assertTrue(password.toString() == "Valid123")
    }
}