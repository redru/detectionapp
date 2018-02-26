detectionapp
============
Project made by two parts:
- backend (kafkaprocessor) in Java handling login events and registering / sending alerts when 4 consecutive logins fail;
- frontend (kafkaclient) in Node.js + React + Socket.io to produce events on kafka server that will be used by kafkaprocessor

NOTE: kafkaprocessor can run alone, it is completely independant from client module

INDEX
-----
1) Prerequisites
2) Structure
3) Configuration
4) Installation

PREREQUISITES
-------------
- Kafka 1.0.0
- Maven 3.x
- JDK 1.8
- Node.js 8.9.x

STRUCTURE
---------
Project is composed by two modules:
- __kafkaprocessor (Java)__: Java project handling streams
- __kafkaclient (Node.js + React + Socket.io)__: client written in Node.js that makes easy putting expected data into kafka server for kafkaprocessor  
  
Modules can be built together executing __mvn clean package__ from the root folder __../detectionapp/__ ; the build is automated so there is no need to install node or npm or any other tool than Maven (plugins will handle locally install Node.js to build the kafkaclient project).  
  
Modules can also be built separately entering the __../detectionapp/<module_root_folder>__ and executing __mvn clean package__

CONFIGURATION
-------------
### kafka server
Create 2 topics with same name as parameter __STREAM_LOGIN_FAILS_SOURCE_TOPIC__ and __STREAM_LOGIN_FAILS_OUTPUT_TOPIC__ . Recomended is NOT to change names, so topics would be:
- login-topic
- login-failure-topic

### kafkaprocessor
Before building the project, the file __../detectionapp/kafkaprocessor/src/main/resources/config.properties__ must be filled with correct parameters:
- __APPLICATION_ID__: application id (default: detection-app)
- __BOOTSTRAP_SERVERS__: kafka bootstrap servers
- __SMTP_HOST__: smtp host from where the alert email will be sent (default: smtp-mail.outlook.com)
- __SMTP_PORT__: smtp port (default: 587)
- __SMTP_USERNAME__: smtp username (usually email)
- __SMTP_PASSWORD__: smtp password
- __SMTP_START_TLS_ENABLE__: tls enable flag (default: true)
- __TARGET_EMAIL__: target email that will receive the alert
- __STREAM_LOGIN_FAILS_SOURCE_TOPIC__: input topic (default: login-topic - preferred NOT to change it) [must match 'stream_login_fails_source_topic']
- __STREAM_LOGIN_FAILS_OUTPUT_TOPIC__: output topic (default: login-failure-topic - preferred NOT to change it) [must match 'stream_login_fails_output_topic']

### kafkaclient
Before building the project, the file __../detectionapp/kafkaclient/src/main/resources/kafka-client/config.js__ must be filled with correct parameters:
- __port__: application port (default: 8080)
- __bootstrap_servers__: kafka bootstrap servers
- __stream_login_fails_source_topic__: input topic (default: login-topic - preferred NOT to change it) [must match 'STREAM_LOGIN_FAILS_SOURCE_TOPIC']
- __stream_login_fails_output_topic__: output topic (default: login-failure-topic - preferred NOT to change it) [must match 'STREAM_LOGIN_FAILS_OUTPUT_TOPIC']

INSTALLATION
------------
### kafka server
Two topics must be created. Preferred names are:
- login-topic
- login-failure-topic

### kafkaprocessor
Run __java -jar ../detectionapp/kafkaprocessor/target/kafka-processor-0.0.1-jar-with-dependencies.jar__

### kafkaclient
Run __node ../detectionapp/kafkaclient/target/kafka-client/server.js__
