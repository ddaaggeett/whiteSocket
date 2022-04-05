import React, { useRef, useState, useEffect } from 'react'
import { useSelector } from 'react-redux'
import { View, Image, StyleSheet } from 'react-native'
const config = require('../../../config')

export default (props) => {

    const { diff, prepping } = useSelector(state => state.app)
    const whiteboardRef = useRef()
    const [fullscreen, setFullscreen] = useState(false)
    const [height, setHeight] = useState(window.innerHeight)
    const [width, setWidth] = useState(window.innerWidth)
    const imageBaseURI = `http://${config.serverIP}:${config.expressPort}/`
    const [imageURI, setImageURI] = useState(`${imageBaseURI}${diff.result_uri}`)

    useEffect(() => {
        if(prepping) setImageURI(`${imageBaseURI}${config.defaultImage}`)
    }, [prepping])

    useEffect(() => {
        setImageURI(`${imageBaseURI}${diff.result_uri}`)
    }, [diff])

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
