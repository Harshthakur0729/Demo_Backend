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

        // 2. Spring Boot Project Build
        stage('Build Spring Boot JAR') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        // 3. Docker Image Build and Push
        stage('Build & Push Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                        // PowerShell ke zariye Exact Token Pass Hoga (Bina Extra Space ke)
                        powershell 'echo $env:DOCKER_PASS | & "C:\\Users\\yasht\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" login -u $env:DOCKER_USER --password-stdin'
                        
                        // Image Build
                        bat '"C:\\Users\\yasht\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" build -t %DOCKER_IMAGE%:latest .'
                        
                        // Image Push
                        bat '"C:\\Users\\yasht\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" push %DOCKER_IMAGE%:latest'
                    }
                }
            }
        }

        // 4. Trigger Auto-Deployment on Render
        stage('Deploy to Render') {
            steps {
                withCredentials([string(credentialsId: 'RENDER_DEPLOY_HOOK', variable: 'RENDER_URL')]) {
                    bat 'curl -X POST %RENDER_URL%'
                }
            }
        }
    }
}