package model

import kotlin.test.Test
import kotlin.test.assertFailsWith

class UserTest {
    private val uId: UInt = 1u
	private val validPassword = "Password123"
    private val passwordTest = Password(validPassword)
    private val usernameTest = "username"
    private val blankUsername = ""
    @Test
    fun `successful user instantiation test`() {
        User(uId, usernameTest, passwordTest)
    }

    @Test
    fun `exception generated by an invalid username test`() {
        assertFailsWith<IllegalArgumentException> { User(uId, blankUsername, passwordTest) }
    }
}