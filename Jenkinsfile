pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'harshthakur0729/backend_images'
        DOCKER_HUB_CRED = 'dockerhub-credentials'
        DOCKER_HOST = 'tcp://127.0.0.1:2375'
    }

    stages {
        // 1. Checkout Code
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        // 2. Build Spring Boot JAR
        stage('Build Spring Boot JAR') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        // 3. Build & Push Docker Image
        stage('Build & Push Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                        // Login to Docker Hub
                        bat '"C:\\Users\\yasht\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" login -u %DOCKER_USER% -p %DOCKER_PASS%'
                        
                        // Step A: Image Build
                        bat '"C:\\Users\\yasht\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" build -t %DOCKER_IMAGE%:latest .'
                        
                        // Step B: Push to Docker Hub
                        bat '"C:\\Users\\yasht\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" push %DOCKER_IMAGE%:latest'
                    }
                }
            }
        }

        // 4. Trigger Auto-Deployment on Render
        stage('Deploy to Render') {
            steps {
                withCredentials([string(credentialsId: 'RENDER_DEPLOY_HOOK', variable: 'RENDER_URL')]) {
                    bat 'curl -X POST "%RENDER_URL%"'
                }
            }
        }
    }
}