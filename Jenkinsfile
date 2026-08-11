pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'harshthakur0729/backend_images'
        DOCKER_HUB_CRED = 'dockerhub-credentials'
    }

    stages {
        // 1. Git Repository Checkout
        stage('Checkout Code') {
            steps {
                git branch: 'oct', url: 'https://github.com/Harshthakur0729/Demo_Backend.git'
            }
        }

        // 2. Spring Boot Project Build (JAR Generation)
        stage('Build Spring Boot JAR') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        // 3. Docker Image Build and Push
        stage('Build & Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKER_HUB_CRED}") {
                        def customImage = docker.build("${DOCKER_IMAGE}:latest")
                        customImage.push('latest')
                    }
                }
            }
        }

        // 4. Trigger Auto-Deployment on Render
        stage('Deploy to Render') {
            steps {
                withCredentials([string(credentialsId: 'RENDER_DEPLOY_HOOK', variable: 'RENDER_URL')]) {
                    sh 'curl -X POST $RENDER_URL'
                }
            }
        }
    }
}