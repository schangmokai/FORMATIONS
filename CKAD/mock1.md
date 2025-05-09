======================1=======================

For this question, please set the context to cluster1 by running:

kubectl config use-context cluster1

Create a pod named ckad17-qos-aecs-3 in namespace ckad17-nqoss-aecs with image nginx and container name ckad17-qos-ctr-3-aecs.

Define other fields such that the Pod is configured to use the Quality of Service (QoS) class of Burstable.

Also retrieve the name and QoS class of each Pod in the namespace ckad17-nqoss-aecs in the below format and save the output to a file named qos_status_aecs in the /root directory.

Format:

NAME    QOS
pod-1   qos_class
pod-2   qos_class

Solution



student-node ~ ➜ kubectl config use-context cluster1
Switched to context "cluster1".

student-node ~ ➜  cat << EOF | kubectl apply -f -
apiVersion: v1
kind: Pod
metadata:
name: ckad17-qos-aecs-3
namespace: ckad17-nqoss-aecs
spec:
containers:
- name: ckad17-qos-ctr-3-aecs
  image: nginx
  resources:
  limits:
  memory: "200Mi"
  requests:
  memory: "100Mi"
  EOF

pod/ckad17-qos-aecs-3 created

student-node ~ ➜  kubectl --namespace=ckad17-nqoss-aecs get pod --output=custom-columns="NAME:.metadata.name,QOS:.status.qosClass"
NAME                QOS
ckad17-qos-aecs-1   BestEffort
ckad17-qos-aecs-2   Guaranteed
ckad17-qos-aecs-3   Burstable

student-node ~ ➜  kubectl --namespace=ckad17-nqoss-aecs get pod --output=custom-columns="NAME:.metadata.name,QOS:.status.qosClass" > /root/qos_status_aecs


====================2=====================

For this question, please set the context to cluster2 by running:


kubectl config use-context cluster2



Create a custom resource my-anime of kind Anime with the below specifications:


Name of Anime: Death Note
Episode Count: 37


TIP: You may find the respective CRD with anime substring in it.


Solution

student-node ~ ➜  kubectl config use-context cluster2
Switched to context "cluster2".

student-node ~ ➜  kubectl get crd | grep -i anime
animes.animes.k8s.io

student-node ~ ➜  kubectl get crd animes.animes.k8s.io \
-o json \
| jq .spec.versions[].schema.openAPIV3Schema.properties.spec.properties
{
"animeName": {
"type": "string"
},
"episodeCount": {
"maximum": 52,
"minimum": 24,
"type": "integer"
}
}

student-node ~ ➜  k api-resources | grep anime
animes                            an           animes.k8s.io/v1alpha1                 true         Anime

student-node ~ ➜  cat << YAML | kubectl apply -f -
apiVersion: animes.k8s.io/v1alpha1
kind: Anime
metadata:
name: my-anime
spec:
animeName: "Death Note"
episodeCount: 37
YAML
anime.animes.k8s.io/my-anime created

student-node ~ ➜  k get an my-anime
NAME       AGE
my-anime   23s

====================3====================

For this question, please set the context to cluster1 by running:


kubectl config use-context cluster1



Create a ConfigMap named ckad04-config-multi-env-files-aecs in the default namespace from the environment(env) files provided at /root/ckad04-multi-cm directory.

Solution

```
student-node ~ ➜  kubectl config use-context cluster1
Switched to context "cluster1".

student-node ~ ➜  kubectl create configmap ckad04-config-multi-env-files-aecs \
         --from-env-file=/root/ckad04-multi-cm/file1.properties \
         --from-env-file=/root/ckad04-multi-cm/file2.properties
configmap/ckad04-config-multi-env-files-aecs created

student-node ~ ➜  k get cm ckad04-config-multi-env-files-aecs -o yaml
apiVersion: v1
data:
  allowed: "true"
  difficulty: fairlyEasy
  exam: ckad
  modetype: openbook
  practice: must
  retries: "2"
kind: ConfigMap
metadata:
  name: ckad04-config-multi-env-files-aecs
  namespace: default
```

===================4====================

For this question, please set the context to cluster3 by running:


kubectl config use-context cluster3



We have already deployed the required pods and services in the namespace ckad01-db-sec.



Create a new secret named ckad01-db-scrt-aecs with the data given below.


Secret Name: ckad01-db-scrt-aecs

Secret 1: DB_Host=sql01

Secret 2: DB_User=root

Secret 3: DB_Password=password123

Configure ckad01-mysql-server to load environment variables from the newly created secret, where the keys from the secret should become the environment variable name in the Pod.

```
student-node ~ ➜  kubectl config use-context cluster3
Switched to context "cluster3".

student-node ~ ➜  k get all -n ckad01-db-sec
NAME                         READY   STATUS    RESTARTS   AGE
pod/ckad01-mysql-server   1/1     Running   0          3m13s
pod/ckad01-db-pod-aecs       1/1     Running   0          3m13s

NAME                                 TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
service/ckad01-webapp-service-aecs   NodePort    10.43.190.89    <none>        8080:30080/TCP   3m13s
service/ckad01-db-svc-aecs           ClusterIP   10.43.117.255   <none>        3306/TCP         3m13s

student-node ~ ➜  kubectl create secret generic ckad01-db-scrt-aecs \
   --namespace=ckad01-db-sec \
   --from-literal=DB_Host=sql01 \
   --from-literal=DB_User=root \
   --from-literal=DB_Password=password123
secret/ckad01-db-scrt-aecs created

student-node ~ ➜  k get -n ckad01-db-sec pod ckad01-mysql-server -o yaml > webapp-pod-sec-cfg.yaml

student-node ~ ➜  vim webapp-pod-sec-cfg.yaml

student-node ~ ➜  cat webapp-pod-sec-cfg.yaml 
apiVersion: v1
kind: Pod
metadata:
  labels:
    name: ckad01-mysql-server
  name: ckad01-mysql-server
  namespace: ckad01-db-sec
spec:
  containers:
  - image: kodekloud/simple-webapp-mysql
    imagePullPolicy: Always
    name: webapp
    envFrom:
    - secretRef:
        name: ckad01-db-scrt-aecs

student-node ~ ➜  kubectl replace -f webapp-pod-sec-cfg.yaml --force 
pod "ckad01-mysql-server" deleted
pod/ckad01-mysql-server replaced

student-node ~ ➜  kubectl exec -n ckad01-db-sec ckad01-mysql-server -- printenv | egrep -w 'DB_Password=password123|DB_User=root|DB_Host=sql01'
DB_Password=password123
DB_User=root
DB_Host=sql01
```

=====================6===================

For this question, please set the context to cluster2 by running:


kubectl config use-context cluster2



Create a ResourceQuota called ckad16-rqc in the namespace ckad16-rqc-ns and enforce a limit of one ResourceQuota for the namespace.

Solution

```
student-node ~ ➜  kubectl config use-context cluster2
Switched to context "cluster2".

student-node ~ ➜  kubectl create namespace ckad16-rqc-ns
namespace/ckad16-rqc-ns created

student-node ~ ➜  cat << EOF | kubectl apply -f -
apiVersion: v1
kind: ResourceQuota
metadata:
  name: ckad16-rqc
  namespace: ckad16-rqc-ns
spec:
  hard:
    resourcequotas: "1"
EOF

resourcequota/ckad16-rqc created

student-node ~ ➜  k get resourcequotas -n ckad16-rqc-ns
NAME              AGE   REQUEST               LIMIT
ckad16-rqc   20s   resourcequotas: 1/1
```




