pipeline {
    agent any
    
    tools {
      nodejs 'NodeJS 16.8'
    }
    
    stages {
        stage('Inizio') {
            steps {
                echo 'Build started'
            }
        }
        stage('Checkout') {
            steps {
                git branch: 'main', 
                    url: 'https://github.com/andrea-colleoni/ipzs-2021-lab.git'
            }
        }
        stage('Build') {
            parallel {
                stage('Frontend') {
                    stages {
                        stage('NPM install') {
                            steps {
                                dir('apps/frontend/esercizio-ng') {
                                    sh 'npm install'
                                    sh 'npm run build'
                                }
                            }
                        }
                        stage('ZIP dist') {
                            steps {
                                script {
                                    zip archive: true, 
                                        dir: 'apps/frontend/esercizio-ng/dist/esercizio',
                                        overwrite: true, 
                                        zipFile: 'apps/frontend/dist.gz'
                                }
                            }
                        }   
                    }
                }  
            }
        }
        stage('Deploy') {
            steps {
                sh 'echo "Build e tag immagine"'
                dir('docker') {
                    sh "docker build -f Dockerfile-Fase3 -t esercizio/frontend:latest -t esercizio/frontend:${env.BUILD_NUMBER} ."
                    sh 'docker run -d --rm -p 8080:80 --name esercizio-webapp esercizio/frontend'
                }                
            }
        }
    }
}