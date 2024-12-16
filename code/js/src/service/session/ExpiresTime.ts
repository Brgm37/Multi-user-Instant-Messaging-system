/**
 * Get the time in seconds until the target date.
 *
 *
 * @param dateString
 *
 * @returns The time in seconds until the target date.
 */
export function getExpiresIn(dateString: string): number {
    const targetDate = new Date(dateString);
    const currentDate = new Date();
    const differenceInMilliseconds = targetDate.getTime() - currentDate.getTime();
    return Math.floor(differenceInMilliseconds / 1000);
}