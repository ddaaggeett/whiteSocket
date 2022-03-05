import { Platform, StyleSheet, Text, View } from 'react-native'
import WebApp from './src/app/web'
import NativeApp from './src/app/native'
import { NavigationContainer } from '@react-navigation/native';

export default function App() {
    if (Platform.OS === 'web') return <WebApp />
    else return (
        <NavigationContainer>
            <NativeApp />
        </NavigationContainer>
    )
}
