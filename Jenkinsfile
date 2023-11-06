pipeline {
    agent any
    stages {
        stage('Source') {
            steps {
                git branch: 'main', 
                    url: 'https://github.com/icekantapat138/se-ject.git'
            }
        }
        stage ('Build') {
            steps {
                bat 'mvnn package -DskipTests'
            }
        }
        stage ('Test') {
            steps {
                echo 'testing...'
                //bat 'mvn test'
            }
        }
        stage ('Deploy') {
            steps {
                bat 'java -jar ./target/dormitory-1.0.jar'
            }
        }
    }
}
