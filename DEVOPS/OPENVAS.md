## Installation openVas


```
###############################################################################
#########################  OPENVAS DEV      ###################################
###############################################################################
```

Create network

```
docker network create --driver bridge devops_network_app
docker network create --driver bridge devops_network_lab
```

docker-compose.yml

```
services:
  openvas:
    image: immauss/openvas
    container_name: openvas
    ports:
      - "9392:9392"   # Interface Web GVM
      - "2222:22"     # SSH pour gvm-cli
      - "9390:9390"    # GMP via TLS
    volumes:
      - openvas-data:/data
    environment:
      - PASSWORD=adminpassword
    networks:
      - devops_network_app
      - devops_network_lab
    restart: unless-stopped

volumes:
  openvas-data:

networks:
  devops_network_app:
    driver: bridge
  devops_network_lab:
    driver: bridge

```

```
docker compose up -d
```


### pour desinstaller

```
docker compose down -v
```

### pour acceder 

http://localhost:9392


```
###############################################################################
#########################  OPENVAS PROD      ##################################
###############################################################################
```

## Sonarqube pour un environnement de prod

### 📁 Arborescence

```
openvas-reverse-proxy/
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
    server_name openvas.local;

    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;

    ssl_protocols TLSv1.2 TLSv1.3;

    location / {
      proxy_pass http://openvas:9392;
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
  openvas:
    image: immauss/openvas
    container_name: openvas
    ports:
      - "9392:9392"   # Interface Web GVM
      - "2222:22"     # SSH pour gvm-cli
      - "9390:9390"    # GMP via TLS
    volumes:
      - openvas-data:/data
    environment:
      - PASSWORD=adminpassword
    networks:
      - devops_network_app
      - devops_network_lab
    restart: unless-stopped
  nginx:
    image: nginx:alpine
    container_name: nginx-openvas-proxy
    ports:
      - "3443:443"  # HTTPS exposé sur port 3443
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/ssl:/etc/nginx/ssl:ro
    depends_on:
      - openvas
    networks:
      - devops_network_lab  # Pour atteindre openvas

volumes:
  openvas-data:

networks:
  devops_network_app:
    driver: bridge
  devops_network_lab:
    driver: bridge

```

```
docker compose up -d
```

### pour desinstaller

```
docker compose down -v
```

### trouver un moyen de créer un utilisateur ssh

```
# Entrer dans le conteneur
docker exec -it openvas bash

# Installer OpenSSH
apt update && apt install -y openssh-server

# Ajouter l'utilisateur mokai
useradd -m -s /bin/bash mokai
echo 'mokai:mokai' | chpasswd

# Activer l’authentification par mot de passe (et désactiver la ligne no)
sed -i 's/^#\?PasswordAuthentication .*/PasswordAuthentication yes/' /etc/ssh/sshd_config

# Créer le dossier nécessaire au démarrage de SSHD
mkdir -p /var/run/sshd

# Lancer le serveur SSH
/usr/sbin/sshd

```
### pour installer le ssh_user automatiquement

## 🗂 Structure de ton projet

```
.
├── docker-compose.yml
├── openvas
│   └── Dockerfile
├── nginx
│   ├── nginx.conf
│   └── ssl
│       ├── cert.pem
│       └── key.pem

```

## 1- creation du Dockerfile

Dockerfile

```
FROM immauss/openvas

# Installer openssh-server et libxml2-utils
RUN apt update && \
    apt install -y openssh-server libxml2-utils && \
    mkdir -p /var/run/sshd

# Créer utilisateur mokai avec mot de passe mokai
RUN useradd -m -s /bin/bash mokai && \
    echo 'mokai:mokai' | chpasswd

# Forcer PasswordAuthentication
RUN grep -q '^PasswordAuthentication yes' /etc/ssh/sshd_config || \
    echo 'PasswordAuthentication yes' >> /etc/ssh/sshd_config

# Exposer le port SSH (optionnel, déjà fait par docker-compose)
EXPOSE 22
```

### 3. 🐳 Mise à jours du fichier docker-compose.yml

docker-compose.yml

```
services:
  openvas:
    image: ./openvas
    container_name: openvas
    ports:
      - "9392:9392"   # Interface Web GVM
      - "2222:22"     # SSH pour gvm-cli
      - "9390:9390"    # GMP via TLS
    volumes:
      - openvas-data:/data
    environment:
      - PASSWORD=adminpassword
    networks:
      - devops_network_app
      - devops_network_lab
    restart: unless-stopped
  nginx:
    image: nginx:alpine
    container_name: nginx-openvas-proxy
    ports:
      - "3443:443"  # HTTPS exposé sur port 3443
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/ssl:/etc/nginx/ssl:ro
    depends_on:
      - openvas
    networks:
      - devops_network_lab  # Pour atteindre openvas

volumes:
  openvas-data:

networks:
  devops_network_app:
    driver: bridge
  devops_network_lab:
    driver: bridge
```

## 🚀 Lancer

```
docker compose up -d --build
```

## Test

```
docker exec -it openvas bash
ssh -p 2222 mokai@92.242.187.138
```
