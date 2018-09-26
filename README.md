# Spring for Apache Kafka (demo project)

This a demo project which goal is to show some basics of Spring for Apache Kafka.

## Project overview

In the scope of the project we'll try to solve a typical problem of a fast producer and a slow consumer.\
This is a kind of a problem when a task or event consumer can't handle incoming load.

## Applications

### Producer application

On the regular basis, producer application:
* Creates a new message (every message is a string view of current time)
* Logs a message
* Puts a message into `demo` topic
* Awaits for a time specified in producer properties

#### Configuration

| Property                         | Description                                  | Default Value          |
|----------------------------------|----------------------------------------------|------------------------|
| server.port                      | Spring embedded server port                  | 8080                   |
| kafka.producer.bootstrap.servers | Kafka producer config: `bootstrap.servers`   | localhost:9092         |
| kafka.producer.metadataMaxAgeMs  | Kafka producer config: `metadata.max.age.ms` | 15000                  |
| kafka.producer.topic             | Topic to produce messages                    | demo                   |
| kafka.producer.delay             | Delay between sending new messages           | 1000                   |
| logging.file                     | Log file                                     | application-${PID}.log |

### Consumer application

On the regular basis, consumer application:
* Receives every new message from `demo` topic
* Awaits for a time specified in consumer properties (to emulate slow processing)

#### Configuration

| Property                         | Description                                   | Default Value          |
|----------------------------------|-----------------------------------------------|------------------------|
| server.port                      | Spring embedded server port                   | 8081                   |
| kafka.consumer.bootstrap.servers | Kafka consumer config: `bootstrap.servers`    | localhost:9092         |
| kafka.consumer.group.id          | Kafka consumer config: `group.id`             | group1                 |
| kafka.consumer.autoOffsetReset   | Kafka consumer config: `auto.offset.reset`    | earliest               |
| kafka.consumer.maxPollRecords    | Kafka consumer config: `max.poll.records`     | 5                      |
| kafka.consumer.maxPollIntervalMs | Kafka consumer config: `max.poll.interval.ms` | 15000                  |
| kafka.consumer.metadataMaxAgeMs  | Kafka consumer config: `metadata.max.age.ms`  | 15000                  |
| kafka.consumer.concurrency       | Spring Kafka config: `concurrency`            | 1                      |
| kafka.consumer.topic             | Topic to consume messages                     | demo                   |
| kafka.consumer.delay             | Artificial delay between consuming messages   | 2000                   |
| logging.file                     | Log file                                      | application-${PID}.log |

## Problem

Producer sends one message per second by default. Consumer processes one message per 2 seconds by default.\
Therefore, there will always be a gap between producer and consumer in terms of messages.

## Solution

Run more consumers!

Kafka provides an easy way to distribute topic messages among multiple consumers. That is to transfer every single message to only one consumer. To make that possible, one should satisfy the following requirements:
* Consumers must build a group. To build a group, all consumers must have the same arbitrary `group.id` value specified in consumer properties. When not specified, every single consumer receives all messages.
* A number of partitions in a topic must be equal or more than a number of consumers. Otherwise, some consumers will stay idle.

## Demo scenario

### Part I

* Install Kafka (see a hint from below)
* Create `demo` topic with one partition (see a hint from below)
* Run producer application
* Run consumer application
* Watch producer and consumer logs and find that there is a lag between producing and consuming messages 

### Part II

* Change consumer `server.port` value to 8082 (in order not to overlap ports with already running application)
* Run one more consumer application
* Watch consumer logs and find that only one consumer application is receiving messages, and there is still the lag

### Part III

* Increase amount of partitions in `demo` topic up to 2.
* Wait `metadata.max.age.ms`
* Watch producer logs and find that producer application sends messages into 2 partitions
* Watch consumer logs and find that both consumer applications receive messages, each from different partition
* Find that lag doesn't increase anymore

### Part IV

* Increase amount of partitions in `demo` topic up to 3.
* Wait `metadata.max.age.ms`
* Watch producer logs and find that producer application sends messages into 3 partitions
* Watch consumer logs and find that all consumer applications receive messages, each from different partition
* Find that lag decreases and scaled consumers can handle the load 

## Prerequisites

* Download Apache Kafka 2.0 built with Scala 2.11 from [here](https://kafka.apache.org/downloads).
* Untar binaries:\
`tar -xvzf kafka_2.11-2.0.0.tgz`
* Run Zookeeper server with default configuration:\
`./bin/zookeeper-server-start.sh config/zookeeper.properties`
* Run Kafka server with default configuration:\
`./bin/kafka-server-start.sh config/server.properties`

## Cheat sheet for Kafka command line tools

* Create `demo` topic with one partition:\
`./bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic demo --partitions 1 --replication-factor 1`
* Delete topic:\
`./bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic demo`
* List topics:\
`./bin/kafka-topics.sh --zookeeper localhost:2181 --list`
* Produce records to Kafka topic:\
`./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic demo`
* Consume records from Kafka topic from very beginning:\
`./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic demo --from-beginning`
* Increase amount of partitions in `demo` topic up to 2:\
`./bin/kafka-topics.sh --zookeeper localhost:2181 --alter --topic demo --partitions 2`
