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
        APP_NAME      = 'temperature-conversion-service'
        DOCKER_IMAGE  = 'sanjayacl/temp-conversion'
        DOCKER_CREDS  = 'dockerhub-creds'  // Jenkins credential ID (username/password or access token) for Docker Hub login
        IMAGE_TAG     = "${env.GIT_COMMIT ? env.GIT_COMMIT.take(7) : env.BUILD_NUMBER}"
        K8S_DIR       = 'k8s'
        EC2_HOST      = 'ec2-user@54.160.201.215'
        EC2_CREDS     = 'fever-detection-ec2-ssh-key'  // same EC2 also runs the k3s cluster
        KUBECONFIG    = '/etc/rancher/k3s/k3s.yaml'
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

        stage('Build & Push Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKER_CREDS) {
                        def image = docker.build("${DOCKER_IMAGE}:${IMAGE_TAG}")
                        image.push()
                        image.push('latest')
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sshagent(credentials: [EC2_CREDS]) {
                    // Ship the current manifests over and apply them so service/ingress/deployment stay in sync with the repo
                    sh "scp -o StrictHostKeyChecking=no -r ${K8S_DIR} ${EC2_HOST}:/tmp/${APP_NAME}-k8s"
                    sh """
                        ssh -o StrictHostKeyChecking=no ${EC2_HOST} '
                            export KUBECONFIG=${KUBECONFIG}
                            sudo -E kubectl apply -f /tmp/${APP_NAME}-k8s
                            sudo -E kubectl set image deployment/${APP_NAME} ${APP_NAME}=${DOCKER_IMAGE}:${IMAGE_TAG}
                            sudo -E kubectl rollout status deployment/${APP_NAME} --timeout=120s
                        '
                    """
                }
            }
        }
    }

    post {
        success {
            echo "Deployment of ${DOCKER_IMAGE}:${IMAGE_TAG} to Kubernetes succeeded."
        }
        failure {
            echo "Pipeline failed for ${APP_NAME}. Check the logs above."
        }
    }
}
