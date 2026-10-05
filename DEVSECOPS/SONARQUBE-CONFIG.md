## Installation Configuration


### 1. 🔑 Créer un Token d’accès

Aller dans SonarQube > Mon compte > Security > Generate Token

![img_4.png](img/img_4.png)

Garde le token pour l'étape Jenkins.

#### 1.1 pour une communication bidirectionnel entre Snarqube et jenkins

NB: pour que SonarQube partage le resultat de l'analyse avec Jenkins il faut créer un webhooks dans sonarquebe

Administrateur > configuration > webhooks

![img_9.png](img/img_9.png)

Administrateur > configuration > webhooks

![img_10.png](img/img_10.png)


### 2. 🧰 Configurer sonarQube dans Jenkins

#### a) Installer le plugin SonarQube Scanner
Jenkins > Gérer Jenkins > Gérer les plugins > Installer SonarQube Scanner

### Creer les credential dans jenkins pour sonarQube

NB: Le type de credential est bien Secret text

![img_6.png](img/img_6.png)

#### b) Ajouter SonarQube dans Jenkins

Jenkins >Administrer Jenkins > System

![img_5.png](img/img_5.png)

Section SonarQube servers :

Nom : SonarQube

URL : http://localhost:9000

Ajouter les credentials (Token créé plus haut)

![img_7.png](img/img_7.png)

## FIN DU DOCUMENT