package controller.message

import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("jdbc_test")
class MessageControllerJDBC : AbstractMessageControllerTest()