import zxcvbn from 'zxcvbn'
import {ZXCVBNResult} from "zxcvbn";

const upperCasePatter = /[A-Z]/
const lowerCasePatter = /[a-z]/
const digitPatter = /[0-9]/
const specialCharacter = /[!@#$%^&*(),.?":{}|<>]/

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
    const result:ZXCVBNResult = zxcvbn(password)
    if (result.score < 2) {
        return result.feedback.warning
    }
    return true
}
