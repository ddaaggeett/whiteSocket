import React, { useRef, useState, useEffect } from 'react'
import { useSelector } from 'react-redux'
import { ImageBackground, View, Image, StyleSheet } from 'react-native'
const config = require('../../../config')

export default (props) => {

    const { diff, prepping } = useSelector(state => state.app)
    const [fullscreen, setFullscreen] = useState(false)
    const [backgroundHeight, setBackgroundHeight] = useState(window.innerHeight)
    const [backgroundWidth, setBackgroundWidth] = useState(window.innerWidth)
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

    useEffect(() => {
        setBackgroundHeight(window.innerHeight)
        setBackgroundWidth(window.innerWidth)
    }, [window.innerHeight, window.innerWidth])

    return (
        <ImageBackground
            source={{uri: `${imageBaseURI}${config.defaultImage}`}}
            style={{height: backgroundHeight, width: backgroundWidth}}
            >
            <Image
                source={{ uri: imageURI }}
                style={{width, height}}
                />
        </ImageBackground>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    image: {
    },
})
