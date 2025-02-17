/**
 * Represents the state of the registration form.
 *
 * The state can be in one of the following states:
 * - "editing": The user is currently editing the form.
 * - "submitting": The form is currently being submitted.
 * - "error": An error occurred while submitting the form.
 * - "redirect": The form was successfully submitted and the user is being redirected.
 *
 * @type RegisterState
 * @prop tag The state of the registration form.
 * @prop isValid Whether the form is valid.
 * @prop message The error message.
 */
export type RegisterState =
    { tag: "editing", isValid: boolean } |
    { tag: "submitting" } |
    { tag: "error", message: string } |
    { tag: "redirect" }