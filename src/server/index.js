const { exec } = require('child_process')

console.log('\nwhitesocket\n')

exec('python ./src/server/index.py', (error, stdout, stderr) => {
    if(error) {
        console.log(error)
        return
    }
    console.log(stdout)
})
