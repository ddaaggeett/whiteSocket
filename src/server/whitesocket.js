const { exec } = require('child_process')
const { imageData } = require('../../config')
const path = require('path')

const whitesocket = (input,output,mode) => {
    return new Promise(function(resolve, reject) {
        exec(`python ./src/server/index.py ${input} ${output} ${mode}`, (error, stdout, stderr) => {
            if(!error) resolve()
        })
    })
}

module.exports = whitesocket
