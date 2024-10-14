package model

import model.messages.MessageInfo
import java.sql.Timestamp
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertFailsWith

class MessageInfoTest {
    private val msgId: UInt = 1u
    private val msg: String = "Hello World"
    private val creationTime: Timestamp = Timestamp.valueOf(LocalDateTime.now())

    @Test
    fun `successful message instantiation test`() {
        MessageInfo(msgId, msg, creationTime)
    }

    @Test
    fun `exception generated by an empty message test`() {
        assertFailsWith<IllegalArgumentException> {
            MessageInfo(msgId, "", creationTime)
        }
    }
}