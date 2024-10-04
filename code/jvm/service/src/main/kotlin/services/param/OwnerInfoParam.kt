package services.param

/**
 * Represents the parameters required to get the owner information.
 * @param username The username of the owner.
 * @param id The id of the owner.
 */
data class OwnerInfoParam(
	val username: String,
	val id: UInt,
)