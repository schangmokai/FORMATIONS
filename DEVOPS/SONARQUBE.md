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
