pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'Java21'
    }

    triggers {
        githubPush()
    }

    environment {
        APP_NAME = 'temperature-conversion-service'
        JAR_NAME = 'temperature-conversion-service-0.0.1-SNAPSHOT.jar'
        DEPLOY_DIR = '/opt/services'
        EC2_HOST   = 'ec2-user@54.160.201.215'
        EC2_CREDS  = 'fever-detection-ec2-ssh-key'  // Jenkins credential ID for the EC2 private key, since we use same fever detect ec2, it works here as well
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Deploy') {
            steps {
                sshagent(credentials: [EC2_CREDS]) {
                    // Stop the service and ensure the deploy directory exists
                    sh """
                        ssh -o StrictHostKeyChecking=no ${EC2_HOST} '
                            sudo mkdir -p ${DEPLOY_DIR}
                            sudo systemctl stop ${APP_NAME} || true
                        '
                    """

                    // Transfer the new JAR from Jenkins server to EC2
                    sh "scp -o StrictHostKeyChecking=no target/${JAR_NAME} ${EC2_HOST}:${DEPLOY_DIR}/${APP_NAME}.jar"

                    // Start the service and verify it came up healthy
                    sh """
                        ssh -o StrictHostKeyChecking=no ${EC2_HOST} '
                            sudo systemctl daemon-reload
                            sudo systemctl start ${APP_NAME}
                            sleep 10
                            sudo systemctl is-active --quiet ${APP_NAME} && echo "Service started successfully" || (echo "Service failed to start"; sudo journalctl -u ${APP_NAME} --no-pager -n 30; exit 1)
                        '
                    """
                }
            }
        }
    }

    post {
        success {
            echo "Deployment of ${APP_NAME} succeeded."
        }
        failure {
            echo "Pipeline failed for ${APP_NAME}. Check the logs above."
        }
    }
}
