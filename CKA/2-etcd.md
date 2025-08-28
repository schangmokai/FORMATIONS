## ETCD

C'est une base de données clé/valeur dans laquelle toutes les instruction du cluster kubernetes sont stockées. 

### entre autres:

 1. node Controller
 2. service Controller
 3. service-account Controller
 4. deployment Controller
 5. replicaset Controller
 6. pv Controller
 7. pvc Controller
 8. job Controller
 9. cronJob Controller
 10. namespace Controller
 11. etc

Toutes les commandes dans kubernetes passe d'abord par l'API-SERVER mais par la suite sont stoqué dans l'ETCD