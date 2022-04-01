import * as actions from '../actions'

const initialState = {
    diff: {
        result_uri: 'blank.jpg',
    },
    id: 'defaultUser', // userID
}

export default function app(state = initialState, action) {
    switch(action.type) {

        case actions.UPDATE_FRAME:
            return {
                ...state,
                diff: action.diff,
            }

        default:
            return state
    }
}
