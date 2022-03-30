import * as actions from '.'

export function updateFrame(diff) {
    return {
        type: actions.UPDATE_FRAME,
        diff
    }
}
