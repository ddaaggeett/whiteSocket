require('./rethinkDB')
require('./expressServer')
require('./sockets')
const { initImageDataDirectory, settleIPConfig } = require('./configure')

initImageDataDirectory()
settleIPConfig()
