const { exec } = require('child_process')
const { imageData } = require('../../config')
const path = require('path')

const whitesocket = (input,output,mode) => {
    exec(`python ./src/server/index.py ${input} ${output} ${mode}`, (error, stdout, stderr) => {
        if(error) {
            console.log(error)
            return
        }
        console.log(stdout)
    })
}

module.exports = whitesocket
