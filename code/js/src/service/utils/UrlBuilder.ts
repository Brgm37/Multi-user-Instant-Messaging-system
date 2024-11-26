/**
 * Build a URL with the given path
 * @param path The path to build the URL with
 * @returns The URL with the given path
 */
export function urlBuilder(path: string): string {
    return `${window.location.origin}/api${path}`;
}