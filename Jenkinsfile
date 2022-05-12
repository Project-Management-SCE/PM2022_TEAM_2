pipeline {
    agent {
        docker {
            image 'androidsdk/android-31'
        }
    }
    stages {
        stage('Build') {
            steps {
                echo 'Running build'
                sh 'chmod +x gradlew && ./gradlew --no-daemon --stacktrace clean'
                sh 'echo no | avdmanager create avd -n first_avd --abi google_apis/x86_64 -k "system-images;android-31;google_apis;x86_64"'
                sh 'emulator -avd first_avd -no-window -no-audio &'
                sh 'adb devices'
            }
        }
        stage('Test') {
            steps {
                echo 'Running Test'
                sh 'emulator -avd first_avd -no-window -no-audio &'
                sh './gradlew test'
            }
        }
        stage('Deliver') {
            steps {
                echo 'Running Deliver'
                sh 'emulator -avd first_avd -no-window -no-audio &'
                sh './gradlew assembleRelease appDistributionUploadRelease'
            }
        }
    }
}
