import React from 'react'
import { View, Text, StyleSheet } from 'react-native'
import Whiteboard from './Whiteboard'
import Arucos from './Arucos'
import io from 'socket.io-client'
import { serverIP, socketPort } from '../../../config'
const socket = io.connect('http://' + serverIP + ':' + socketPort)

export default () => {

    const [image, setImage] = React.useState('default.jpg')

    socket.on('outputImage', data => {
        setImage(`${data.id}/output.jpg`)
    })

    return (
        <View style={styles.container}>
            <Arucos />
            <Whiteboard image={image} />
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
})
