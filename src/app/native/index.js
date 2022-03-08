import React from 'react'
import { View, StyleSheet } from 'react-native'
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs'
import Camera from './Camera'

const Tab = createBottomTabNavigator()

export default () => {

    const screenOptions = {
        activeBackgroundColor: '#222',
        inactiveBackgroundColor: 'black',
        labelPosition: 'beside-icon',
        labelStyle:{fontSize:20,position:'absolute',color:'white'},
    }

    return (
        <View style={styles.container}>
            <Tab.Navigator screenOptions={screenOptions}>
                <Tab.Screen name="Home" component={Camera} />
            </Tab.Navigator>
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
})
