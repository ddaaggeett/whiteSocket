const fs = require('fs')
const path = require('path')
const config = require('../../config')
const { networkInterfaces } = require('os')
const nets = networkInterfaces()

const initImageDataDirectory = () => {
    fs.mkdir(path.join(config.imageData), {recursive:true}, error => {
        if(error) console.error(error)
    })
}

const settleIPConfig = () => {
    const results = Object.create({})
    for (const name of Object.keys(nets)) {
        for (const net of nets[name]) {
            if (net.family === 'IPv4' && !net.internal) {
                if (!results[name]) results[name] = []
                results[name].push(net.address)
            }
        }
    }
    config.serverIP = results['wlp2s0'][0]
    fs.writeFile(config.configFile, JSON.stringify(config, null, 4), (error) => {})
}

module.exports = {
    settleIPConfig,
    initImageDataDirectory,
}
