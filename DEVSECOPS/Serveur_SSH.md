
```
apt update && apt install -y openssh-server
mkdir -p /var/run/sshd
```

```
/usr/sbin/sshd
```

```
ps aux | grep sshd
```

```
telnet X.X.X.X 2222
```


### creation d'un utilisateur

```
useradd -m -s /bin/bash mokai
```

### set user password

```
echo 'mokai:mokai' | chpasswd
```
```
sed -i 's/#PasswordAuthentication yes/PasswordAuthentication yes/' /etc/ssh/sshd_config
sed -i 's/#PermitRootLogin.*/PermitRootLogin yes/' /etc/ssh/sshd_config
```

### restart

```
/usr/sbin/sshd
```

### teste

```
ssh mokai@localhost -p 2222
```