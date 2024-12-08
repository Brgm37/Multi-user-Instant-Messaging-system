package pipeline

import com.example.appWeb.model.dto.input.user.AuthenticatedUserInputModel
import interfaces.AuthServiceInterface
import jakarta.servlet.http.Cookie
import org.springframework.stereotype.Component
import utils.Failure
import utils.Success

/**
 * Processes the authorization header.
 *
 * @property service the user services interface
 */
@Component
class RequestTokenProcessor(
    private val service: AuthServiceInterface,
) {
    fun processAuthorizationHeader(authorizationValue: String): AuthenticatedUserInputModel? {
        val parts = authorizationValue.trim().split(" ")
        if (parts.size != 2) {
            return null
        }
        val (scheme, token) = parts
        if (scheme.lowercase() != SCHEME) {
            return null
        }
        return service
            .getUserByToken(token)
            .let { resp ->
                when (resp) {
                    is Success -> {
                        val uId = checkNotNull(resp.value.uId) { "User ID not found" }
                        AuthenticatedUserInputModel(uId, token)
                    }

                    is Failure -> null
                }
            }
    }

    fun processToken(cookie: Cookie): AuthenticatedUserInputModel? =
        service
            .getUserByToken(cookie.value)
            .let { resp ->
                when (resp) {
                    is Success -> {
                        val uId = checkNotNull(resp.value.uId) { "User ID not found" }
                        AuthenticatedUserInputModel(uId, cookie.value)
                    }

                    is Failure -> null
                }
            }

    companion object {
        const val SCHEME = "bearer"
    }
}