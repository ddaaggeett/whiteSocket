const { exec } = require('child_process')
const path = require('path')
const config = require('../../../config')

const whitesocket = (diff = config.defaultDiff) => {
    return new Promise(function(resolve, reject) {
        exec(`python ./src/server/whitesocket/index.py '${JSON.stringify(diff)}'`, (error, stdout, stderr) => {
            if(!error) {
                console.log('whitesocket python output:')
                console.log(stdout)
                resolve()
            }
            else if(error.toString().includes('ERROR: aruco corner count')) {
                console.log('ERROR: aruco corner count')
                reject('recapture')
            }
        })
    })
}

module.exports = whitesocket
