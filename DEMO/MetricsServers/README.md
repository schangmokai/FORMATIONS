pour le HPA pour la collecte des metrique

kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml


Poure le VPA

git clone https://github.com/kubernetes/autoscaler.git
cd autoscaler/

./vertical-pod-autoscaler/hack/vpa-up.sh
