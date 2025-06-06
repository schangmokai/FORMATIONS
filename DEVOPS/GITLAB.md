## Installation Gitlab


```
###############################################################################
#########################  GITLAB DEV      ####################################
###############################################################################
```

docker-compose.yml

```
services:
  gitlab:
    image: gitlab/gitlab-ce:latest
    container_name: gitlab
    hostname: gitlab.example.com
    ports:
      - "443:443"
      - "8080:80"
      - "2222:22"
    restart: always
    volumes:
      - /srv/gitlab/config:/etc/gitlab
      - /srv/gitlab/logs:/var/log/gitlab
      - /srv/gitlab/data:/var/opt/gitlab
    networks:
      - devops_network_app
      - devops_network_lab
networks:
  devops_network_app:
    driver: bridge
  devops_network_lab:
    driver: bridge
```



```
###############################################################################
#########################  GITLAB PROD      ###################################
###############################################################################
```

## installation pour un environnement de prod



1- créer un certificat autosigné

```
openssl req -x509 -newkey rsa:4096 -keyout gitlab-selfsigned.key -out gitlab-selfsigned.crt -days 89365 -nodes
```

2- vérifier la validité du certificat

```
openssl x509 -in gitlab-selfsigned.crt -noout -enddate
ou
openssl s_client -connect 10.145.40.242:443 -showcerts </dev/null 2>/dev/null | openssl x509 -noout -dates -subject
```

3- deplacer les certificat vers un repertoire à monter sur git

```
mkdir -p /srv/gitlab/certs
mv gitlab-selfsigned.crt /srv/gitlab/certs/
mv gitlab-selfsigned.key /srv/gitlab/certs/

mv gitlab-selfsigned.crt 10.145.40.242.crt
mv gitlab-selfsigned.key 10.145.40.242.key

mv 10.145.40.242.crt /srv/gitlab/certs/10.145.40.242.crt
mv 10.145.40.242.key /srv/gitlab/certs/10.145.40.242.key
```

4- Docker compose pour l'installation de gitLab

```
services:
  gitlab:
    image: 'gitlab/gitlab-ce:latest'
    container_name: gitlabhttps
    ports:
      - '80:80'
      - '443:443'
    volumes:
      - '/srv/gitlab/data:/var/opt/gitlab'
      - '/srv/gitlab/logs:/var/log/gitlab'
      - '/srv/gitlab/config:/etc/gitlab'
      - '/srv/gitlab/certs/10.145.40.242.crt:/etc/gitlab/ssl/10.145.40.242.crt:ro'
      - '/srv/gitlab/certs/10.145.40.242.key:/etc/gitlab/ssl/10.145.40.242.key:ro'
    environment:
      GITLAB_OMNIBUS_CONFIG: |
        external_url 'https://10.145.40.242'
        nginx['redirect_http_to_https'] = true
        letsencrypt['enable'] = false
    networks:
      - devops_network_app
      - devops_network_lab
networks:
  devops_network_app:
    driver: bridge
  devops_network_lab:
    driver: bridge
```


### commande de lancement

```
docker compose up -d
```


## change password
```
sudo docker exec -it gitlabhttps /bin/bash
```
```
gitlab-rails console -e production
```
```
user = User.where(id: 1).first
user.password = ''
user.password_confirmation = ''
user.save!
```

## new password
```
username: 
password: 
```

```
curl -k https://10.145.40.242
https://10.145.40.242
```

# GITLAB UP AND TABLE