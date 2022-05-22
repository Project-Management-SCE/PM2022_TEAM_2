pipeline {
    
    options {
        buildDiscarder logRotator( numToKeepStr: '5')
    }
    environment{
        DEFUALT_MAIL_LIST = 'alonte1@ac.sce.ac.il'
    }
    parameters {
        string(name:'MailingList', defaultValue: '',description: 'Email mailing list', trim: true)
    }
    
    
    agent {
        docker {
            image 'androidsdk/android-31'
        }
    }
    stages {
        stage('Build') {
            steps {
                echo 'Running build'
                //sh 'chmod +x gradlew && ./gradlew --no-daemon --stacktrace clean'
                //sh 'echo no | avdmanager create avd -n first_avd --abi google_apis/x86_64 -k "system-images;android-31;google_apis;x86_64"'
                //sh 'emulator -avd first_avd -no-window -no-audio &'
                //sh 'adb devices'
            }
        }
        /*stage('Test') {
            steps {
                echo 'Running Test'
                sh 'emulator -avd first_avd -no-window -no-audio &'
                sh './gradlew test'
            }
        }*/
               
        /*stage('Deliver') {
            steps {
                echo 'Running Deliver'
                sh 'emulator -avd first_avd -no-window -no-audio &'
                sh './gradlew assembleRelease appDistributionUploadRelease'
            }
        }*/
    }
    
       post {
          always {
            archiveArtifacts artifacts: 'unTagResources_Details.csv', onlyIfSuccessful: true
            script {
                if(params.MailingList || env.DEFUALT_MAIL_LIST){
                    emailext subject: '$DEFAULT_SUBJECT', mimeType: 'text/html', attachmentsPattern: 'unTagResources_Details.csv',
                            to: "${params.MailingList},${env.DEFUALT_MAIL_LIST}", body: '${SCRIPT, template="groovy-html.template"}'
                }
            }
        }
     }
}
