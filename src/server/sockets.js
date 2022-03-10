var express = require('express')
const fs = require('fs')
var app = express()
var http = require('http').Server(app)
var io = require('socket.io')(http, { cors: { origin: "*", methods: ["GET", "POST"] } })
var { socketPort } = require('../../config')
const whitesocket = require('./whitesocket')

io.on('connection', (socket) => {
    console.log('connected')
    socket.on('inputImage', (data, returnToSender) => {
        var buff = Buffer.from(data.image, 'base64')
        fs.writeFileSync('../whitesocket_data/image.jpg', buff)
        // TODO: whitesocket()
    })
})

http.listen(socketPort, function(){
    console.log('socket.io listening on *:' + socketPort)
})
