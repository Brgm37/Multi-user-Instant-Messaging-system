/**
 * Extract the search parameters from the URL.
 *
 * @param search The search string.
 * @returns string
 */
export function extractSearchParams(search: string): string {
    if (search == null)
        return ""
    return decodeURIComponent(search)
}