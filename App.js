import { Platform, StyleSheet, Text, View } from 'react-native'
import WebApp from './src/app/web'
import NativeApp from './src/app/native'
import { NavigationContainer } from '@react-navigation/native';
import { Provider } from 'react-redux'
import { PersistGate } from 'redux-persist/integration/react'
import configureStore from './src/app/redux';

const store = configureStore().store
const persistor = configureStore().persistor

export default function App() {
    if (Platform.OS === 'web') return (
        <Provider store={store}>
        <PersistGate loading={null} persistor={persistor}>
            <WebApp />
        </PersistGate>
        </Provider>
    )
    else return (
        <NavigationContainer>
            <NativeApp />
        </NavigationContainer>
    )
}
