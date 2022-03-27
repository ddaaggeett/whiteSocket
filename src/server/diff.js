const path = require('path')
const fs = require('fs')
const { dbConnxConfig } = require('../../config')
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
        r.connect(dbConnxConfig).then(connection => {
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

module.exports = {
    save,
    apply,
    binaryStringToFile,
}
