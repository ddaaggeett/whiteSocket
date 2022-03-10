const fs = require('fs')
const path = require('path')
const config = require('../../config')
const { exec } = require('child_process')

fs.mkdir(path.join(config.imageData), {recursive:true}, error => {
    if(error) console.error(error)
    exec('hostname -I', (error, stdout, stderr) => {
        if (!error) {
            const cut = stdout.indexOf(' \n')
            const serverIP = stdout.slice(0,cut)
            config.serverIP = serverIP
            fs.writeFile(config.configFile, JSON.stringify(config, null, 4), (error) => {
            })
        }
    })
})
