## Cron Job

Les jobs sont des Job qui son programmer par des crontab.

Pod avec Job

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

Exemple de cron-Job pour ce Job

```
apiVersion: batch/v1
kind: CronJob
metadata:
  name: reporting-math-job 
spec:
  schedule: "*/1 * * * *"
  jobTemplate:
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



