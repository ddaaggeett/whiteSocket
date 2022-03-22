import React from 'react'
import { View, StyleSheet } from 'react-native'
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import Camera from './Camera'
import * as ScreenOrientation from 'expo-screen-orientation'

const Stack = createNativeStackNavigator()

export default () => {

    const screenOptions = {
        headerShown: false,
    }

    React.useEffect(() => {
        ScreenOrientation.lockAsync(ScreenOrientation.OrientationLock.LANDSCAPE_RIGHT)
    }, [])

    return (
        <View style={styles.container}>
            <Stack.Navigator screenOptions={screenOptions}>
                <Stack.Screen name="Home" component={Camera} />
            </Stack.Navigator>
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
})
