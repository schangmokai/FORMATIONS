## REPLICASET

1. ReplicationController
2. ReplicaSet

Les deux jouent presque le même rôle gérer les pods et se rassurer que le nombre de pods souhaité est ce qui est déployé.

### Difference

La grande difference est que le ReolicationController Manage uniquement les pods qu'il a crée alors que le ReplicaSet en plus des pods qu'il a créee, il utiliser un selector pour manager tous les pods qui match.

### Mise à jour des replicaSet

```
kubectl scale --replicas=6 -f toto-rs.yaml
kubectl scale rs toto-rs --replicas=6
```