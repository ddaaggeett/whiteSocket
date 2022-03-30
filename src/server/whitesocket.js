const { exec } = require('child_process')
const path = require('path')
const config = require('../../config')

const whitesocket = diff => {
    const input = path.join(config.imageData, diff.uri)
    const output = path.join(config.imageData, diff.result)
    const mode = diff.mode
    return new Promise(function(resolve, reject) {
        exec(`python ./src/server/index.py ${input} ${output} ${mode}`, (error, stdout, stderr) => {
            if(!error) resolve()
        })
    })
}

module.exports = whitesocket
