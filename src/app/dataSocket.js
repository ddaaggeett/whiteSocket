import { useEffect } from 'react'
import { io } from 'socket.io-client'
import { serverIP, socketPort } from '../../config'
import { useDispatch } from 'react-redux'
import * as actions from './redux/actions/actionCreators'
const socket = io('http://' + serverIP + ':' + socketPort)

export default () => {

    const redux = useDispatch()

    useEffect(() => {
        socket.on('updateFrame', data => redux(actions.updateFrame(data)))
    }, [])
}
