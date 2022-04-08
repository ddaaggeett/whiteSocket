const { exec } = require('child_process')
const path = require('path')
const config = require('../../../config')

const whitesocket = diff => {
    const input = path.join(config.imageData, diff.uri)
    const prev = path.join(config.imageData, diff.prev_uri)
    const output = path.join(config.imageData, diff.result_uri)
    const mode = diff.mode
    return new Promise(function(resolve, reject) {
        exec(`python ./src/server/whitesocket/index.py ${input} ${prev} ${output} ${mode} ${diff.outputShape}`, (error, stdout, stderr) => {
            if(!error) resolve()
        })
    })
}

module.exports = whitesocket
