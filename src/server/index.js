const { exec } = require('child_process')
const { imageData } = require('../../config')
const path = require('path')

console.log('\nwhitesocket\n')

require('./rethinkDB')
const timestamp = Date.now().toString()
const inputImage = path.join(imageData,'input.png')
const outputImage = path.join(imageData,'output.png')

exec(`python ./src/server/index.py ${inputImage} ${outputImage}`, (error, stdout, stderr) => {
    if(error) {
        console.log(error)
        return
    }
    console.log(stdout)
})
