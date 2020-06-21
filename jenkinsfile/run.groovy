pipeline {
    agent{node('master')}
    stages {
        stage('Start: clean workspace, download from git') {
            steps {
                script {
                    cleanWs()
                    withCredentials([
                        usernamePassword(credentialsId: 'srv_sudo',
                        usernameVariable: 'username',
                        passwordVariable: 'password')
                    ]) {
                        try {
                            sh "echo '${password}' | sudo -S docker stop terenteva_irina"
                            sh "echo '${password}' | sudo -S docker container rm terenteva_irina"
                        } catch (Exception e) {
                            print 'container not exist, skip clean'
                        }
                    }
                }
                script {
                    echo 'Update from repository'
                    checkout([$class                           : 'GitSCM',
                              branches                         : [[name: '*/master']],
                              doGenerateSubmoduleConfigurations: false,
                              extensions                       : [[$class           : 'RelativeTargetDirectory',
                                                                   relativeTargetDir: 'auto']],
                              submoduleCfg                     : [],
                              userRemoteConfigs                : [[credentialsId: 'IvanSitnikovGit', url: 'https://github.com/irinakushner/devops.git']]])
                }
            }
        }
        stage ('build, run docker image'){
            steps{
                script{
                     withCredentials([
                        usernamePassword(credentialsId: 'srv_sudo',
                        usernameVariable: 'username',
                        passwordVariable: 'password')
                    ]) {

                        sh "echo '${password}' | sudo -S docker build ${WORKSPACE}/auto -t terenteva_irina"
                        sh "echo '${password}' | sudo -S docker run -d -p 1707:80 --name terenteva_irina -v /home/adminci/is_mount_dir:/stat terenteva_irina"
                    }
                }
            }
        }
        stage ('Finish: Get and record statistics'){
            steps{
                script{
                    withCredentials([
                        usernamePassword(credentialsId: 'srv_sudo',
                        usernameVariable: 'username',
                        passwordVariable: 'password')
                    ]) {
                        
                        sh "echo '${password}' | sudo -S docker exec -t terenteva_irina bash -c 'df -h > /statistics/statistics.txt'"
                        sh "echo '${password}' | sudo -S docker exec -t terenteva_irina bash -c 'top -n 1 -b >> /statistics/statistics.txt'"
                    }
                }
            }
        }
        
    }

    
}