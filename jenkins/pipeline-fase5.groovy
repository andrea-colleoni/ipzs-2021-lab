pipeline {
    agent any
    
    tools {
      jdk 'Jdk-11'
      maven 'Maven 3.8.2'
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
            
                stage('Backend') {
                    stages {
                        stage('Pulizia e compilazione') {
                            steps {
                                dir('apps/backend/esercizio') {
                                    sh 'mvn -Dmaven.test.skip=true clean compile'
                                }
                            }
                        }
                        stage('Test & Package') {
                            steps {
                                dir('apps/backend/esercizio') {
                                    sh 'mvn package'
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
                    sh 'docker stop esercizio-webapp || true'
                    sh 'docker run -d --rm -p 8080:80 --name esercizio-webapp esercizio/frontend'
                }                
            }
        }
    }
    post {
        success {
            archiveArtifacts artifacts: 'apps/backend/esercizio/target/backend.jar', followSymlinks: false
        }
always {
            junit 'apps/backend/esercizio/target/surefire-reports/*.xml'
            writeFile encoding: 'UTF-8', 
                file: 'BUILD.md', 
                text: """# Informazioni di build

__Data build__: """ + new java.util.Date().toString() + """
__Progetto__: ${env.JOB_NAME} ${currentBuild.currentResult}
__Build n. ${env.BUILD_NUMBER}__: ${currentBuild.result}"""     
            archiveArtifacts artifacts: 'BUILD.md', followSymlinks: false
        }        
    }
    
}