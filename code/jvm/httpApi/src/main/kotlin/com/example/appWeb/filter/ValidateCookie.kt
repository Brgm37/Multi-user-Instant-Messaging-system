package com.example.appWeb.filter

import com.example.appWeb.model.problem.Problem
import interfaces.UserServicesInterface
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpFilter
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus.UNAUTHORIZED
import utils.Failure
import utils.Success

class ValidateCookie(
	private val service: UserServicesInterface,
) : HttpFilter() {
	override fun doFilter(
		request: ServletRequest,
		response: ServletResponse,
		chain: FilterChain,
	) {
		val httpRequest = request as HttpServletRequest
//            val httpResponse = response as jakarta.servlet.http.HttpServletResponse
		val cookie = httpRequest.cookies?.find { it.name == "session" }
		if (cookie == null) {
//                httpResponse.sendRedirect("/login") // TODO: confirm if this is the correct redirect
			Problem.Unauthorized.response(UNAUTHORIZED)
			return
		}
		when (val isValid = service.isValidToken(cookie.value)) {
			is Success -> {
				if (isValid.value) {
					chain.doFilter(request, response)
				} else {
//                        httpResponse.sendRedirect("/login") // TODO: confirm if this is the correct redirect
					Problem.Unauthorized.response(UNAUTHORIZED)
				}
			}

			is Failure -> {
//                    httpResponse.sendRedirect("/login") // TODO: confirm if this is the correct redirect
				Problem.Unauthorized.response(UNAUTHORIZED)
			}
		}
	}
}
