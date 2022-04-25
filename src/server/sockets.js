const express = require('express')
const config = require('../../config')
const diff = require('./diff')
const user = require('./user')

var app = express()
var http = require('http').Server(app)
var io = require('socket.io')(http, {
    cors: { origin: "*", methods: ["GET", "POST"] },
    maxHttpBufferSize: 1e8, // for expo camera image quality: 1.0
})

io.on('connection', (socket) => {
    socket.on('syncUserState', appState => user.save(appState))
    socket.on('capture', (data, returnToSender) => {
        diff.handle(data)
        .then(object => io.emit('updateCurrent', object))
        .catch(error => {
            if(error === 'recapture') returnToSender(error)
        })
    })
    socket.on('prepCapture', () => io.emit('prepCapture'))
    socket.on('capturePrepped', () => io.emit('capturePrepped'))
})

http.listen(config.socketPort, function(){
    console.log('socket.io listening on *:' + config.socketPort)
})
