# Svolgimento dell'esercizio

## Fase 1
- Creazione workspace
- Creazione progetto angular
  - ng new esercizio
- Creazione repository su github
- Aggiunta del remote sul folder locale
  - git remote add origin https://github.com/andrea-colleoni/ipzs-2021-lab.git
  - git fetch --all
  - git checkout -b origin/main
- Aggiunta del progetto al branch main, commit e push
  - git add .
  - git commit -m "fase 1"
  - git push

## Fase 2
- Creazione del [file docker-compose](docker/docker-compose.yml)
- Avvio di compose
  - cd docker
  - docker-compose up -d
- Verifica del funzionamento dell'applcazione su http://localhost:8080
- Arresto di compose
  - docker-compose down

## Fase 3
- Avvio di un container jenkins dall'immagine prodotta a lezione
  - docker run 
--rm -d -p 8080:8080 -v <JENKINS_HOME>:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock --name jenkins <IMMAGINE JENKINS>
- Creazione del free style project
  - Generale: Setup di nome e caratteristiche generali (URL github, ecc.)
  - SCM: Setup di SCM con Git
  - Trigger: Impostazione del polling SCM con pianificazione */1 * * * *
  - Impostazione di NPM nel PATH di sistema; devono essere attive le seguenti condizioni:
    - plugin NPM come visto a lezione
    - configurazione strumento globale NodeJS (anche con auto installazione)
  - Aggiunta passo di compilazione __shell__ per la build di angular
    - cd apps/frontend/esercizio-ng
    - npm install
    - npm run build
  - Aggiunta passo di compilazione __shell__ per la creazione dell'archivio compresso
    - tar -zcvf apps/frontend/dist.gz apps/frontend/esercizio-ng/dist/esercizio
  - Aggiunta di un'azione di post compilazione per archiviare l'archivio compresso