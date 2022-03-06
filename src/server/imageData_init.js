const fs = require('fs')
const path = require('path')
const { imageData } = require('../../config')

fs.mkdir(path.join(imageData), {recursive:true}, error => {
    if(error) console.error(error)
})
