const { exec } = require('child_process')
const path = require('path')
const config = require('../../../config')

const whitesocket = diff => {
    return new Promise(function(resolve, reject) {
        exec(`python ./src/server/whitesocket/index.py '${JSON.stringify(diff)}'`, (error, stdout, stderr) => {
            if(!error) resolve()
        })
    })
}

module.exports = whitesocket
