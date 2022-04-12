import React, { useRef, useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { ImageBackground, View, Image, StyleSheet } from 'react-native'
import * as actions from '../redux/actions/actionCreators'
const config = require('../../../config')

export default (props) => {

    const redux = useDispatch()
    const { current, prepping, outputShape } = useSelector(state => state.app)
    const [fullscreen, setFullscreen] = useState(false)
    const [height, setHeight] = useState(window.innerHeight)
    const [width, setWidth] = useState(window.innerWidth)
    const imageBaseURI = `http://${config.serverIP}:${config.expressPort}/`
    const [imageURI, setImageURI] = useState(`${imageBaseURI}${current.result_uri}`)

    useEffect(() => {
        if(prepping) setImageURI(`${imageBaseURI}${config.defaultImage}`)
    }, [prepping])

    useEffect(() => {
        setImageURI(`${imageBaseURI}${current.result_uri}`)
    }, [current])

    const scaleImage = () => {
        if(current.shape != undefined){
        if(outputShape.width/outputShape.height <= current.shape.width/current.shape.height) {
            setWidth(window.innerWidth)
            setHeight(window.innerWidth/current.shape.width*current.shape.height)
        }
        else {
            setHeight(window.innerHeight)
            setWidth(window.innerHeight/current.shape.height*current.shape.width)
        }
        }
    }

    const handleResize = () => {
        redux(actions.updateOutputShape({
            width: window.innerWidth,
            height: window.innerHeight,
        }))
        scaleImage()
    }

    useEffect(() => {
        scaleImage()
        window.addEventListener("resize", handleResize);
        return () => window.removeEventListener("resize", handleResize)
    }, [])

    return (
        <ImageBackground
            source={{uri: `${imageBaseURI}${config.defaultImage}`}}
            style={{width: outputShape.width, height: outputShape.height}}
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
