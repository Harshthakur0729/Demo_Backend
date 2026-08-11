pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'harshthakur0729/backend_images'
        DOCKER_HUB_CRED = 'dockerhub-credentials'
        DOCKER_HOST = 'tcp://127.0.0.1:2375'
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'oct', url: 'https://github.com/Harshthakur0729/Demo_Backend.git'
            }
        }

        stage('Build Spring Boot JAR') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                        bat '"C:\\Users\\yasht\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" login -u %DOCKER_USER% -p %DOCKER_PASS%'
                        bat '"C:\\Users\\yasht\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" build -t %DOCKER_IMAGE%:latest .'
                        bat '"C:\\Users\\yasht\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" push %DOCKER_IMAGE%:latest'
                    }
                }
            }
        }

        stage('Deploy to Render') {
            steps {
                withCredentials([string(credentialsId: 'RENDER_DEPLOY_HOOK', variable: 'RENDER_URL')]) {
                    bat 'curl -X POST %RENDER_URL%'
                }
            }
        }
    }
}
