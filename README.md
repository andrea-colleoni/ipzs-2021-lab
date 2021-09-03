# Esercizio di laboratorio

## Sviluppare la seguente soluzione

### Fase 1 (setup generale, GIT)
- Iniziare con un workspace vuoto
- Aggiungere uno degli esercizi svolti nel modulo di angular (sorgenti)
- Creare un repository SCM (libera scelta sul provider) dove pushare il progetto
- Pushare il progetto angular sul repository

### Fase 2 (solo docker)
- Scrivere nel workspace un docker-compose che sia in grado di pubblicare il progetto angulare buildato in un container ottenuto da un'immagine NGINX
- Verificare che la web application funzioni

### Fase 3 (frestyle su jenkins e dockerfile da docker-cli)
- Scrivere un Free style project che faccia la build del progetto angular (npm install + npm build) e archivi un archivio compresso ZIP di tale build
- Scrivere un Dockerfile che faccia la build un'immagine dell'applicazione angular distribuita su jenkins
  - partire da un'immagine NGINX
  - eseguire il download dello ZIP creato dalla lastSuccessfulBuild (ricordarsi di consentire all'utente anonimo l'accesso in lettura agli artifact archiviati dai job)
  - scompattare lo zip nella cartella dove nginx pubblica la web application
- Verificare che l'immagine sia eseguibile lanciando un container (senza mappatura del volume)

### Fase 4
- Trasformare il Free style project di cui al punto 1 della fase 3 in una pipeline
- Aggiungere alla pipeline le attività docker che sono state eseguite manualmente nella fase 3
  - build dell'immagine
  - tagging dell'immagine usando il build number come tag
  - run del contianer
- Triggerare la pipeline con il polling del SCM
- Effettuare una modifica al progetto e pushare la modifica sul SCM
- Verificare che nel container il progetto in esecuzione contanga la modifica effettuata

### Fase 5
- Aggiungere al progetto (nel workspace) anche un backend Spring Boot
- Buildare il progetto con maven nella pipeline già creata (in modalità parallel)
- Creare un Dockerfile che crei un'immagine del backend
- Aggiungere alla pipline la build dell'immagine
- Aggiungere alla pipleine il run di un docker-compose che comprenda front end e back end