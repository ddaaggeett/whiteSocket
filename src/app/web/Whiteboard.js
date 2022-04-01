import React, { useRef, useState, useEffect } from 'react'
import { useSelector } from 'react-redux'
import { View, Image, StyleSheet } from 'react-native'
const config = require('../../../config.json')

export default (props) => {

    const { diff } = useSelector(state => state.app)
    const whiteboardRef = useRef()
    const [fullscreen, setFullscreen] = useState(false)
    const [height, setHeight] = useState(window.innerHeight)
    const [width, setWidth] = useState(window.innerWidth)

    const imageURI = `http://${config.serverIP}:${config.expressPort}/${diff.result_uri}`

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

    useEffect(() => {
        setHeight(window.innerHeight)
        setWidth(window.innerWidth)
    }, [window.innerHeight])

    useEffect(() => {
        setHeight(window.innerHeight)
        setWidth(window.innerWidth)
    }, [])

    return (
        <Image
            source={{ uri: imageURI }}
            style={{width,height}}
            ref={whiteboardRef}
            onClick={handleFullscreen}
            />
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    image: {
    },
})
