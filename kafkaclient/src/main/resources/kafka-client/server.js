"use strict";
const http = require('http');
const path = require('path');
const kafka = require('kafka-node');
const express = require('express');
const bodyParser = require('body-parser');

const client = new kafka.KafkaClient({ kafkaHost: 'DESKTOP-SBIL9SV:9092' });
const client2 = new kafka.KafkaClient({ kafkaHost: 'DESKTOP-SBIL9SV:9092' });

// Consumer login-topic
const consumer = new kafka.Consumer(client, [{ topic: 'login-topic', offset: 0 }], { autoCommit: false, fromOffset: true });

const loginTopicRows = [];

consumer.on('message', message => {
    loginTopicRows.push(JSON.parse(message.value));
    message = JSON.parse(message.value);

    sockets.forEach(socket => socket.emit('new-login', message));
});

// Consumer login-failure-topic
const consumer2 = new kafka.Consumer(client2, [{ topic: 'login-failure-topic' }], { autoCommit: false, fromOffset: false });

consumer2.on('message', message => {
    message = JSON.parse(message.value);

    sockets.forEach(socket => socket.emit('login-failure', message));
});

// Producer
const loginTopicProducer = new kafka.Producer(client);

const app = express();
// parse application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ extended: false }));
// parse application/json
app.use(bodyParser.json());

app.use('/', express.static(path.join(__dirname, '/build')));

app.post('/login', (req, res) => {
    const now = new Date();

    loginTopicProducer.send([
        {
            topic: 'login-topic',
            attributes: 1,
            messages: JSON.stringify({
                logTime: `${now.getHours()}:${now.getMinutes()}:${now.getSeconds()}`,
                userID: req.body.user,
                IP: '127.0.0.1',
                status: req.body.status
            })
        }
    ], (err, data) => {
        if (err) {
            console.error(err);
        }
    });

    res.status(200).json({ });
});

app.get('/topics/login', (req, res) => {
    res.json({
        rows: loginTopicRows
    });
});

// SOCKETS CONFIGURATION
const server = http.createServer(app);

const io = require('socket.io')(server);
const sockets = [];

io.on('connection', socket => {
    sockets.push(socket);
});

// SERVER STARTUP
server.listen(8080, () => {
    console.log('Listening at http://localhost:8080');
});
