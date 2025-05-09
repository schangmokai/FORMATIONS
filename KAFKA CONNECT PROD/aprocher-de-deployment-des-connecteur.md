
## 📁 Structure du projet (exemple de repo Git)

```
kafka-connect-deployer/
├── connectors/
│   ├── user-sync.json
│   ├── order-sync.json
├── deploy.sh
├── Dockerfile
└── job-kafka-connect.yaml
```

## 🔧 deploy.sh (mis à jour pour être autonome dans le conteneur)

```
  #!/bin/bash
  
  set -e
  
  echo "📦 Lancement du déploiement des connecteurs Kafka Connect"
  
  KAFKA_CONNECT_URL="${KAFKA_CONNECT_URL:-http://kafka-connect:8083}"
  
  for file in ./connectors/*.json; do
  connector_name=$(basename "$file" .json)

echo "🔍 Traitement du connecteur: $connector_name"

  status=$(curl -s -o /dev/null -w "%{http_code}" "$KAFKA_CONNECT_URL/connectors/$connector_name")

  if [ "$status" -eq 200 ]; then
  echo "🔁 Mise à jour de la config du connecteur $connector_name"
  curl -s -X PUT "$KAFKA_CONNECT_URL/connectors/$connector_name/config" \
-H "Content-Type: application/json" \
  -d @"$file"
  else
  echo "🆕 Création du connecteur $connector_name"
  curl -s -X POST "$KAFKA_CONNECT_URL/connectors" \
-H "Content-Type: application/json" \
  -d @"$file"
  fi

  echo ""
  done
  
  echo "✅ Tous les connecteurs ont été traités."

```

 ## 🐳 Dockerfile

```
FROM curlimages/curl:latest

WORKDIR /app

# Copier le script et les connecteurs
COPY deploy.sh .
COPY connectors/ ./connectors/

RUN chmod +x deploy.sh

ENTRYPOINT ["/app/deploy.sh"]

```

## ☸️ job-kafka-connect.yaml

```
apiVersion: batch/v1
kind: Job
metadata:
  name: kafka-connect-deploy
spec:
  template:
    spec:
      containers:
        - name: deployer
          image: ton-registre/kafka-connect-deployer:latest
          env:
            - name: KAFKA_CONNECT_URL
              value: "http://kafka-connect.default.svc.cluster.local:8083"
      restartPolicy: Never
  backoffLimit: 1

```

## 📁 Structure du chart

```
kafka-connect-deployer/
├── Chart.yaml
├── values.yaml
├── templates/
│   ├── job.yaml
│   └── configmap-connectors.yaml (optionnel)
```

## 📄 Chart.yaml

```
apiVersion: v2
name: kafka-connect-deployer
description: Helm chart to deploy Kafka Connect connectors
version: 0.1.0

```

## 🧾 values.yaml

```
image:
  repository: ton-registre/kafka-connect-deployer
  tag: latest
  pullPolicy: IfNotPresent

kafkaConnectUrl: "http://kafka-connect.default.svc.cluster.local:8083"

job:
  name: kafka-connect-deploy
  backoffLimit: 1

```

## 📦 templates/job.yaml

```
apiVersion: batch/v1
kind: Job
metadata:
  name: {{ .Values.job.name }}
spec:
  backoffLimit: {{ .Values.job.backoffLimit }}
  template:
    spec:
      containers:
        - name: kafka-connect-deployer
          image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
          imagePullPolicy: {{ .Values.image.pullPolicy }}
          env:
            - name: KAFKA_CONNECT_URL
              value: "{{ .Values.kafkaConnectUrl }}"
      restartPolicy: Never

```
