### Si vous avez déjà python installé il est la par defaut sur ubuntu

#### installation de uv

```
sudo snap install astral-uv --classic
```

#### creation du docciser du projet

```
mkdir agentic-rag
```

#### se deplacer dans le repertoire du projet et tapper la commande suivante

```
uv init
```

##### resultat

```
Initialized project `agentic-rag`
```


#### installation des dépendences UV

```
uv add langchain
uv add langchain-openai
uv add chromadb
uv add python-dotenv
uv add langchain-community
uv add ipython
```
