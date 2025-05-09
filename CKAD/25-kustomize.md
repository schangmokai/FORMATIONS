
## Installation du kustomize

Potasse

## Kustomize + transformer

```
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization

resources:
  - api-deploy.yaml
  - api-service.yaml
```

### comment faire le build avec kustomize

```
kustomize build k8s/
```

Ceci nous permet de voir s'il ya des erreurs

### comment faire le build avec kustomize et appliquer directement dans kubernetes

```
kustomize build k8s/ | kubectl apply -f -

kubectl apply -k k8s/
```

## Transform

### transformers commonLabels

```
commonLabels:
  departement: engineering
```

NB: Avec ceci après le build, kustomize va ajouter à toutes les resource .yaml du sous dossier dans la section label la valeur
departement: engineering

### transformers namePrefix and nameSuffix

Ceci permet d'ajouter un prefix à tous les champs name dans la définition du .yaml

```
namePrefix: mokai-
```

Pareil on peut avoir nameSuffix général ou par dossier

```
nameSuffix: -dev
```

### transformers commonAnnotations

Ceci permet d'ajouter une annotation à tous les yaml du sous dossier

```
commonAnnotations:
  logging: debug
```

### transformers image

```
images:
  - name: nginx
    newName: postgres
    newTag: "4.2"
```

## patches

### Replace 

Pour remplacer le nom d'un deploiement par exemple

#### replace deployment name

```
patches:
  - target:
    kind: Deployment
    name: api-deployment
    
    patch: |-
      - op: replace
        path: /metadata/name
        value: web-deployment
```

### replace replicas value

#### strategie Json 6902 path

```
patches:
  - target:
    kind: Deployment
    name: api-deployment
    
    patch: |-
      - op: replace
        path: /spec/replicas
        value: 3
```

#### strategie merge patch

```
patches:
  - patch: |-
      apiVersion: apps/v1
      kind: Deployment
      metadata:
        name: api-deployment
      spec:
        replicas: 3
```

### Json 6902 patch Inline vs Separate File

```
patches:
  - target:
    kind: Deployment
    name: api-deployment
    
    patch: |-
      - op: replace
        path: /spec/replicas
        value: 3
```

```
patches:
  - target:
    kind: Deployment
    name: api-deployment
    patch: replica-patch.yaml
```

replica-patch.yaml

```
- op: replace
  path: /spec/replicas
  value: 3
```

#### strategie merge patch with file 

```
patches:
  - deployment-patch.yaml
```
deployment-patch.yaml

```
apiVersion: apps/v1
kind: Deployment
  metadata:
  name: api-deployment
spec:
  replicas: 3
```

### pour supprimer un paramètre 


```
patches:
  - target:
      kind: Deployment
      name: mongo-deployment
    
    patch: |-
      - op: remove
        path: /spec/template/metadata/labels/org
```

### pour ajouter un paramètre


```
patches:
  - target:
      kind: Deployment
      name: api-deployment
    
    patch: |-
      - op: add
        path: /spec/template/metadata/labels/org
        value: mokai
```

## Patches List

### Replace List Json6902

```
patches:
  - target:
      kind: Deployment
      name: api-deployment
    
    patch: |-
      - op: replace
        path: /spec/template/spec/conatainers/0
        value: 
          name: haproxy
          image: haproxy
```

### si je veux ajouter un nouveau container dans un pod avec patch

```
patches:
  - target:
      kind: Deployment
      name: api-deployment
    
    patch: |-
      - op: replace
        path: /spec/template/spec/conatainers/-
        value: 
          name: nginx
          image: nginx
```

Resultat

```
spec:
  containers:
  - image: haproxy
    name: haproxy 
  - image: nginx
    name: nginx
```

### pour supprimer le container nginx

```
patches:
  - target:
      kind: Deployment
      name: api-deployment
    
    patch: |-
      - op: remove
        path: /spec/template/spec/conatainers/1
```

ou encore

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-deployment
spec:
  template:
    spec:
      containers:
        - name: nginx
          $patch: delete
```


## Overlays

```
k8s/
|__ base/
|   |__kustomization.yaml
|   |__nginx-depl.yaml
|   |__service.yaml
|   |__redis-deply.yaml
|___overlays/
    |__dev/
    |  |__ kustomization.yaml     
    |  |__ config-map.yaml
    |__stg/
    |  |__ kustomization.yaml
    |  |__ config-map.yaml
    |__prod/
       |__ kustomization.yaml
       |__config-map.yaml

```

Dans la section base: on place tous les fichiers nécessaire pour le deploiement de l'applictation dans tous les env.
Dans la section overlays: on ajoute les modifications en fonction des env.

### base/nginx-deploy.yaml

```
base/nginx-deploy.yaml

apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deploy
spec:
  replicas: 1

```
###  base/kustomization.yaml
```
base/kustomization.yaml

apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization

resources:
  - nginx-depl.yaml
  - service.yaml
  - redis-depl.yaml
```
###  overlays/dev/kustomization.yaml
```
overlays/dev/kustomization.yaml

bases:
  - ../../base
patch: |-
      - op: replace
        path: /spec/replicas
        value: 2
```

###  nous pouvons ajouter grafana uniquement pour la prod

```
k8s/
|__ base/
|   |__kustomization.yaml
|   |__nginx-depl.yaml
|   |__service.yaml
|   |__redis-deply.yaml
|___overlays/
    |__dev/
    |  |__ kustomization.yaml     
    |  |__ config-map.yaml
    |__stg/
    |  |__ kustomization.yaml
    |  |__ config-map.yaml
    |__prod/
       |__ kustomization.yaml
       |__ config-map.yaml
       |__ grfana-deply.yaml

```

###  overlays/prod/kustomization.yaml
```
overlays/dev/kustomization.yaml

apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
bases:
  - ../../base

resources:
  - grafana-deply.yaml 

patch: |-
      - op: replace
        path: /spec/replicas
        value: 2
```

