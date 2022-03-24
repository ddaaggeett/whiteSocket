const path = require('path')
const fs = require('fs')
const config = require('../../config')
const whitesocket = require('./whitesocket')

class Diff {

    constructor(diffObject, prev_uri='blank.jpg') {
        this.dir = diffObject.dir
        this.uri = diffObject.uri
        this.result_uri = diffObject.result_uri
        this.mode = diffObject.mode
        this.timestamp = diffObject.timestamp
        this.imageBinaryString = diffObject.imageBinaryString
    }

    apply = () => {
        return new Promise((resolve, reject) => {
            fs.mkdir(this.dir, {recursive:true}, error => {
                if(!error) {
                    var buff = Buffer.from(this.imageBinaryString, 'base64')
                    fs.writeFile(this.uri, buff, (error) => {
                        if (!error) {
                            whitesocket(this.uri, this.result_uri, this.mode)
                            .then(() => resolve())
                            .catch(error => {})
                        }
                    })
                }
            })
        })
    }
}

module.exports = Diff
