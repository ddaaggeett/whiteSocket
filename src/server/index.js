const initConfig = require ('./configure')

initConfig().then(() => {
    require('./rethinkDB')
    require('./sockets')
    require('./expressServer')
})
