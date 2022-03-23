import * as actions from '.'

export function updateFrame(frame) {
    return {
        type: actions.UPDATE_FRAME,
        frame
    }
}
