package jpadb

import UserRepositoryInterface
import model.User

class UserRepository() : UserRepositoryInterface {

	override fun joinChannel(
		uId: UInt,
		channelId: UInt,
	) {
        // Implementation
    }

	override fun findById(id: Int): User? {
        TODO("Not yet implemented")
    }

	override fun findAll(): List<User> {
        TODO("Not yet implemented")
    }

	override fun save(entity: User) {
        TODO("Not yet implemented")
    }

	override fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }
}

