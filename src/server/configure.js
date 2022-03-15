const fs = require('fs')
const { copyFile } = require('fs/promises')
const path = require('path')
const config = require('../../config_example')
const { networkInterfaces } = require('os')
const nets = networkInterfaces()

const initImageDataDirectory = () => {
    fs.mkdir(path.join(config.imageData), {recursive:true}, error => {
        if(error) console.error(error)
    })
}

const writeConfigFile = (config) => {
    return new Promise((resolve,reject) => {
        fs.writeFile(config.configFile, JSON.stringify(config, null, 4), (error) => {
            if(!error) resolve()
        })
    })
}

const settleIPConfig = () => {
    return new Promise((resolve,reject) => {

        const results = Object.create({})
        for (const name of Object.keys(nets)) {
            for (const net of nets[name]) {
                if (net.family === 'IPv4' && !net.internal) {
                    if (!results[name]) results[name] = []
                    results[name].push(net.address)
                }
            }
        }
        if (results['wlp2s0']) {
            config.serverIP = results['wlp2s0'][0]
            writeConfigFile(config).then(() => resolve())
        }
        else if (results['enp8s0']) {
            config.serverIP = results['enp8s0'][0]
            writeConfigFile(config).then(() => resolve())
        }
    })
}

const initConfig = () => {
    return new Promise((resolve,reject) => {
        copyFile('./config_example.json',config.configFile).then(() => {
            initImageDataDirectory()
            settleIPConfig().then(() => resolve())
        })
    })
}

module.exports = initConfig
