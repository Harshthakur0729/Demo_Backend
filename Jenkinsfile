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
                // Windows par 'sh' ki jagah 'bat' use hota hai
                bat 'mvn clean package -DskipTests'
            }
        }

        // 3. Docker Image Build and Push
        stage('Build & Push Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                        // Docker Login
                        bat 'echo %DOCKER_PASS% | "C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe" login -u %DOCKER_USER% --password-stdin'
                        
                        // Build Image
                        bat '"C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe" build -t %DOCKER_IMAGE%:latest .'
                        
                        // Push Image
                        bat '"C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe" push %DOCKER_IMAGE%:latest'
                    }
                }
            }
        }

        // 4. Trigger Auto-Deployment on Render
        stage('Deploy to Render') {
            steps {
                withCredentials([string(credentialsId: 'RENDER_DEPLOY_HOOK', variable: 'RENDER_URL')]) {
                    // Windows Batch variable syntax %RENDER_URL% use kiya gaya hai
                    bat 'curl -X POST %RENDER_URL%'
                }
            }
        }
    }
}