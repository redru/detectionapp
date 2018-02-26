"use strict";
const KafkaManager = require('./kafka-manager');

class RouteConfig {

    constructor() { }

    /**
     *
     * @param {Object} app
     */
    configure(app) {
        this.app = app;

        /**
         * This route retrieves the full list of login-topic
         */
        app.get('/topics/login', (req, res) => {
            return res.json({ rows: KafkaManager.getClient('loginConsumer').state.loginTopicRows });
        });

        /**
         * This route adds a new login to the login-topic
         */
        app.post('/login', (req, res) => {
            if (!req.body.user) {
                return res.status(403).json({ errorCode: 'MISSING_MANDATORY_PARAMETERS', errorMessage: 'User is missing.' });
            }

            const now = new Date();
            const message = {
                logTime: `${now.getHours()}:${now.getMinutes()}:${now.getSeconds()}`,
                userID  : req.body.user,
                IP      : req.connection.remoteAddress,
                status  : req.body.status
            };

            KafkaManager.getClient('loginTopicProducer').send({
                topic   : 'login-topic',
                message : message
            });

            res.status(200).json(message);
        });
    }

}

module.exports = new RouteConfig();
