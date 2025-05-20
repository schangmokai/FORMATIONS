## Installation Nexus

```
###############################################################################
#########################  NEXUS DEV      #####################################
###############################################################################
```

## Nexus pour un environnement de prod

docker-compose.yml

```
services:
  nexus:
    image: sonatype/nexus3:latest
    container_name: nexus
    ports:
      - "8081:8081"
      - "8082:8082"
      - "8099:8083"
    networks:
      - devops_network_lab
      - devops_network_app
    volumes:
      - nexus-data:/nexus-data
    restart: unless-stopped
    environment:
      INSTALL4J_ADD_VM_PARAMS: '-Xms1200m -Xmx1200m -XX:MaxDirectMemorySize=2g -XX:MaxMetaspaceSize=512m'
networks:
  devops_network_lab:
    driver: bridge
  devops_network_app:
    driver: bridge
volumes:
  nexus-data:
    driver: local
```

```
docker compose up -d
```

### pour avoir le password admin

```
docker exec nexus cat /nexus-data/admin.password
```
## default password

```
764fc5c0-2e4f-40be-b885-1fb3982b258c
```

### access url 

http://92.242.187.138:8081/

## change password
```
username: 
password: 
```

```
###############################################################################
#########################  NEXUS PROD      ####################################
###############################################################################
```



## Nexus pour un environnement de prod

### 📁 Arborescence

```
nexus-reverse-proxy/
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
    server_name nexus.local;

    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;

    ssl_protocols TLSv1.2 TLSv1.3;

    location / {
      proxy_pass http://nexus:8081;
      proxy_set_header Host $host;
      proxy_set_header X-Real-IP $remote_addr;
      proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
      proxy_set_header X-Forwarded-Proto https;
    }
  }
}

```

### 3. 🐳 Fichier docker-compose.yml

docker-compose.yml

```
services:
  nexus:
    image: sonatype/nexus3:latest
    container_name: nexus
    ports:
      - "8081:8081"   # HTTP direct (optionnel)
      - "8082:8082"
      - "8099:8083"
    networks:
      - devops_network_lab
      - devops_network_app
    volumes:
      - nexus-data:/nexus-data
    restart: unless-stopped
    environment:
      INSTALL4J_ADD_VM_PARAMS: >
        -Xms1200m -Xmx1200m
        -XX:MaxDirectMemorySize=2g
        -XX:MaxMetaspaceSize=512m
        -Djava.util.prefs.userRoot=/nexus-data/javaprefs
  nginx:
    image: nginx:alpine
    container_name: nginx-nexus-proxy
    ports:
      - "8443:443"  # HTTPS exposé sur port 8443
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/ssl:/etc/nginx/ssl:ro
    depends_on:
      - nexus
    networks:
      - devops_network_lab  # Pour atteindre Nexus

volumes:
  nexus-data:
    driver: local

networks:
  devops_network_lab:
    driver: bridge
  devops_network_app:
    driver: bridge
```

### ▶️ Étape 5 : Lancer Nexus

```
docker compose up -d
```

```
curl -k https://10.145.40.242:8443
https://10.145.40.242:8443
```

### pour avoir le password admin

```
docker exec nexus cat /nexus-data/admin.password
```
## default password

```
764fc5c0-2e4f-40be-b885-1fb3982b258c
```

### access url

https://10.145.40.242:8443

## change password
```
username: 
password: 
```

# NEXUS UP AND TABLE