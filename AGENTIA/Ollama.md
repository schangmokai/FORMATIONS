# Installation en une seule commande
curl -fsSL https://ollama.com/install.sh | sh

# Vérifier l'installation

# Vérifier la version
ollama --version

# Vérifier que le service tourne
sudo systemctl status ollama

# Tester avec un modèle
ollama run llama3.2

# Modèles populaires

ollama pull llama3.2        # Petit et rapide
ollama pull llama3.1:8b     # Équilibré
ollama pull mistral         # Excellent pour le français
ollama pull codellama       # Pour le code
ollama pull phi3            # Petit et efficace

# Lister les modèles installés
ollama list

# Utiliser un modèle
ollama run llama3.2 "Bonjour, comment vas-tu ?"

# Gérer le service

# Démarrer Ollama
sudo systemctl start ollama

# Arrêter Ollama
sudo systemctl stop ollama

# Redémarrer Ollama
sudo systemctl restart ollama

# Activer au démarrage
sudo systemctl enable ollama

# Voir les logs
sudo journalctl -u ollama -f

## Configuration Spring Boot
## application.properties :

spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=llama3.2
spring.ai.ollama.chat.options.temperature=0.7
spring.ai.ollama.chat.options.num-predict=4096

## pom.xml :

<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
    <version>1.0.0-M4</version>
</dependency>