import * as actions from '../actions'

const initialState = {
    diff: {
        result: 'blank.jpg',
    },
    id: 'defaultUser', // userID
}

export default function app(state = initialState, action) {
    switch(action.type) {

        case actions.UPDATE_FRAME:
            return {
                ...state,
                diff: {
                    prev: state.diff.id,
                    result: action.diff.result,
                    ...action.diff,
                },
            }

        default:
            return state
    }
}
