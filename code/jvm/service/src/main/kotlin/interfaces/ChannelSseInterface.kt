package interfaces

interface ChannelSseInterface {
    fun isUserInChannel(
        uId: UInt,
        cId: UInt,
    ): Boolean
}