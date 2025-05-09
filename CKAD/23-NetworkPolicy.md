## NetworkPolicy dans kubernets

Il s'agit d'un composant dans kubernetes qui nous aide à faire du cloasonnement dans notre cluster kubernetes

NB: Le composant NetworkPolicy est disponible pour certains CNI et pas pour d'autre.

Exemple : disponible pour 

1. Calico
2. kube-router
3. Romana
4. Weave-net

Pas disponible pour

1. Flannel


Par defaut kubernetes ouvre le trafic pour tous les pods et pour toutes les destinations dans le Cluster.

Lorsqu'on définit un NetworkPolicy, la première des choses à faire c'est :

1. de spécifier le pod ou l'enssemple des pods auxquelle notre NetworkPolicy s'applique.

```
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: test-network-policy
  namespace: default
spec:
  podSelector:
    matchLabels:
      role: db
```

L'exemple ci-dessus est juste un extrait que nous allons améliorer au fur et a mésure.

Notre exemple s'applique à tous les pods du namespace default ayant pour label role=db

2. Spécifier le type de policy à appliquer

On distingue deux type de policy:

a) Ingress

   Pour definir la policy sur le trafic entrant dans le pods   

b) Egress

   Pour definir la policy sur le trafic sortant du pods

```
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: test-network-policy
  namespace: default
spec:
  podSelector:
    matchLabels:
      role: db
  policyTypes:
  - Ingress
```

3. Une fois le type de policy défini, nous allons définir les spécifications de notre ingress.

```
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: test-network-policy
  namespace: default
spec:
  podSelector:
    matchLabels:
      role: db
  policyTypes:
  - Ingress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          name: api-pod
```

A ce niveau, notre policy s'applique à tous les pod du cluster ayant le label name=api-pod en d'autre terme, nos pod ayant  label role=db pourront accepter uniquement le traffic venant des pods ayant le label name=api-pod.

4. Si nous voulons encore filtrer par namespace, premierement nous devons labeliser le namespace en question.

   Nous devons ajouter une autre proprieté de selection (namespaceSelector).

```
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: test-network-policy
  namespace: default
spec:
  podSelector:
    matchLabels:
      role: db
  policyTypes:
  - Ingress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          name: api-pod
    - namespaceSelector:
        matchLabels:
          name: prod   
```

5. Nous pouvons également bloquer le trafic entrant sur notre pod par adresse IP ne autoriser qu'une IP par exemple.

```
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: test-network-policy
  namespace: default
spec:
  podSelector:
    matchLabels:
      role: db
  policyTypes:
  - Ingress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          name: api-pod
    - namespaceSelector:
        matchLabels:
          name: prod  
    - ipBlock:
        cidr: 192.168.0.10/24 
```

Dans ce cas, le seul service externe autorisé à accéder à notre base de données doit venir de l'ip 192.168.0.10.

7. nous devons maintenant spécifier le port la lequel on entre dans notre pod

```
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: test-network-policy
  namespace: default
spec:
  podSelector:
    matchLabels:
      role: db
  policyTypes:
  - Ingress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          name: api-pod
    - namespaceSelector:
        matchLabels:
          name: prod  
    - ipBlock:
        cidr: 192.168.0.10/24 
    ports:
    - protocol: TCP
      port: 3306
```

notre policy laisse les requêtes entrer dans notre pod par le port 

8. De la même facon, nous pouvons définir une policy de sotie de notre pod.

```
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: test-network-policy
  namespace: default
spec:
  podSelector:
    matchLabels:
      role: db
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          name: api-pod
    - namespaceSelector:
        matchLabels:
          name: prod  
    - ipBlock:
        cidr: 192.168.0.10/24 
    ports:
    - protocol: TCP
      port: 3306
  egress:
  - to:
    - ipBlock:
        cidr: 192.168.0.11/24 
    ports:
    - protocol: TCP
      port: 80
```

notre serveur de backup est a l'adersse 192.168.0.11/24  et nous avons un agent dans le pod qui pousse les données sur le serveur de backup au port 80.

## CBS Policy

```
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: cbs-network-policy
  namespace: default
spec:
  podSelector:
    matchLabels:
      type: backend
  policyTypes:
  - Ingress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          app: crm-front-end
          type: frontend
    - namespaceSelector:
        matchLabels:
          name: default   
```