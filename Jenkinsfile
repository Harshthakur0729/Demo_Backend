pipeline {
    agent any

    stages {
        // 1. SonarQube Verification Check
        stage('SonarQube Verification') {
            steps {
                withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                    bat 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.organization=harshthakur0729 -Dsonar.projectKey=Harshthakur0729_Demo_Backend -Dsonar.host.url=https://sonarcloud.io -Dsonar.token=%SONAR_TOKEN%'
                }
            }
        }

        // 2. Trigger Auto-Deployment on Render
        stage('Deploy to Render') {
            steps {
                withCredentials([string(credentialsId: 'RENDER_DEPLOY_HOOK', variable: 'RENDER_URL')]) {
                    bat 'curl -X POST "%RENDER_URL%"'
                }
            }
        }
    }
}