
export function useMapping() {
    function minutesToHrsAndMins(time) {
        return {
            hours: Math.floor(time / 60),
            minutes: time % 60,
        }
    }


    return {
        minutesToHrsAndMins
    }
}
