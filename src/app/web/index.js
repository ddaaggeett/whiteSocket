import React from 'react'
import { View, Text, StyleSheet } from 'react-native'
import Whiteboard from './Whiteboard'

export default () => {

    return (
        <View style={styles.container}>
            <Text>whitesocket web app</Text>
            <Whiteboard />
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
})
