pipeline {
    agent any

    stages {
        // Trigger Auto-Deployment on Render
        stage('Deploy to Render') {
            steps {
                withCredentials([string(credentialsId: 'RENDER_DEPLOY_HOOK', variable: 'RENDER_URL')]) {
                    bat 'curl -X POST "%RENDER_URL%"'
                }
            }
        }
    }
}