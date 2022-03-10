const { exec } = require ('child_process')

exec(`cp ./config_example.json ./config.json`, (error, stdout, stderr) => {
    if(error) console.error(error)
    require('./rethinkDB')
    require('./expressServer')
    require('./sockets')
    require('./configure')
})
