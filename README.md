detectionapp
============

index
-----
1) Prerequisites
2) Structure
3) Configuration
4) Installation

Prerequisites
-------------
- Maven 3.x
- JDK 1.8
- Node.js 8.9.x

Project Structure
-----------------
Project is composed by two modules:
- __kafkaprocessor (Java)__: Java project handling streams
- __kafkaclient (Node.js + React + Socket.io)__: client written in Node.js that makes easy enter expected data for kafkaprocessor
