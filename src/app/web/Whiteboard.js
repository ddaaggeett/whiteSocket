import React, { useRef, useState } from 'react'
import { Image, StyleSheet } from 'react-native'

export default () => {

    const whiteboardRef = useRef()
    const [fullscreen, setFullscreen] = useState(false)
    const frame = require('../../../whitesocket_data/input.jpg')

    const handleFullscreen = () => {
        if(!fullscreen) {
            whiteboardRef.current.requestFullscreen()
            .then(() => setFullscreen(true))
            .catch(error => console.error(error))
        }
        else {
            document.exitFullscreen()
            setFullscreen(false)
        }
    }

    return (
        <Image
            style={styles.frame}
            source={frame}
            ref={whiteboardRef}
            onClick={handleFullscreen}
            />
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    frame: {
        height: 300,
        width: 400,
    },
})
