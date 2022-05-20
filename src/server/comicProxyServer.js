const express = require('express')
const cors = require('cors')
const comicRouter = require('./routers/getComic')
const app = express()

app.use(cors())
app.use(comicRouter)

app.listen('5000', () => {
    console.log('服务器启动在5000')
})