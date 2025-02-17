## Job

Les jobs sont des processus qui une fois lancés ils font une tâche bien précise et se termine.

Pod avec Job

```
apiVersion: batch/v1
kind: Job
metadata:
  name: math-job
spec:
  template:
    spec:
      containers:
        - name: math-add
          image: ubuntu
          command: ['expr', '3', '+', '3']
      restartPolicy: Never
```

Si on exécute ceci, le job va s'exécuter et le pod sera au status complete

Nous nous souhaiton que notre job j'exécute 5 fois par exemple, nous devons ajouter le paramètre completions: 5

```
apiVersion: batch/v1
kind: Job
metadata:
  name: math-job
spec:
  completions: 5
  template:
    spec:
      containers:
        - name: math-add
          image: ubuntu
          command: ['expr', '3', '+', '3']
      restartPolicy: Never
```

Avec ce qui précède, les jobs sont crées les un après les autre.

Si nous souhaitons lancer les Job en parallèle, nous pouvons ajouter parallelism: 5

```
apiVersion: batch/v1
kind: Job
metadata:
  name: math-job
spec:
  completions: 5
  parallelism: 5
  template:
    spec:
      containers:
        - name: math-add
          image: ubuntu
          command: ['expr', '3', '+', '3']
      restartPolicy: Never
```


