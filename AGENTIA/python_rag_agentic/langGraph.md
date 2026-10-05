### Si vous avez déjà python installé il est la par defaut sur ubuntu

#### installation de uv

```
sudo snap install astral-uv --classic
```

#### creation du docciser du projet

```
mkdir langGraph
```

#### se deplacer dans le repertoire du projet et tapper la commande suivante

```
uv init
```

##### resultat

```
Initialized project 'langGraph'
```


#### installation des dépendences UV

```
uv venv
source .venv/bin/activate
python3 mcp-server.py


uv add ipykernel
uv add langchain
uv add langchain-openai
uv add langchain-community
uv add langgraph
uv add python-dotenv

uv add chromadb
uv add ipython
```
