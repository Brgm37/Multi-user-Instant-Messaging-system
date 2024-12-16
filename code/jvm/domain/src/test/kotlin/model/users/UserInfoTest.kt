package model.users

import kotlin.test.Test
import kotlin.test.assertFailsWith

class UserInfoTest {
    @Test
    fun `successful user info instantiation test`() {
        UserInfo(uId = 1u, username = "username")
    }

    @Test
    fun `exception generated by an invalid username test`() {
        assertFailsWith<IllegalArgumentException> { UserInfo(uId = 1u, username = "") }
    }
}