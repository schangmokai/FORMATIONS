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


