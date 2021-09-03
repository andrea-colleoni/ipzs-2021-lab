# Svolgimento dell'esercizio

## Fase 1
- Creazione workspace
- Creazione progetto angular
  ```console
  ng new esercizio
  ```
- Creazione repository su github
- Aggiunta del remote sul folder locale
  ```console
  git remote add origin https://github.com/andrea-colleoni/ipzs-2021-lab.git
  git fetch --all
  git checkout -b origin/main
  ```
- Aggiunta del progetto al branch main, commit e push
  ```console
  git add .
  git commit -m "fase 1"
  git push
  ```

## Fase 2
- Creazione del [file docker-compose](docker/docker-compose.yml)
- Avvio di compose
  ```console
  cd docker
  docker-compose up -d
  ```
- Verifica del funzionamento dell'applcazione su http://localhost:8080
- Arresto di compose
  ```console
  docker-compose down
  ```

## Fase 3
- Avvio di un container jenkins dall'immagine prodotta a lezione
  ```console
  docker run --rm -d -p 8080:8080 -v <JENKINS_HOME>:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock --name jenkins <IMMAGINE JENKINS>
  ```
- Creazione del free style project
  - Generale: Setup di nome e caratteristiche generali (URL github, ecc.)
  - SCM: Setup di SCM con Git
  - Trigger: Impostazione del polling SCM con pianificazione */1 * * * *
  - Impostazione di NPM nel PATH di sistema; devono essere attive le seguenti condizioni:
    - plugin NPM come visto a lezione
    - configurazione strumento globale NodeJS (anche con auto installazione)
  - Aggiunta passo di compilazione __shell__ per la build di angular
    ```console
    cd apps/frontend/esercizio-ng
    npm install
    npm run build
    ```
  - Aggiunta passo di compilazione __shell__ per la creazione dell'archivio compresso
    ```console
    cd apps/frontend/esercizio-ng/dist
    tar -zcvf ../../dist.gz esercizio
    ```
  - Aggiunta di un'azione di post compilazione per archiviare l'archivio compresso
- Creazione dell'immagine dell'applicazione
  - Definire il [Dockerfile dell'applicazione](docker/Dockerfile-Fase3)
  - Build dell'immagine: 
  ```console
  docker build -f Dockerfile-Fase3 -t esercizio/frontend:latest .
  ```
  - Run della nuova immagine: 
  ```console
  docker run -p 8080:80 esercizio/frontend
  ```
  - Verifica che l'app funzioni: http://localhost:8080

## Fase 4
- Creazione del pipeline project
  - Riportare nel progetto le configurazioni comuni al progetto free style
  - Aggiungere la [Pipeline](jenkins/pipeline-fase4.groovy)
  - Eseguire la build
  - Annotazioni:
    - Nella pipleine è stato usato un pluging per comprimere la directory di distribuzione; il plugin produce un archivio diverso dal comando tar di linux usato nel free style project
- Aggiunta delle azioni di deployment con docker
  - Aggiunta di un nuovo stage 'Deploy'
  - Aggiunta dei comandi docker come shell script dalla directory con le risorse docker
  ```groovy
    sh 'echo "Build e tag immagine"'
    dir('docker') {
        sh "docker build -f Dockerfile-Fase3 -t esercizio/frontend:latest -t esercizio/frontend:${env.BUILD_NUMBER} ."
        sh 'docker run -d -p 8080:80 --name esercizio-webapp esercizio/frontend'
    }   
  ```
  - Verifica che http://localhost:8080 risponda correttamente
  - __Attenzione__: nella esecuzione delle build dopo la prima si deve verificare che il contianer avviato alla build precedente sia stato arrestato prima di poter avviare il contianer della nuova build.

## Fase 5
- Creare un progetto Spring Boot da [Spring Intializr](https://start.spring.io/)
  - Aggiungere le dipendenze desiderate (e.g. web)
  - Scaricare lo ZIP del progetto nel workspace ed strarlo in una directory (e.g. apps/backend)
  - Compilarlo usando maven
```console
cd apps/backend/esercizio
mvn compile
mvn spring-boot:run
```
  - Modificare il pom.xml secondo le necessità (e.g. modificando il finalName dell'artifact generato)
- Modificare la pipeline
  - Aggiungere il tool JDK per la compilazione con maven
  - Aggiungere lo stage 'Backend' allo stage __parallel__
  - Aggiungere gli step per pulizia, compilazione, packaging e test
  - Aggiungere nelle azioni di post compilazione la pubblicazione dell'artifact e la pubblicazione dei risultati dei test con JUnit
  - Pushare su git il workspace e lasciare che la build di Jenkins parta automaticamente

