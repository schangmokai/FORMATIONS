## Demarrage de Kafka pour la demo

```
wget http://miroir.univ-lorraine.fr/apache/kafka/2.3.0/kafka_2.12-2.3.0.tgz
tar -xvf kafka_2.12-2.3.0.tgz
```

1. avant de demarrer le broker kafka, on demarre d'abbord zookeeper

```
bin/zookeeper-server-start.sh config/zookeeper.properties
```

2. Une fois zookeeper demarrer on demarre kafka et pour le demarrer

```
bin/kafka-server-start.sh config/server.properties
```

3. dans les configs de Kafka lorqu'il demarre les serveurzookeeper par defaut est dans le localhost.
mais il peut être changé

```
zookeeper.connect=localhost:2181
```

3. Démarrer kafka connect en mode distribué

```
bin/connect-distributed.sh config/connect-distributed.properties
```
4. Creation d'un topics

```
bin/kafka-topics.sh --create --topic test-dojo \
--partitions 3 --replication-factor 1 --bootstrap-server localhost:9092
```

5. Lister les topics
```
bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
```
6. pour décrire un topic

```
bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic test-dojo
```
6. voir le contenu d'une partition 

```
bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic test-dojo \
  --partition 2 \
  --from-beginning
```
7. Consommer avec consumer group
```
bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic test-topic \
  --group group1 \
  --from-beginning
```

6. Démarrer un consumer sur un topics
```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --group group1 --topic test-dojo
```
6. Démarrer un producer sur un topics
```
bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic test-dojo
```
4. creation de la base de données et de la table table1 avec insertion de données

```
create database dojodemosource;
use dojodemosource;
CREATE TABLE table1 (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    firstname VARCHAR(100),
    vitesse INT,
    categorie VARCHAR(50),
    score DECIMAL(5,2)
);
```

5. insertion de données.

```
INSERT INTO table1 (id, name, firstname, vitesse, categorie, score) VALUES
(1, 'chaval 1', 'toto', 129, 'elite', 9.75);
INSERT INTO table1 (id, name, firstname, vitesse, categorie, score) VALUES
(2, 'chaval 2', 'Max', 120, 'standard', 8.50);
INSERT INTO table1 (id, name, firstname, vitesse, categorie, score) VALUES
(3, 'chaval 3', 'Léo', 140, 'elite', 9.20);
INSERT INTO table1 (id, name, firstname, vitesse, categorie, score) VALUES
(4, 'chaval 4', 'tata', 110, 'amateur', 7.00);
INSERT INTO table1 (id, name, firstname, vitesse, categorie, score) VALUES
(5, 'chaval 5', 'Anna', 135, 'standard', 8.90);
```


## Exemple de connecteur source

```
create database dojodemosink;
use dojodemosink;
select * from table1;
CREATE TABLE table1 (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    firstname VARCHAR(100),
    vitesse INT,
    categorie VARCHAR(50),
    score DECIMAL(5,2)
);
```

```
 curl -s http://localhost:8083/connectors | jq .
 curl -s http://localhost:8083/connectors/dojo-database-demo-source/status | jq .
 curl -X DELETE http://localhost:8083/connectors/dojo-database-demo-source | jq .
 
 curl -X POST http://localhost:8083/connectors      -H "Content-Type: application/json"      -d '{
     "name": "dojo-database-demo-source",
     "config": {
        "connector.class": "io.debezium.connector.mysql.MySqlConnector",
        "database.hostname": "localhost",
        "database.port": "3306",
        "database.user": "mokai",
        "database.password": "mokai",
        "database.server.id": "1",
        "database.server.name": "mysql-db",
        "database.include.list": "dojodemosource",
        "table.include.list": "dojodemosource.table1",
        "topic.prefix": "sqlout",
        "database.history.kafka.bootstrap.servers": "localhost:9092",
        "schema.history.internal": "io.debezium.storage.file.history.FileSchemaHistory",
        "schema.history.internal.file.filename": "/tmp/schemahistory.dat",
        "database.connectionTimeZone": "Africa/Lagos"
      }
 }'
```

## Exemple de connecteur sink

```
  curl -s http://localhost:8083/connectors | jq .
  curl -s http://localhost:8083/connectors/dojo-database-demo-sink/status | jq .
  curl -X DELETE http://localhost:8083/connectors/dojo-database-demo-sink | jq .

  curl -X POST http://localhost:8083/connectors      -H "Content-Type: application/json"      -d '{
      "name": "dojo-database-demo-sink",
      "config": {
      "connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
      "tasks.max": "1",
      "topics": "sqlout.dojodemosource.table1",
      "connection.url": "jdbc:mysql://localhost:3306/dojodemosink?useSSL=false&serverTimezone=Africa/Lagos",
      "connection.user": "mokai",
      "connection.password": "mokai",
      "table.name.format": "dojodemosink.table1",
      "auto.create": false,
      "auto.evolve": false,
      "insert.mode": "upsert",
      "pk.mode": "record_key",
      "pk.fields": "id",
      "delete.enabled": true,
      "batch.size": "1000",
      "value.converter": "org.apache.kafka.connect.json.JsonConverter",
      "value.converter.schemas.enable": "true",
      "transforms": "ExtractAfter,RemoveDateFields",
      "transforms.ExtractAfter.type": "org.apache.kafka.connect.transforms.ExtractField$Value",
      "transforms.ExtractAfter.field": "after",
      "transforms.RemoveDateFields.type": "org.apache.kafka.connect.transforms.ReplaceField$Value",
      "transforms.RemoveDateFields.blacklist": "created_date,updated_date"

    }
 }'
```

6. Exemple de connecteur pour fichier.


```
curl -s http://localhost:8083/connectors | jq .
curl -s http://localhost:8083/connectors/dojo-file-source-connector/status | jq .
curl -X DELETE http://localhost:8083/connectors/dojo-file-source-connector | jq .
  
curl -X POST http://localhost:8083/connectors  -H "Content-Type: application/json" -d '{
  "name": "dojo-file-source-connector",
  "config": {
    "connector.class": "FileStreamSource",
    "tasks.max": "1",
    "file": "/tmp/source/input.txt",
    "topic": "file-topic-dojo"
  }
}'

```

destination

```
curl -s http://localhost:8083/connectors | jq .
curl -s http://localhost:8083/connectors/dojo-file-sink-connector/status | jq .
curl -X DELETE http://localhost:8083/connectors/dojo-file-sink-connector | jq .

curl -X POST http://localhost:8083/connectors  -H "Content-Type: application/json" -d '{
  "name": "dojo-file-sink-connector",
  "config": {
    "connector.class": "FileStreamSink",
    "tasks.max": "1",
    "topics": "file-topic-dojo",
    "file": "/tmp/sink/output.txt"
  }
}'

```

## Pour démarrer un cluster Kafka avec docker compose

```
services:
  # ====================
  # ZOOKEEPER CLUSTER
  # ====================
  zookeeper1:
    image: confluentinc/cp-zookeeper:7.5.0
    hostname: zookeeper1
    container_name: zookeeper1
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_SERVER_ID: 1
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
      ZOOKEEPER_INIT_LIMIT: 5
      ZOOKEEPER_SYNC_LIMIT: 2
      ZOOKEEPER_SERVERS: zookeeper1:2888:3888;zookeeper2:2888:3888;zookeeper3:2888:3888

  zookeeper2:
    image: confluentinc/cp-zookeeper:7.5.0
    hostname: zookeeper2
    container_name: zookeeper2
    ports:
      - "2182:2181"
    environment:
      ZOOKEEPER_SERVER_ID: 2
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
      ZOOKEEPER_INIT_LIMIT: 5
      ZOOKEEPER_SYNC_LIMIT: 2
      ZOOKEEPER_SERVERS: zookeeper1:2888:3888;zookeeper2:2888:3888;zookeeper3:2888:3888

  zookeeper3:
    image: confluentinc/cp-zookeeper:7.5.0
    hostname: zookeeper3
    container_name: zookeeper3
    ports:
      - "2183:2181"
    environment:
      ZOOKEEPER_SERVER_ID: 3
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
      ZOOKEEPER_INIT_LIMIT: 5
      ZOOKEEPER_SYNC_LIMIT: 2
      ZOOKEEPER_SERVERS: zookeeper1:2888:3888;zookeeper2:2888:3888;zookeeper3:2888:3888

  # ====================
  # KAFKA CLUSTER
  # ====================
  kafka1:
    image: confluentinc/cp-kafka:7.5.0
    hostname: kafka1
    container_name: kafka1
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: "zookeeper1:2181,zookeeper2:2181,zookeeper3:2181"
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka1:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3
    depends_on:
      - zookeeper1
      - zookeeper2
      - zookeeper3

  kafka2:
    image: confluentinc/cp-kafka:7.5.0
    hostname: kafka2
    container_name: kafka2
    ports:
      - "9093:9092"
    environment:
      KAFKA_BROKER_ID: 2
      KAFKA_ZOOKEEPER_CONNECT: "zookeeper1:2181,zookeeper2:2181,zookeeper3:2181"
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka2:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3
    depends_on:
      - zookeeper1
      - zookeeper2
      - zookeeper3

  kafka3:
    image: confluentinc/cp-kafka:7.5.0
    hostname: kafka3
    container_name: kafka3
    ports:
      - "9094:9092"
    environment:
      KAFKA_BROKER_ID: 3
      KAFKA_ZOOKEEPER_CONNECT: "zookeeper1:2181,zookeeper2:2181,zookeeper3:2181"
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka3:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3
    depends_on:
      - zookeeper1
      - zookeeper2
      - zookeeper3
```

8. Pour le lancer

```
docker compose up -d
```

1. Vérifier que les 3 brokers sont en cluster :

```
docker exec -it kafka1 kafka-topics --bootstrap-server kafka1:9092 --list
docker exec -it kafka2 kafka-topics --bootstrap-server kafka2:9092 --list
docker exec -it kafka3 kafka-topics --bootstrap-server kafka3:9092 --list
```

2. Créer un topic avec réplication sur 3 brokers :

```
docker exec -it kafka1 kafka-topics --create --topic test-topic \
  --partitions 3 --replication-factor 3 --bootstrap-server kafka1:9092
```

3. Vérifier le topic etant sur n'importe quel broker

```
docker exec -it kafka1 kafka-topics --bootstrap-server kafka1:9092 --list
docker exec -it kafka2 kafka-topics --bootstrap-server kafka2:9092 --list
docker exec -it kafka3 kafka-topics --bootstrap-server kafka3:9092 --list
```


## Pour démarrer un cluster Kafka  connect avec docker

```
services:
  # ====================
  # ZOOKEEPER CLUSTER
  # ====================
  zookeeper1:
    image: confluentinc/cp-zookeeper:7.5.0
    hostname: zookeeper1
    container_name: zookeeper1
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_SERVER_ID: 1
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
      ZOOKEEPER_INIT_LIMIT: 5
      ZOOKEEPER_SYNC_LIMIT: 2
      ZOOKEEPER_SERVERS: zookeeper1:2888:3888;zookeeper2:2888:3888;zookeeper3:2888:3888

  zookeeper2:
    image: confluentinc/cp-zookeeper:7.5.0
    hostname: zookeeper2
    container_name: zookeeper2
    ports:
      - "2182:2181"
    environment:
      ZOOKEEPER_SERVER_ID: 2
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
      ZOOKEEPER_INIT_LIMIT: 5
      ZOOKEEPER_SYNC_LIMIT: 2
      ZOOKEEPER_SERVERS: zookeeper1:2888:3888;zookeeper2:2888:3888;zookeeper3:2888:3888

  zookeeper3:
    image: confluentinc/cp-zookeeper:7.5.0
    hostname: zookeeper3
    container_name: zookeeper3
    ports:
      - "2183:2181"
    environment:
      ZOOKEEPER_SERVER_ID: 3
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
      ZOOKEEPER_INIT_LIMIT: 5
      ZOOKEEPER_SYNC_LIMIT: 2
      ZOOKEEPER_SERVERS: zookeeper1:2888:3888;zookeeper2:2888:3888;zookeeper3:2888:3888

  # ====================
  # KAFKA CLUSTER
  # ====================
  kafka1:
    image: confluentinc/cp-kafka:7.5.0
    hostname: kafka1
    container_name: kafka1
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: "zookeeper1:2181,zookeeper2:2181,zookeeper3:2181"
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka1:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3
    depends_on:
      - zookeeper1
      - zookeeper2
      - zookeeper3

  kafka2:
    image: confluentinc/cp-kafka:7.5.0
    hostname: kafka2
    container_name: kafka2
    ports:
      - "9093:9092"
    environment:
      KAFKA_BROKER_ID: 2
      KAFKA_ZOOKEEPER_CONNECT: "zookeeper1:2181,zookeeper2:2181,zookeeper3:2181"
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka2:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3
    depends_on:
      - zookeeper1
      - zookeeper2
      - zookeeper3

  kafka3:
    image: confluentinc/cp-kafka:7.5.0
    hostname: kafka3
    container_name: kafka3
    ports:
      - "9094:9092"
    environment:
      KAFKA_BROKER_ID: 3
      KAFKA_ZOOKEEPER_CONNECT: "zookeeper1:2181,zookeeper2:2181,zookeeper3:2181"
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka3:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3
    depends_on:
      - zookeeper1
      - zookeeper2
      - zookeeper3

  # ====================
  # KAFKA CONNECT
  # cp-kafka-connect:7.5.0: utilise java 11
  # cp-kafka-connect:7.9.0: utilise java 17
  # cp-kafka-connect:8.0.0: utilise java 21
  # ====================
  connect:
    image: confluentinc/cp-kafka-connect:7.9.0
    hostname: connect
    container_name: connect
    ports:
      - "8083:8083"
    environment:
      CONNECT_BOOTSTRAP_SERVERS: "kafka1:9092,kafka2:9092,kafka3:9092"
      CONNECT_REST_PORT: 8083
      CONNECT_REST_ADVERTISED_HOST_NAME: "connect"
      CONNECT_GROUP_ID: "connect-cluster"
      CONNECT_CONFIG_STORAGE_TOPIC: "connect-configs"
      CONNECT_OFFSET_STORAGE_TOPIC: "connect-offsets"
      CONNECT_STATUS_STORAGE_TOPIC: "connect-status"
      CONNECT_KEY_CONVERTER: "org.apache.kafka.connect.json.JsonConverter"
      CONNECT_VALUE_CONVERTER: "org.apache.kafka.connect.json.JsonConverter"
      CONNECT_INTERNAL_KEY_CONVERTER: "org.apache.kafka.connect.json.JsonConverter"
      CONNECT_INTERNAL_VALUE_CONVERTER: "org.apache.kafka.connect.json.JsonConverter"
      CONNECT_CONFIG_STORAGE_REPLICATION_FACTOR: 3
      CONNECT_OFFSET_STORAGE_REPLICATION_FACTOR: 3
      CONNECT_STATUS_STORAGE_REPLICATION_FACTOR: 3
      CONNECT_PLUGIN_PATH: "/usr/share/java,/etc/kafka-connect/jars"
    volumes:
      - ./plugins:/etc/kafka-connect/jars
    depends_on:
      - kafka1
      - kafka2
      - kafka3

  # ====================
  # AKHQ UI (Kafka UI)
  # ====================
  akhq:
    image: tchiotludo/akhq:0.24.0
    container_name: akhq
    ports:
      - "8080:8080"
    environment:
      AKHQ_CONFIGURATION: |
        akhq:
          connections:
            kafka-cluster:
              properties:
                bootstrap.servers: "kafka1:9092,kafka2:9092,kafka3:9092"
    depends_on:
      - kafka1
      - kafka2
      - kafka3
```

### Pour lancer

```
docker compose up -d
docker-compose up -d

```

### Sécurisation du cluster
Kafka IO
kcat -b ip:port -L

lien: https://www.kafkio.com/download?utm_source=chatgpt.com

```
KafkIO-linux-2.1.2-x64.tar.gz
cd KafkIO-linux-2.1.2-x64/bin
./KafkaIo
```
bonjour rtp