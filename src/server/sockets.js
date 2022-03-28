const express = require('express')
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
        diff.handle(data).then(diffObject => io.emit('updateFrame', diffObject))
    })
})

http.listen(config.socketPort, function(){
    console.log('socket.io listening on *:' + config.socketPort)
})
