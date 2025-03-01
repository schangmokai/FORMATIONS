## Installation Nexus


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
docker-compose up -d
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
username: admin
password: mokai
```

# NEXUS UP AND TABLE