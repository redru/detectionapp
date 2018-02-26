"use strict";
const kafka = require('kafka-node');

class KafkaClientProducer {

    /**
     *
     */
    constructor() {
        this.producer = new kafka.Producer(new kafka.KafkaClient({ kafkaHost: _configuration.bootstrap_servers }));
    }

    /**
     *
     * @param {Object} configuration
     * @param {String} configuration.topic
     * @param {Object} configuration.message
     */
    send(configuration) {
        this.producer.send([
            {
                topic: configuration.topic,
                attributes: 1,
                messages: JSON.stringify(configuration.message)
            }
        ], err => {
            if (err) {
                console.error(err);
            }
        });
    }

}

module.exports = KafkaClientProducer;
