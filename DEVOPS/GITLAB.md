## Installation Gitlab


docker-compose.yml

```
services:
  nginx:
    image: nginx:alpine
    container_name: nginx
    ports:
      - 80:80
      - 443:443
    restart: always
    volumes:
      - conf_d:/etc/nginx/conf.d
      - certs_data:/etc/nginx/certs
      - data_site:/usr/share/nginx/html
      - logs:/var/log/nginx/log
    networks:
      - devops_network_lab
      - devops_network_app
networks:
  devops_network_lab:
    driver: bridge
  devops_network_app:
    driver: bridge
volumes:
  logs:
    driver: local
  conf_d:
    driver: local
  data_site:
    driver: local
  certs_data:
    driver: local
```

### commande de lancement

```
docker-compose up -d
```


## change password
```
sudo docker exec -it gitlab /bin/bash
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

# GITLAB UP AND TABLE