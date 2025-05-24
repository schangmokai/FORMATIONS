## Stratégie de gestion des branches GIT-FLOW

#### 1- `master` : pour la production
#### 2- `develop` : la branche où l’on fusionne les développements
#### 3- `release` : pour les versions prêtes à être mises en production
#### 4- `feature` : branches des développeurs créées à partir de `develop` pour chaque ticket Jira
#### 5- `hotfix` : branche destinée à corriger les bugs en production. Elle est créée à partir de `develop` pour implémenter le correctif. Une fois le correctif appliqué, on la fusionne à la fois sur `develop` et sur `master`.

Ce qui précède est une mise en pratique de Git Flow mais de façon manuelle.

## Installation de Git Flow

```bash
  sudo apt-get install git-flow
```

### 1) Initialiser le projet

```bash
  git flow init
```

### 2) Créer une nouvelle branche en utilisant Git Flow

```bash
  git flow feature start homepage
```

Git Flow va créer une nouvelle branche `feature/homepage` à partir de `develop`.

### 3) Après les commits, pour pousser la branche `homepage` sur le dépôt distant

```bash
  git flow feature publish homepage
```

### 4) Une fois mes développements terminés

```bash
  git flow feature finish homepage
```

Cette action fusionne la branche sur `develop`, supprime la branche `feature/homepage` en local et bascule la branche locale sur `develop`.

### 5) Étant sur `develop`, si nous voulons faire une release, nous pouvons utiliser la commande suivante :

```bash
  git flow release start v0.0.1
```

Cela créera une nouvelle branche `release/v0.0.1` sur la base de `develop` et nous positionnera dessus.

### 6) Étant sur la release, nous allons faire un bump de version dans le fichier `README.md` (c’est-à-dire une mise à jour de la documentation de la version) :

**README.md**
```md
## début

## fin
```

### 7) Ensuite, faire un commit puis un finish :

```bash
  git flow release finish v0.0.1
```

Cette commande met à jour la branche `master` avec les modifications de la release, puis met également à jour `develop` avec les mêmes modifications.  
Ensuite, la branche `release` est supprimée.

### 8) Publier les mises à jour

```bash
  git push --tags
git push origin master
git push origin develop
```

### 9) Création d’un hotfix à l’aide de Git Flow

```bash
  git flow hotfix start title
```

Faire une petite modification, un `git add`, puis un `git commit`.

### 10) Une fois les modifications terminées

```bash
  git flow hotfix finish title
```

Cela fusionne la branche avec `master` et `develop`, puis supprime la branche `hotfix`. Enfin, on est repositionné sur `develop`.

---

### ✅ FIN