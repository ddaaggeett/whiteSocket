import React from 'react'
import { View, Text, StyleSheet } from 'react-native'
import Whiteboard from './Whiteboard'

export default () => {

    const image = 'input.jpg'

    return (
        <View style={styles.container}>
            <Text>whitesocket web app</Text>
            <Whiteboard image={image} />
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
})
