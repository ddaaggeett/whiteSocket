import * as actions from '../actions'

const initialState = {
    frame: {
        current: 'blank.jpg',
        prev: null,
        id: null,
    },
}

export default function app(state = initialState, action) {
    switch(action.type) {

        case actions.UPDATE_FRAME:
            return {
                ...state,
                frame: action.frame,
            }

        default:
            return state
    }
}
