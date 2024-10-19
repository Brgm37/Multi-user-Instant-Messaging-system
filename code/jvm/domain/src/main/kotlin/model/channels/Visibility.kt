package model.channels

/**
 * The visibility of a channel.
 *
 * @property PUBLIC The channel is visible to everyone.
 * @property PRIVATE The channel is only visible to the owner and the users in the channel.
 */
enum class Visibility {
    PUBLIC,
    PRIVATE,
    ;

    companion object {
        private val names = entries.map(Visibility::name)

        fun validate(visibility: String) = visibility.uppercase() in names
    }
}