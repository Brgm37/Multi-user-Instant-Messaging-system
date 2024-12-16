package pipeline

import com.example.appWeb.controller.UserController
import com.example.appWeb.model.dto.input.user.AuthenticatedUserInputModel
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

/**
 * Intercepts the request to enforce authentication.
 *
 * @property processor the request token processor
 */
@Component
class AuthenticationInterceptor(
    private val processor: RequestTokenProcessor,
) : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        if (
            handler is HandlerMethod &&
            handler
                .methodParameters
                .map(MethodParameter::getParameterType)
                .any(AuthenticatedUserInputModel::class.java::equals)
        ) {
            request
                .cookies
                ?.find { it.name == UserController.AUTH_COOKIE }
                ?.let { processor.processToken(it) }
                ?.let { AuthenticatedUserArgumentResolver.addUserTo(it, request) }
                ?.let { return true }

            request
                .getHeader(NAME_AUTHORIZATION_HEADER)
                ?.let { processor.processAuthorizationHeader(it) }
                ?.let { AuthenticatedUserArgumentResolver.addUserTo(it, request) }
                ?.let { return true }

            response.status = SC_UNAUTHORIZED
            response.addHeader(NAME_WWW_AUTHENTICATE_HEADER, RequestTokenProcessor.SCHEME)
            return false
        }
        return true
    }

    companion object {
        const val NAME_AUTHORIZATION_HEADER = "Authorization"
        private const val NAME_WWW_AUTHENTICATE_HEADER = "WWW-Authenticate"
    }
}