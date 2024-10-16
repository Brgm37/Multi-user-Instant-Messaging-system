package controller.channel

import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("jdbc", "hikari")
class ChannelControllerJDBC : AbstractChannelControllerTest()