/**
 * The action that can be performed on the login form.
 *
 * The action can be one of the following:
 * - "submit": The user has submitted the form.
 * - "success": The form was successfully submitted.
 * - "edit": The user is currently editing the form.
 * - "go-back": When an error occurs the user can go back to editing state.
 * - "error": An error occurred while submitting the form.
 *
 * @type LoginAction
 * @prop type The type of the action.
 * @prop isValid The validity of the form.
 * @prop message The error message.
 */
export type LoginAction =
    { type: "submit" } |
    { type: "success" } |
    { type: "edit", isValid: boolean } |
    { type: "go-back"} |
    { type: "error", message: string }