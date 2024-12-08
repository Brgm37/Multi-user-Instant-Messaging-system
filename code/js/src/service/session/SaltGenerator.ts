import random from "secure-random";

/**
 * Generate a random salt
 *
 * @param length
 *
 * @returns string
 */
export default function (length: number): string {
    return random(length, {type: 'Array'}).toString();
}