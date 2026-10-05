#### installation de uv

```
sudo snap install astral-uv --classic
```

### installation de la dependence mcp

```
uv add "mcp[cli]"
```

### ajouter la dependence de langchain

```
uv add langchain
uv add langchain-tavily
```

### il faut une clé pour TAVILY


https://app.tavily.com/home


### pour installer la variable d'environnement

```
uv add python-dotenv
``` 


### pour démarrer une interface de test du MCP


```
npx @modelcontextprotocol/inspector

```

## pour le lancer en local

```
source .venv/bin/activate
python3 mcp-server.py
```