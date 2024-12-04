/**
 * The action that can be performed on the registration form.
 *
 * The action can be one of the following:
 * - "submit": The user has submitted the form.
 * - "success": The form was successfully submitted.
 * - "edit": The user is currently editing the form.
 * - "go-back": When an error occurs the user can go back to editing state.
 * - "error": An error occurred while submitting the form.
 */
export type RegisterAction =
    { type: "submit" } |
    { type: "success" } |
    { type: "edit", isValid: boolean } |
    { type: "go-back"} |
    { type: "error", message: string }