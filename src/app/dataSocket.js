import { useEffect } from 'react'
import { useSelector } from 'react-redux'
import { io } from 'socket.io-client'
import { serverIP, socketPort } from '../../config'
import { useDispatch } from 'react-redux'
import * as actions from './redux/actions/actionCreators'
import config from '../../config'
const socket = io(`http://${config.serverIP}:${config.socketPort}`)

export default () => {

    const redux = useDispatch()
    const appState = useSelector(state => state.app)

    useEffect(() => {
        socket.emit('syncUserState', appState)
        socket.on('updateFrame', diff => redux(actions.updateFrame(diff)))
        socket.on('prepCapture', () => redux(actions.prepCapture(true)))
    }, [])

    useEffect(() => {
        socket.emit('syncUserState', appState)
    }, [appState.diff])

    useEffect(() => {
        if(appState.prepping) {
            socket.emit('capturePrepped')
            redux(actions.prepCapture(false))
        }
    }, [appState.prepping])
}
