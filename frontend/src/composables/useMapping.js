
export function useMapping() {
    function minutesToHrsAndMins(time) {
        return {
            hours: Math.floor(time / 60),
            minutes: time % 60,
        }
    }


    function hrsAndMinsToMinutes(hours, minutes) {
        return (hours || 0) * 60 + (minutes || 0)
    }

    return {
        minutesToHrsAndMins,
        hrsAndMinsToMinutes,
    }
}
