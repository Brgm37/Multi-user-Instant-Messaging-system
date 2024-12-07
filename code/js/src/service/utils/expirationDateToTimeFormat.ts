
export function expirationDateToTimeFormat(expirationDate: string): string {
    const currentTime = Date.now();
    let additionalTime = 0;
    switch (expirationDate) {
        case "30 minutes":
            additionalTime = 30 * 60 * 1000;
            break;
        case "1 hour":
            additionalTime = 60 * 60 * 1000;
            break;
        case "6 hours":
            additionalTime = 6 * 60 * 60 * 1000;
            break;
        case "1 day":
            additionalTime = 24 * 60 * 60 * 1000;
            break;
        case "7 days":
            additionalTime = 7 * 24 * 60 * 60 * 1000;
            break;
        default:
            throw new Error("Invalid expiration date");
    }

    const actualDate = new Date(currentTime + additionalTime);
    return actualDate.toLocaleString("sv-SE", { year: "numeric", month: "2-digit", day: "2-digit", hour: "2-digit", minute: "2-digit", second: "2-digit" }).replace(" ", "T");
}