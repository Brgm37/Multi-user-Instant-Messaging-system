package jdbc

import model.*
import model.AccessControl.READ_WRITE
import model.Visibility.PRIVATE
import model.Visibility.PUBLIC
import org.eclipse.jetty.util.security.Password
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import java.sql.Connection
import kotlin.random.Random
import kotlin.test.*

class ChannelJDBCTest {
	companion object {
		private fun runWithConnection(block: (Connection) -> Unit) =
			PGSimpleDataSource()
				.apply { setURL("jdbc:postgresql://localhost:5433/daw_test?user=postgres&password=password") }
				.connection
				.let(block)
	}

	@BeforeEach
	fun clean() {
		runWithConnection { connection ->
			ChannelJDBC(connection).clear()
			UserJDBC(connection).clear()
			MessageJDBC(connection).clear()
		}
	}

	private fun createChannel(connection: Connection, visibility: Visibility = PUBLIC): Channel =
		UserJDBC(connection)
			.createUser(
				User(
					username = "user${System.currentTimeMillis() + Random.nextInt()}",
					password = Password("password")
				)
			)
			.let { user ->
				assertNotNull(user)
				assertNotNull(user.uId)
				val id = requireNotNull(user.uId) { "User id is null" }
				ChannelJDBC(connection)
					.createChannel(
						Channel
							.createChannel(
								owner = UserInfo(id, user.username),
								name = ChannelName("channel", user.username),
								accessControl = READ_WRITE,
								visibility = visibility
							)
					)
			}

	@Test
	fun `create a channel and find it`() = runWithConnection { connection ->
		assertNotNull(createChannel(connection).id)
	}

	@Test
	fun `find a channel by id`() = runWithConnection { connection ->
		val channel = createChannel(connection)
		val id = requireNotNull(channel.id) { "Channel id is null" }
		val foundChannel = ChannelJDBC(connection).findById(id)
		assertNotNull(foundChannel)
	}

	@Test
	fun `find a channel by name`() = runWithConnection { connection ->
		UserJDBC(connection)
			.createUser(
				User(
					username = "user",
					password = Password("password")
				)
			)
			.let { user ->
				assertNotNull(user)
				val id = requireNotNull(user.uId) { "User id is null" }
				val numberOfChannel = 5
				repeat(numberOfChannel) {
					Channel.createChannel(
						owner = UserInfo(id, user.username),
						name = ChannelName("channel$it", user.username),
						accessControl = READ_WRITE,
						visibility = PUBLIC
					).let { channel ->
						ChannelJDBC(connection).createChannel(channel)
					}
				}
				ChannelJDBC(connection)
					.findByUserId(id)
					.let { channels ->
						assertTrue(channels.isNotEmpty())
						assertEquals(numberOfChannel, channels.size)
					}
			}
	}

	@Test
	fun `test update a channel`() {
		runWithConnection { connection ->
			val channel = createChannel(connection)
			assertIs<Channel.Public>(channel)
			val id = requireNotNull(channel.id) { "Channel id is null" }
			val updatedChannel = channel.copy(
				name = ChannelName("new name", channel.owner.username),
			)
			ChannelJDBC(connection).save(updatedChannel)
			val foundChannel = ChannelJDBC(connection).findById(id)
			assertEquals(updatedChannel, foundChannel)
		}
	}

	@Test
	fun `test delete a channel`() {
		runWithConnection { connection ->
			val channel = createChannel(connection)
			val id = requireNotNull(channel.id) { "Channel id is null" }
			ChannelJDBC(connection).deleteById(id)
			assertNull(ChannelJDBC(connection).findById(id))
		}
	}

	@Test
	fun `test find all channels`() {
		runWithConnection { connection ->
			val numberOfChannels = 5
			val channels = (0 until numberOfChannels).map {
				createChannel(connection)
			}
			createChannel(connection, PRIVATE)
				.let { channel ->
					assertIs<Channel.Private>(channel)
					assertNotNull(channel.id)
					val id = requireNotNull(channel.id) { "Channel id is null" }
					assertNotNull(ChannelJDBC(connection).deleteById(id))
				}
			val foundChannels = ChannelJDBC(connection).findAll()
			assertEquals(numberOfChannels, foundChannels.size)
			assertEquals(channels, foundChannels)
		}
	}
}