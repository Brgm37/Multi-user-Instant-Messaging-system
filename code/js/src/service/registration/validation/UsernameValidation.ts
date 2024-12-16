/**
 * Username validation patterns.
 */
const upperCasePatter = /[A-Z]/
const lowerCasePatter = /[a-z]/

/**
 * Validate the username.
 * @param username The username to validate.
 * @returns A string with the error message or true if the username is valid.
 */
export function usernameValidation(username: string): string | true {
    if (username.length === 0) {
        return ""
    }
    if (username.length < 3) {
        return "The username should have at least 3 characters."
    }
    if (!upperCasePatter.test(username)) {
        return "The username should have at least one upper case letter."
    }
    if (!lowerCasePatter.test(username)) {
        return "The username should have at least one lower case letter."
    }
    return true
}