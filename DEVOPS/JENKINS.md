## Installation Jenkins


docker-compose.yml

```
services:
  jenkins:
    image: jenkins/jenkins:lts
    container_name: jenkins
    ports:
      - "8092:8080"
      - "50000:50000"
    volumes:
      - jenkins_home:/var/jenkins_home
    networks:
      - devops_network_app
      - devops_network_lab
    restart: unless-stopped
networks:
  devops_network_app:
    driver: bridge
  devops_network_lab:
    driver: bridge
volumes:
  jenkins_home:
    driver: local
```

## Installation Jenkins integrant maven

```
services:
  jenkins:
    image: jenkins/jenkins:lts
    container_name: jenkins
    ports:
      - "8092:8080"
      - "50000:50000"
    volumes:
      - jenkins_home:/var/jenkins_home
      - maven_repo:/var/jenkins_home/.m2
      - /var/run/docker.sock:/var/run/docker.sock  # Monter le socket Docker de l'hôte
    networks:
      - devops_network_app
      - devops_network_lab
    restart: unless-stopped
    user: root  # Required for package installations inside the container
    environment:
      MAVEN_HOME: /usr/share/maven
      JAVA_HOME: /usr/lib/jvm/java-17-openjdk-amd64  # Chemin du JDK 17
      PATH: $PATH:/usr/share/maven/bin:/usr/lib/jvm/java-17-openjdk-amd64/bin  # Ajout de Java au PATH
    command: >
      /bin/bash -c "
      apt-get update &&
      apt-get install -y maven docker.io openjdk-17-jdk wget yq &&
      wget https://github.com/aquasecurity/trivy/releases/download/v0.44.0/trivy_0.44.0_Linux-64bit.deb &&
      dpkg -i trivy_0.44.0_Linux-64bit.deb &&
      exec /usr/local/bin/jenkins.sh"
networks:
  devops_network_app:
    driver: bridge
  devops_network_lab:
    driver: bridge
volumes:
  jenkins_home:
    driver: local
  maven_repo:
    driver: local
```

```
docker-compose up -d
```

### pour avoir le password admin

```
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```
## default password

```
a8ab95b2b65a425690a91e3d0fd58c8e
```

### access url 

http://92.242.187.138:8082/

## change password
```
username: admin
password: Mokai@10.
```

# JENKINS UP AND TABLE