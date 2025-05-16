## ✅ Étapes pour mettre à jour Apache Tomcat sans perdre les configs

### 1. 🔐 Sauvegarder l’ancienne instance

Avant tout, fais une copie de ton installation existante :

```
cp -r /opt/tomcat /opt/tomcat_backup
```

### 2. 📥 Télécharger la nouvelle version

Va sur https://tomcat.apache.org, télécharge la dernière version stable correspondant à ta version de Java, puis :

```
cd /tmp
wget https://downloads.apache.org/tomcat/tomcat-<version>/v<full-version>/bin/apache-tomcat-<full-version>.tar.gz
wget https://downloads.apache.org/tomcat/tomcat-11/v11.0.7/bin/apache-tomcat-11.0.7.tar.gz

tar -xzvf apache-tomcat-<full-version>.tar.gz
tar -xzvf apache-tomcat-11.0.7.tar.gz

```

### 3. 📁 Préparer la nouvelle structure

Installe la nouvelle version dans un dossier propre :

```
mv apache-tomcat-<full-version> /opt/tomcat_new
mv apache-tomcat-11.0.7 /opt/tomcat_new
```

### 4. 🔄 Copier les fichiers de configuration

Depuis l’ancien dossier (/opt/tomcat_backup), copie uniquement les fichiers importants :

```
cp /opt/tomcat_backup/conf/server.xml /opt/tomcat_new/conf/
cp /opt/tomcat_backup/conf/web.xml /opt/tomcat_new/conf/
cp -r /opt/tomcat_backup/conf/Catalina /opt/tomcat_new/conf/
cp /opt/tomcat_backup/conf/tomcat-users.xml /opt/tomcat_new/conf/
cp -r /opt/tomcat_backup/webapps/ROOT /opt/tomcat_new/webapps/
```

### 5. 🧪 Tester en local

Lance temporairement la nouvelle instance :

```
cd /opt/tomcat/bin
./shutdown.sh

cd /opt/tomcat_new/bin
./startup.sh
```
Va sur http://@IP:8080 et vérifie que tout fonctionne comme attendu.

### 7. 🔄 Basculer définitivement

Une fois vérifié, tu peux soit :

Supprimer l’ancien dossier et renommer le nouveau :


```
mv /opt/tomcat /opt/tomcat_old
mv /opt/tomcat_new /opt/tomcat
```

## FIN

