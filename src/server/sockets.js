var express = require('express')
var app = express()
var http = require('http').Server(app)
var io = require('socket.io')(http, { cors: { origin: "*", methods: ["GET", "POST"] } })
var { socketPort } = require('../../config')
const whitesocket = require('./whitesocket')

io.on('connection', (socket) => {
    socket.on('inputImage', (object, returnToSender) => {
        // TODO: whitesocket()
    })
})

http.listen(socketPort, function(){
    console.log('socket.io listening on *:' + socketPort)
})
