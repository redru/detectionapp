"use strict";
global._configuration       = require('./config');
const http                  = require('http');
const path                  = require('path');
const express               = require('express');
const bodyParser            = require('body-parser');
const RouteConfig           = require('./server/route-config');
const KafkaSocket           = require('./server/kafka-socket');
const KafkaManager          = require('./server/kafka-manager');
const KafkaClientConsumer   = require('./server/kafka-client-consumer');
const KafkaClientProducer   = require('./server/kafka-client-producer');

// Consumer login-topic
KafkaManager.addClient('loginConsumer', new KafkaClientConsumer({
    topic       : _configuration.stream_login_fails_source_topic,
    offset      : 0,
    fromOffset  : true
}).onMessage((message, state)=> {
    state.loginTopicRows.push(JSON.parse(message.value));
    message = JSON.parse(message.value);

    KafkaSocket.sockets.forEach(socket => socket.emit('new-login', message));
}));

KafkaManager.getClient('loginConsumer').state.loginTopicRows = [];

// Consumer login-failure-topic
KafkaManager.addClient('loginFailureConsumer', new KafkaClientConsumer({
    topic       : _configuration.stream_login_fails_output_topic,
    offset      : 0,
    fromOffset  : false
}).onMessage(message => {
    message = JSON.parse(message.value);
    KafkaSocket.sockets.forEach(socket => socket.emit('login-failure', message));
}));

// Producer
KafkaManager.addClient('loginTopicProducer', new KafkaClientProducer());

const app = express();
// parse application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ extended: false }));
// parse application/json
app.use(bodyParser.json());

// Static pages
app.use('/', express.static(path.join(__dirname, '/build')));

// Configure application routes
RouteConfig.configure(app);

// Server + sockets configuration
const server = http.createServer(app);
KafkaSocket.setServer(server);

// SERVER STARTUP
server.listen(8080, () => {
    console.log('Listening at http://localhost:8080');
});
