import * as actions from '../actions'
import { defaultImage } from '../../../../config'

const initialState = {
    diff: {
        result_uri: defaultImage,
    },
    prepping: false,
    id: 'defaultUser', // userID
}

export default function app(state = initialState, action) {
    switch(action.type) {

        case actions.UPDATE_FRAME:
            return {
                ...state,
                diff: action.diff,
            }

        case actions.PREP_CAPTURE:
            return {
                ...state,
                prepping: action.prepping,
            }

        default:
            return state
    }
}
