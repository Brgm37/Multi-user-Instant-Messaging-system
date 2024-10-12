/**
 * Generic repository interface for basic CRUD operations
 */
interface Repository<T> {
	/**
	 * Retrieves an entity by its ID
	 * @param id The ID of the entity to retrieve
	 * @return The entity with the given ID or null if it does not exist
	 */
	fun findById(id: UInt): T?

	/**
	 * Retrieves all entities
	 * @param offset The offset to start retrieving entities
	 * @param limit The maximum number of entities to retrieve
	 * @return A list with all entities
	 */
	fun findAll(
		offset: Int,
		limit: Int,
	): List<T>

	/**
	 * Saves a new or existing entity
	 * @param entity The entity to save
	 */
	fun save(entity: T)

	/**
	 * Deletes an entity
	 * @param id The entity to delete
	 */
	fun deleteById(id: UInt)

	/**
	 * Deletes all entities
	 */
	fun clear()
}
