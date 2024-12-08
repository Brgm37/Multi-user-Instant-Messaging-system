/**
 * Generate a hash for the given data.
 *
 * @param data - The data to hash
 * @returns A promise that resolves to the hash as a hexadecimal string
 */
export async function generateHash(data: string): Promise<string> {
    const encoder = new TextEncoder();
    const encodedData = encoder.encode(data);
    const hashBuffer = await crypto.subtle.digest('SHA-256', encodedData);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return hashArray.map(byte => byte.toString(16).padStart(2, '0')).join('');
}