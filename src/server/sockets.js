const express = require('express')
const path = require('path')
const config = require('../../config')
const diff = require('./diff')

var app = express()
var http = require('http').Server(app)
var io = require('socket.io')(http, {
    cors: { origin: "*", methods: ["GET", "POST"] },
    maxHttpBufferSize: 1e8, // for expo camera image quality: 1.0
})

io.on('connection', (socket) => {
    socket.on('inputImage', (data, returnToSender) => {
        const dir = path.join(config.imageData, data.timestamp.toString())
        const diffObject = {
            dir,
            uri: path.join(dir,'diff.jpg'),
            result_uri: path.join(dir,'output.jpg'),
            mode: data.mode,
            timestamp: data.timestamp,
        }
        diff.binaryStringToFile(diffObject, data.imageBinaryString).then(() => {
            diff.apply(diffObject).then(() => {
                diff.save(diffObject).then(result => {
                    io.emit('outputImage', result)
                })
            })
        })
    })
})

http.listen(config.socketPort, function(){
    console.log('socket.io listening on *:' + config.socketPort)
})
