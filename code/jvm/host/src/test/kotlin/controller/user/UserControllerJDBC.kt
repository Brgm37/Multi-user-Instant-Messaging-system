package controller.user

import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("jdbc_test")
class UserControllerJDBC : AbstractUserControllerTest()