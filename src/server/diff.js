const path = require('path')
const fs = require('fs')
const config = require('../../config')
const whitesocket = require('./whitesocket')
const r = require('rethinkdb')

const apply = (diff) => {
    return new Promise((resolve, reject) => {
        whitesocket(diff.uri, diff.result_uri, diff.mode)
        .then(() => resolve())
        .catch(error => reject())
    })
}

const save = (diff) => {
    return new Promise((resolve, reject) => {
        r.connect(config.dbConnxConfig).then(connection => {
            r.table('diffs').insert(diff, { returnChanges: true, conflict: 'update' }).run(connection)
            .then(result => {
                resolve(result.changes[0].new_val)
            })
            .catch(error => {
                console.log(`\ndiff save error\n${error}`)
                reject()
            })
        })
        .catch(error => {
            console.log(`\ndiff save db connection error\n${error}`)
            reject()
        })
    })
}

const binaryStringToFile = (diff, imageBinaryString) => {
    return new Promise((resolve,reject) => {
        fs.mkdir(diff.dir, {recursive:true}, error => {
            if(!error) {
                const buffer = Buffer.from(imageBinaryString, 'base64')
                fs.writeFile(diff.uri, buffer, (error) => {
                    if (!error) resolve()
                })
            }
        })
    })
}

const handle = (data) => {
    return new Promise((resolve, reject) => {
        const dir = path.join(config.imageData, data.timestamp.toString())
        const uri = path.join(dir,'diff.jpg')
        const result_uri = path.join(dir,'result.jpg')
        const result_uri_static = path.relative(config.imageData,result_uri)
        const diffObject = {
            dir,
            uri,
            result_uri,
            result_uri_static,
            mode: data.mode,
            timestamp: data.timestamp,
        }
        binaryStringToFile(diffObject, data.imageBinaryString).then(() => {
            apply(diffObject).then(() => {
                save(diffObject).then(result => resolve(result))
            })
        })
    })
}

module.exports = {
    handle,
    save,
    apply,
    binaryStringToFile,
}
