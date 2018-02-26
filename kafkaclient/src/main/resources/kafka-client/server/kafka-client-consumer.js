"use strict";
const kafka = require('kafka-node');

class KafkaClientConsumer {

    /**
     *
     * @param {Object} configuration
     * @param {String} configuration.topic
     * @param {Number} configuration.offset
     * @param {Boolean} configuration.fromOffset
     */
    constructor(configuration) {
        this.client = new kafka.KafkaClient({ kafkaHost: _configuration.bootstrap_servers });
        this.consumer = new kafka.Consumer(this.client, [{ topic: configuration.topic, offset: configuration.offset }], { autoCommit: false, fromOffset: configuration.fromOffset });
        this.state = { };
    }

    /**
     *
     * @param {Function} callback
     */
    onMessage(callback) {
        this.consumer.on('message', message => {
            return callback(message, this.state);
        });

        return this;
    }

}

module.exports = KafkaClientConsumer;
