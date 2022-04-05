import * as actions from '.'

export function updateFrame(diff) {
    return {
        type: actions.UPDATE_FRAME,
        diff
    }
}

export const prepCapture = prepping => {
    return {
        type: actions.PREP_CAPTURE,
        prepping
    }
}
