package model

/**
 * The access control settings of a channel.
 *
 * @property READ_ONLY Users can only read messages in the channel.
 * The only user that can write messages is the owner.
 * @property READ_WRITE Users can read and write messages in the channel.
 */
enum class AccessControl {
    READ_ONLY { override fun toString() = "READ_ONLY" },
    READ_WRITE { override fun toString() = "READ_WRITE" },
}