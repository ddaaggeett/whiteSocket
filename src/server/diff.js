const path = require('path')
const fs = require('fs')
const config = require('../../config')
const whitesocket = require('./whitesocket')
const r = require('rethinkdb')

const apply = (diff) => {
    return new Promise((resolve, reject) => {
        whitesocket(diff)
        .then(() => resolve())
        .catch(error => reject())
    })
}

const getPrevDiff = user => {
    return new Promise((resolve, reject) => {
        r.connect(config.dbConnxConfig).then(connection => {
            r.table('users').get(user).run(connection)
            .then(user => {
                if (user.diff.id == undefined) { // first diff
                    const id = null
                    const uri = user.diff.result
                    resolve({id,uri})
                }
                else r.table('diffs').get(user.diff.id).run(connection)
                .then(diff => {
                    const id = user.diff.id
                    const uri = diff.result
                    resolve({id,uri})
                })
                .catch(error => {})
            })
            .catch(error => {})
        })
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
        const dir = path.join(config.imageData, path.dirname(diff.uri))
        const uri = path.join(config.imageData, diff.uri)
        fs.mkdir(dir, {recursive:true}, error => {
            if(!error) {
                const buffer = Buffer.from(imageBinaryString, 'base64')
                fs.writeFile(uri, buffer, (error) => {
                    if (!error) resolve()
                })
            }
        })
    })
}

const handle = (data) => {
    return new Promise((resolve, reject) => {
        const dir = path.join(data.timestamp.toString())
        const uri = path.join(dir,'diff.jpg')
        const result = path.join(dir,'result.jpg')
        let diff = {
            uri,
            result,
            mode: data.mode,
            user: data.user,
        }
        binaryStringToFile(diff, data.imageBinaryString).then(() => {
            getPrevDiff(diff.user)
            .then(prev => {
                diff = {
                    ...diff,
                    prev_id: prev.id,
                    prev_uri: prev.uri,
                }
                apply(diff).then(() => {
                    save(diff).then(result => resolve(result))
                })
            })
            .catch(error => {})
        })
    })
}

module.exports = {
    handle,
    save,
    apply,
    binaryStringToFile,
}
