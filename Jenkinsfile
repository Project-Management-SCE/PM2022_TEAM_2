pipeline {
    agent {
        docker {
            image 'androidsdk/android-30'
        }
    }
    stages {
        stage('Build') {
            steps {
                echo 'Running build'
                sh 'chmod +x gradlew && ./gradlew --no-daemon --stacktrace clean --warning-mode all'
                sh 'echo no | avdmanager create avd -n first_avd --abi google_apis/x86_64 -k "system-images;android-30;google_apis;x86_64"'
                sh 'emulator -avd first_avd -no-window -no-audio &'
                sh 'adb devices'
            }
        }
        /*stage('Test') {
            steps {
                echo 'Running Test'
                sh 'emulator -avd first_avd -no-window -no-audio &'
                sh './gradlew test'
            }
            post {
                always {
                    echo 'Running post-test'
                }
            }
        }
        stage('Deliver') {
            steps {
                echo 'Running Deliver'
                echo 'Connecting to FireBase... '
                sh 'emulator -avd first_avd -no-window -no-audio &'
                sh './gradlew'

                echo 'Connecting to Data Base... '
                sh 'emulator -avd first_avd -no-window -no-audio &'
                sh './gradlew'
                sh 'adb devices'
            }
        }*/
    }
}
