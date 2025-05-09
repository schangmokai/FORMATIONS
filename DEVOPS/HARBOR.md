## Installation Harbor

```
La meilleur facon de procéder c'est de suivre cette doc https://blog.stephane-robert.info/docs/developper/artefacts/harbor/

wget https://github.com/goharbor/harbor/releases/download/v2.9.1/harbor-offline-installer-v2.9.1.tgz
tar xvfz harbor-offline-installer-v2.9.1.tgz
cd harbor
```

### modification du fichier harbor.yml

```
mv harbor.yml.tmpl harbor.yml
vi harbor.yml
```

```
# Configuration file of Harbor

# The IP address or hostname to access admin UI and registry service.
# DO NOT use localhost or 127.0.0.1, because Harbor needs to be accessed by external clients.
hostname: 92.242.187.138

# http related config
http:
  # port for http, default is 80. If https enabled, this port will redirect to https port
  port: 8083

# https related config
#https:
  # https port for harbor, default is 443
 # port: 4443
  # The path of cert and key files for nginx
 # certificate: /your/certificate/path
 # private_key: /your/private/key/path

```

### preparation de l'installation
```
sudo ./prepare --with-trivy
```
### installation proprement dit
```
sudo ./install.sh
```

## change password
```
http:92.242.187.138:8083
username: 
password: 
```


# HARBOR UP AND TABLE