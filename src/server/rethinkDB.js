const process = require('process')
const r = require('rethinkdb')
var { db, tables, dbConnxConfig } = require('../../config')
const { spawn } = require('child_process')
var dbConnx = null

const createTables = (tables) => {
    for(var table in tables) {
        r.db(db).tableCreate(table).run(dbConnx).then(result => {
            console.log(`\nTABLE RESULT:\n${JSON.stringify(result,null,4)}`)
            console.log("\nRethinkDB table '%s' created", table)
        }).error(error => {
            console.log("\nRethinkDB table '%s' already exists (%s:%s)\n%s", table, error.name, error.msg, error.message)
        })
    }
}

const initDB = () => {
    r.connect(dbConnxConfig).then(connection => {
        dbConnx = connection
        r.dbCreate(db).run(connection).then(result => {
            console.log(`\nDB RESULT:\n${JSON.stringify(result,null,4)}`)
            console.log("\nRethinkDB database '%s' created", db)
            createTables(tables)
        }).error(error => {
            console.log("\nRethinkDB database '%s' already exists (%s:%s)\n%s", db, error.name, error.msg, error.message)
            createTables(tables)
        })
    }).error(error => {
        console.log('\nError connecting to RethinkDB!\n',error)
    })
}

const rethinkdb = spawn('rethinkdb',[])
rethinkdb.stdout.on('data', data => {
    console.log(`\nRethinkDB output\n${data}`)
    initDB()
})
rethinkdb.stderr.on('data', error => {
    console.error(`\nERROR starting RethinkDB\n${error}`)
})
process.on('exit', (code) => {
    rethinkdb.kill()
})
