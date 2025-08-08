## Installation SonarQube

```
###############################################################################
#########################  SONARQUBE DEV      #################################
###############################################################################
```

## SonarQube pour un environnement de dev

docker-compose.yml

```

services:
  sonarqube:
    image: sonarqube:community
    container_name: sonarqube
    depends_on:
      - db
    ports:
      - "9000:9000"
    environment:
      SONAR_JDBC_URL: jdbc:postgresql://db:5432/sonarqube
      SONAR_JDBC_USERNAME: sonar
      SONAR_JDBC_PASSWORD: sonar
      # Désactive les vérifications strictes d'Elasticsearch (utile en dev)
      SONAR_ES_BOOTSTRAP_CHECKS_DISABLE: "true"
      # Ajuste la mémoire Java pour éviter les erreurs ES
      JAVA_OPTS: "-Xms512m -Xmx2g"
    volumes:
      - sonarqube_data:/opt/sonarqube/data
      - sonarqube_extensions:/opt/sonarqube/extensions
      - sonarqube_logs:/opt/sonarqube/logs
    networks:
      - devops_network_lab
      - devops_network_app

  db:
    image: postgres:13
    container_name: sonardb
    environment:
      POSTGRES_USER: sonar
      POSTGRES_PASSWORD: sonar
      POSTGRES_DB: sonarqube
    volumes:
      - postgresql:/var/lib/postgresql/data
    networks:
      - devops_network_lab

volumes:
  sonarqube_data:
  sonarqube_extensions:
  sonarqube_logs:
  postgresql:

networks:
  devops_network_lab:
    driver: bridge
  devops_network_app:
    driver: bridge

```

```
docker compose up -d
```

### username et password par défaut admin

http://localhost:9000


![img_8.png](img_8.png)

### pour desinstaller

```
docker-compose down -v
```

### Pour analyser un projet spring-boot en local étant à la racine du projet

```
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=crm-service-authentification \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=sqa_ac274e0abe2f276d3bf02e49c1e71e65f062414c
```


```
###############################################################################
#########################  SONARQUBE PROD      ################################
###############################################################################
```

## Sonarqube pour un environnement de prod

### 📁 Arborescence

```
sonarqube-reverse-proxy/
├── docker-compose.yml
├── nginx/
│   ├── nginx.conf
│   ├── ssl/
│   │   ├── cert.pem
│   │   └── key.pem
```

### 1. 🔐 Génère ou place ton certificat SSL

```
mkdir -p nginx/ssl
openssl req -x509 -nodes -days 89365 -newkey rsa:2048 \
  -keyout nginx/ssl/key.pem \
  -out nginx/ssl/cert.pem \
  -subj "/CN=10.145.40.242"
```

### 2. ⚙️ Fichier nginx.conf

```
events {}

http {
  server {
    listen 443 ssl;
    server_name sonarqube.local;

    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;

    ssl_protocols TLSv1.2 TLSv1.3;

    location / {
      proxy_pass http://sonarqube:9000;
      proxy_set_header Host $host;
      proxy_set_header X-Real-IP $remote_addr;
      proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
      proxy_set_header X-Forwarded-Proto https;
      proxy_set_header X-Forwarded-Host $host;
      proxy_set_header X-Forwarded-Port $server_port;
    }
  }
}

```

### 3. 🐳 Fichier docker-compose.yml



docker-compose.yml

```

services:
  sonarqube:
    image: sonarqube:community
    container_name: sonarqube
    depends_on:
      - db
    ports:
      - "9000:9000"
    environment:
      SONAR_JDBC_URL: jdbc:postgresql://db:5432/sonarqube
      SONAR_JDBC_USERNAME: sonar
      SONAR_JDBC_PASSWORD: sonar
      # Désactive les vérifications strictes d'Elasticsearch (utile en dev)
      SONAR_ES_BOOTSTRAP_CHECKS_DISABLE: "true"
      # Ajuste la mémoire Java pour éviter les erreurs ES
      JAVA_OPTS: "-Xms512m -Xmx2g"
      SONAR_WEB__BASEURL: "https://sonarqube.local:5443"
    volumes:
      - sonarqube_data:/opt/sonarqube/data
      - sonarqube_extensions:/opt/sonarqube/extensions
      - sonarqube_logs:/opt/sonarqube/logs
    networks:
      - devops_network_lab
      - devops_network_app
      
  db:
    image: postgres:13
    container_name: sonardb
    environment:
      POSTGRES_USER: sonar
      POSTGRES_PASSWORD: sonar
      POSTGRES_DB: sonarqube
    volumes:
      - postgresql:/var/lib/postgresql/data
    networks:
      - devops_network_lab
      
  nginx:
    image: nginx:alpine
    container_name: nginx-sonarqube-proxy
    ports:
      - "5443:443"  # HTTPS exposé sur port 5443
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/ssl:/etc/nginx/ssl:ro
    depends_on:
      - sonarqube
    networks:
      - devops_network_lab  # Pour atteindre sonarqube

volumes:
  sonarqube_data:
  sonarqube_extensions:
  sonarqube_logs:
  postgresql:

networks:
  devops_network_lab:
    driver: bridge
  devops_network_app:
    driver: bridge
```

```
docker compose up -d
```