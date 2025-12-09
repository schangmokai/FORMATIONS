## desinstaller forti-client

```
sudo apt remove --purge forticlient -y
```

```
sudo rm -rf /etc/forticlient
sudo rm -rf /opt/forticlient
sudo rm -rf ~/.config/FortiClient
```

## pour installer 

```
sudo apt install forticlient
```


### Ou encore un autre client 

```
²
```
### 
```
sudo openfortivpn 10.10.10.10:443 -u mokai --trusted-cert 982c1b1faba393267d54567d09f0e80f3e208326a99768dskdsldjkjh67278
```

ou avec le fichier

```
sudo nano /etc/openfortivpn/config
```

```
host = 10.10.10.10
port = 443
username = mokai
trusted-cert = 982c1b1faba393267d54567d09f0e80f3e208326a99768dskdsldjkjh67278
```

## pour lancer

sudo openfortivpn

