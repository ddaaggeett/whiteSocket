import React from 'react'
import { View, Text, StyleSheet } from 'react-native'
import { useSelector } from 'react-redux'
import Whiteboard from './Whiteboard'
import Arucos from './Arucos'
import useDataSocketHook from '../dataSocket'
import io from 'socket.io-client'
import { serverIP, socketPort } from '../../../config'
const socket = io.connect('http://' + serverIP + ':' + socketPort)

export default () => {

    useDataSocketHook()
    const { frame } = useSelector(state => state.app)
    const [image, setImage] = React.useState('blank.jpg')

    socket.on('outputImage', diff => {
        setImage(diff.result_uri_static)
    })

    return (
        <View style={styles.container}>
            <View style={styles.arucos}><Arucos /></View>
            <View style={styles.whiteboard}><Whiteboard image={image} /></View>
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    arucos: {
        position: 'absolute',
        zIndex: 1,
    },
    whiteboard: {
        position: 'absolute',
        zIndex: 0,
    },
})
