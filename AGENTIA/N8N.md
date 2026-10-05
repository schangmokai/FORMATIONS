### installation

npx n8n

### si vous n'avez pas la bonne version de nodejs

```
# Installer nvm si ce n'est pas déjà fait
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash

# Recharger votre terminal
source ~/.bashrc  # ou source ~/.zshrc si vous utilisez zsh

# Installer une version compatible
nvm install 20.19

# Utiliser cette version
nvm use 20.19

# Vérifier la version
node --version

# Lancer n8n
npx n8n
```

### pour lancer n8n

```
npx n8n
```

### MCP server

```
http://localhost:8098/mcp
http://localhost:8091/mcp
```

### une fois le workflow construit pour le visualiser sous forme de chat

1- double cick sur l'objet tout au debut qui recois le message utilisateur
2- Make Chat Publicly Available
3- Copier le lien ( Chat URL) et le partager


