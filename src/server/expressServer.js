const express = require('express')
const app = express()
const { dbConnxConfig, expressPort, appName, serverIP, imageData } = require('../../config')
const r = require('rethinkdb')
const path = require('path')

app.use(express.static(path.join(__dirname, '/../..', imageData)))

app.listen(expressPort, (err) => {
    if (err) throw err
    console.log(`${appName} express server ready at http://${serverIP}:${expressPort}`)
})
