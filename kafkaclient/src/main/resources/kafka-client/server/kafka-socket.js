"use strict";

/**
 * This class is mainly used to keep track of all connected users.
 */
class KafkaSocket {

    constructor() {
        this.sockets = [];
    }

    /**
     * Configures the global socket
     * @param {Object} server
     */
    setServer(server) {
        this.io = require('socket.io')(server);

        this.io.on('connection', socket => {
            this.sockets.push(socket);
        });
    }

}

module.exports = new KafkaSocket();
