"use strict";
const http = require('http');
const express = require('express');
const bodyParser = require('body-parser');

const app = express();
// parse application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ extended: false }))

// parse application/json
app.use(bodyParser.json())

app.use('/', express.static('./build'));

const server = http.createServer(app);

server.listen(8080, () => {
    console.log('Listening at http://localhost:8080');
});
