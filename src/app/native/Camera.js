import React, { useState, useEffect } from 'react'
import { StyleSheet, Text, View, TouchableOpacity } from 'react-native'
import { Camera } from 'expo-camera'
import io from 'socket.io-client'
import config from '../../../config'
const socket = io.connect(`http://${config.serverIP}:${config.socketPort}`)

export default () => {
    const [hasPermission, setHasPermission] = useState(null)
    const [cameraReady, setCameraReady] = useState(false)
    const [mode, setMode] = useState(null)

    useEffect(() => {
        (async () => {
            const { status } = await Camera.requestCameraPermissionsAsync()
            setHasPermission(status === 'granted')
        })()

        socket.on('capturePrepped', () => {
            if(cameraReady) camera.takePictureAsync({
                quality: 1.0,
                base64: true,
            }).then(image => {
                const imageBinaryString = `${image.base64}data:image/jpg;base64,`
                socket.emit('capture', {
                    timestamp: Date.now(),
                    imageBinaryString,
                    mode,
                    user: config.user,
                }, error => {
                    if(error === 'recapture') {
                        // TODO: alert user to recapture
                    }
                })
            })
        })
    }, [])

    const __takePicture = (mode) => {
        setMode(mode)
        socket.emit('prepCapture')
    }

    const handleCameraReady = () => setCameraReady(true)

    if (hasPermission === null) {
        return <View />
    }
    if (hasPermission === false) {
        return <Text>No access to camera</Text>
    }
    return (
        <View style={styles.container}>
            <Camera
                style={styles.camera}
                ref={ref => camera = ref}
                flashMode={'off'}
                autoFocus={'on'}
                onCameraReady={handleCameraReady}
                >
                <View style={styles.buttonContainer}>
                    <TouchableOpacity
                        style={styles.button}
                        onPress={() => __takePicture('erase')}>
                        <Text style={styles.text}>ERASE</Text>
                    </TouchableOpacity>
                    <TouchableOpacity
                        style={styles.button}
                        onPress={() => __takePicture('write')}>
                        <Text style={styles.text}>WRITE</Text>
                    </TouchableOpacity>
                </View>
            </Camera>
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    camera: {
        flex: 1,
    },
    buttonContainer: {
        flex: 1,
        backgroundColor: 'transparent',
        flexDirection: 'row',
        margin: 50,
    },
    button: {
        flex: 1,
        padding: 20,
        borderWidth: 1,
        borderColor: 'black',
        backgroundColor: 'rgba(2,2,2,0.5)',
        alignSelf: 'center',
        alignItems: 'center',
    },
    text: {
        fontSize: 50,
        color: 'white',
    },
})
