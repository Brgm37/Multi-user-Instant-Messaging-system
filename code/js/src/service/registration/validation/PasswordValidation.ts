
/**
 * Password validation patterns
 */
const upperCasePatter = /[A-Z]/
const lowerCasePatter = /[a-z]/
const digitPatter = /[0-9]/
const specialCharacter = /[!@#$%^&*(),.?":{}|<>]/

/**
 * Validate the password
 * @param password The password to validate
 * @returns The error message or true if the password is valid
 */
export function passwordValidation(password: string): string | true {
    if (password.length == 0) {
      return ""
    }
    if (!upperCasePatter.test(password)) {
        return "The password should have at least one upper case letter."
    }
    if (!lowerCasePatter.test(password)) {
        return "The password should have at least one lower case letter."
    }
    if (!digitPatter.test(password)) {
        return "The password should have at least one digit."
    }
    if (!specialCharacter.test(password)) {
        return "The password should have at least one special character."
    }
    if (password.length < 8) {
        return "The password should have at least 8 characters."
    }
    return true
}
