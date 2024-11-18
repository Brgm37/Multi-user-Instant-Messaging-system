package pipeline

import com.example.appWeb.model.dto.input.user.AuthenticatedUserInputModel
import interfaces.AuthServiceInterface
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
    fun processAuthorizationHeader(authorizationValue: String?): AuthenticatedUserInputModel? {
        if (authorizationValue == null) {
            return null
        }
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

    companion object {
        const val SCHEME = "bearer"
    }
}