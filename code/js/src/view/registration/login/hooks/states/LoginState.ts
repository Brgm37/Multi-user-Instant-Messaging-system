/**
 * Represents the state of the login form.
 *
 * The state can be in one of the following states:
 * - "editing": The user is currently editing the form.
 * - "error": An error occurred while submitting the form.
 * - "submitting": The form is currently being submitted.
 * - "redirect": The form was successfully submitted and the user is being redirected.
 *
 * @type LoginState
 * @property tag The tag of the state.
 * @property isValid Whether the form is valid.
 * @property message The error message.
 */
export type LoginState =
    { tag: "editing", isValid: boolean } |
    { tag: "error", message: string } |
    { tag: "submitting" } |
    { tag: "redirect" }
