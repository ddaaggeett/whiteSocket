import * as actions from '../actions'

const initialState = {
    frame: {
        current: 'blank.jpg',
    },
    id: 'defaultUser', // userID
}

export default function app(state = initialState, action) {
    switch(action.type) {

        case actions.UPDATE_FRAME:
            return {
                ...state,
                frame: {
                    prev: state.frame.current,
                    current: action.frame.result_uri_static,
                    ...action.frame,
                },
            }

        default:
            return state
    }
}
