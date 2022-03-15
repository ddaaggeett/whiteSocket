const express = require('express')
const path = require('path')
const config = require('../../config')
const fs = require('fs')
const whitesocket = require('./whitesocket')

var app = express()
var http = require('http').Server(app)
var io = require('socket.io')(http, {
    cors: { origin: "*", methods: ["GET", "POST"] },
    maxHttpBufferSize: 1e8, // for expo camera image quality: 1.0
})

io.on('connection', (socket) => {
    console.log('connected')
    socket.on('inputImage', (data, returnToSender) => {
        var buff = Buffer.from(data.image, 'base64')
        const input_dir = path.join(config.imageData, data.timestamp.toString())
        const input_uri = path.join(input_dir,'input.jpg')
        const output_uri = path.join(input_dir,'output.jpg')
        fs.mkdir(input_dir, {recursive:true}, error => {
            if(!error) fs.writeFile(input_uri, buff, (error) => {
                if (!error) whitesocket(input_uri, output_uri, data.mode)
            })
        })

    })
})

http.listen(config.socketPort, function(){
    console.log('socket.io listening on *:' + config.socketPort)
})
