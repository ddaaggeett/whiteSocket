const r = require('rethinkdb')
const config = require('../../config')

const save = userState => {
    r.connect(config.dbConnxConfig).then(connection => {
        r.table('users').insert(userState, { returnChanges: true, conflict: 'update' }).run(connection)
        .then(result => {
            console.log(result.changes[0].new_val)
        })
        .catch(error => {
            console.log(`\nuser save error\n${error}`)
        })
    })
    .catch(error => {
        console.log(`\nuser save db connection error\n${error}`)
    })
}

module.exports = {
    save,
}
