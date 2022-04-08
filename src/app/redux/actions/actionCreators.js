import * as actions from '.'

export const updateFrame = diff => {
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

export const updateOutputShape = shape => {
    return {
        type: actions.UPDATE_OUTPUT_SHAPE,
        shape
    }
}
