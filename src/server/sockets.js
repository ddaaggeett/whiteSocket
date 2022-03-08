var express = require('express')
const fs = require('fs')
var app = express()
var http = require('http').Server(app)
var io = require('socket.io')(http, { cors: { origin: "*", methods: ["GET", "POST"] } })
var { socketPort } = require('../../config')
const whitesocket = require('./whitesocket')

io.on('connection', (socket) => {
    socket.on('inputImage', (image, returnToSender) => {
        const buffer = Buffer.from(image.base64, 'base64');
        fs.writeFileSync('../whitesocket_data/image.png', buffer)
        // TODO: not saving to file properly
        // TODO: whitesocket()
    })
})

http.listen(socketPort, function(){
    console.log('socket.io listening on *:' + socketPort)
})
