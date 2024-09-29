package jdbc

import ChannelRepositoryInterface
import model.Channel
import org.postgresql.ds.PGSimpleDataSource

class ChannelJDBC(
	private val envName: String
):ChannelRepositoryInterface {
	private val connection = PGSimpleDataSource()
	init {
		val connectionURL = System.getenv(envName)
		connection.setURL(connectionURL)
	}
	override fun createChannel(channel: Channel): Channel {
		TODO("Not yet implemented")
	}

	override fun findById(id: Int): Channel? {
		TODO("Not yet implemented")
	}

	override fun findAll(): List<Channel> {
		TODO("Not yet implemented")
	}

	override fun save(entity: Channel) {
		TODO("Not yet implemented")
	}

	override fun deleteById(id: Int) {
		TODO("Not yet implemented")
	}

}