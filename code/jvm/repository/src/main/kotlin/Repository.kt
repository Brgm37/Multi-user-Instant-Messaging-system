/**
 * Generic repository interface for basic CRUD operations
 */
//TODO: Check if is reasonable to return a sequence of entities in findAll method
interface Repository<T> {

    /**
     * Retrieves an entity by its ID
     * @param id The ID of the entity to retrieve
     * @return The entity with the given ID or null if it does not exist
     */
    fun findById(id: UInt): T?

    /**
     * Retrieves all entities
     * @return A list with all entities
     */
    fun findAll(): Sequence<T>

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
}