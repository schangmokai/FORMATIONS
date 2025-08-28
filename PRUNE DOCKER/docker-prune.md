

## Prune docker images

### Nettoyer les conteneurs arrêtés

```
docker system prune -a
```

```
docker container prune -f
```

### Supprimer les images inutilisées

```
docker image prune -a -f
```

### Supprimer les volumes inutilisés

```
docker volume prune -f
```

### Supprimer les réseaux inutilisés

```
docker network prune -f
```
### Tout nettoyer d’un coup (sauf les volumes)

```
docker system prune -a -f
```

### Tout nettoyer, y compris les volumes

```
docker system prune -a --volumes -f
```

###  Vérifier l'espace disque utilisé par Docker

```
docker system df
```

## le must des musts

```
sudo du -h /var/lib/docker/containers/*/*-json.log | sort -h
```

## pour vider

```
sudo truncate -s 0 /var/lib/docker/containers/*/*-json.log
```

