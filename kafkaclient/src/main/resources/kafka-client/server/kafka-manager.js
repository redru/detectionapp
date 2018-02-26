"use strict";
class KafkaManager {

    constructor() {
        this._mapping = { };
    }

    addClient(name, client) {
        this._mapping[name] = client;
    }

    getClient(name) {
        return this._mapping[name];
    }

}

module.exports = new KafkaManager();
