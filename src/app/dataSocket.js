import { useEffect } from 'react'
import { useSelector } from 'react-redux'
import { io } from 'socket.io-client'
import { serverIP, socketPort } from '../../config'
import { useDispatch } from 'react-redux'
import * as actions from './redux/actions/actionCreators'
const socket = io('http://' + serverIP + ':' + socketPort)

export default () => {

    const redux = useDispatch()
    const appState = useSelector(state => state.app)

    useEffect(() => {
        socket.emit('syncUserState', appState)
        socket.on('updateFrame', diff => redux(actions.updateFrame(diff)))
    }, [])

    useEffect(() => {
        socket.emit('syncUserState', appState)
    }, [appState.diff])
}
