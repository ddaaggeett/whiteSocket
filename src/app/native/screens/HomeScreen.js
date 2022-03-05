import React from 'react'
import { View, Text, ScrollView, StyleSheet } from 'react-native'
import { StatusBar } from 'expo-status-bar';
import { SafeAreaView } from 'react-native-safe-area-context'

export default () => {

    return (
        <SafeAreaView>
        <ScrollView>
            <StatusBar style="light" />
            <Text>whitesocket native app</Text>
        </ScrollView>
        </SafeAreaView>
    )
}
