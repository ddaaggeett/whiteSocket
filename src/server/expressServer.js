const express = require('express')
const app = express()
const { dbConnxConfig, expressPort, appName, serverIP, imageData } = require('../../config')
const r = require('rethinkdb')
const path = require('path')

app.use(express.static(path.join(__dirname, '/../..', imageData)))

app.get('/:frameID', (req, res) => {
    // TODO: import * as Linking from 'expo-linking'
    // if (Platform.OS === 'web') Linking.openURL(`http://${serverIP}:${expressPort}/${frameID}`)
    const frameID = res.req.params.frameID
    r.connect(dbConnxConfig).then(connection => {
        r.table('frames').get(frameID).run(connection).then(response => {
        })
    })
})

app.listen(expressPort, (err) => {
    if (err) throw err
    console.log(`${appName} express server ready at http://${serverIP}:${expressPort}`)
})
