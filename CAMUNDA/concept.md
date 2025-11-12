## Camunda c'est quoi ?

C'est un orchestrateur de processus il permet d'utomatiser les point de termination des activité d'une entreprise

Exemple de point de terminaison: API Rest

### BPM (Business process Managerment)

### BPMN (Business Process Model and Notation)

   Modelisation des processus metier

###  BPA (Business process Automation)

   Automatisation des processus modeliser dans le BPMN

### Déploiement de Camunda

```
services:
  postgres:
    image: postgres:14
    environment:
      POSTGRES_DB: camunda
      POSTGRES_USER: camunda
      POSTGRES_PASSWORD: camunda
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  camunda:
    image: camunda/camunda-bpm-platform:run-latest
    environment:
      - DB_DRIVER=org.postgresql.Driver
      - DB_URL=jdbc:postgresql://postgres:5432/camunda
      - DB_USERNAME=camunda
      - DB_PASSWORD=camunda
      - WAIT_FOR=postgres:5432
    depends_on:
      - postgres
    ports:
      - "8080:8080"
volumes:
  postgres_data:

```