node('built-in') {

    jdk = tool name: 'JDK-17'
    env.JAVA_HOME = "${jdk}"

    /* Stage checkout, will get the source code from git server */
    stage('Checkout') {
        checkout scm
        currentPomVersion = readMavenPom().getVersion() // Get current pom version after checkout the project
    }

    /* Stage build, build the project to generate jar file */
    stage('Build') {
        withMaven(maven: 'MAVEN') {
            sh "chmod +x mvnw"
            sh "./mvnw clean install -D maven.test.skip=true"
        }
    }


    stage('Push image to docker registry') {
        /*
            Login to Docker registry and push this images was built in stage Buid
            docker-registry-user: The credential that defined on Jenkins server
        */


        print 'Build with staging configuration.......'
        docker.withRegistry('https://registry.hub.docker.com', 'docker-registry-user') {
            def tvShowImage = docker.build("hoangtan250997/kafka_consumer")
            tvShowImage.push()
        }

        sh "docker rmi registry.hub.docker.com/hoangtan250997/kafka_consumer -f || true"
    }

    stage('Pull and start new container') {
        /*
        Switch to Test server pull and run image from registry
        Before build, must stop and remove container that already run. then remove old image in the dev server
        After remove container and image, pull new image from registry and rerun the container
        */
        withCredentials([usernamePassword(credentialsId: SERVER_ACCOUNT, usernameVariable: 'serverUsername', passwordVariable: 'serverPassword')]) {
            def remote = [:]
            remote.name = SERVER_HOSTNAME
            remote.host = SERVER_HOSTNAME
            remote.user = serverUsername
            remote.password = serverPassword
            remote.allowAnyHosts = true
            sshCommand remote: remote, command: """docker stop ${CONTAINER_NAME} || true && docker rm ${CONTAINER_NAME} || true"""
            sshCommand remote: remote, command: """docker rmi registry.hub.docker.com/hoangtan250997/kafka_consumer -f || true"""
            withCredentials([usernamePassword(credentialsId: 'docker-registry-user', usernameVariable: 'dockerRegistryAccountName', passwordVariable: 'dockerRegistryAccountPassword')])
                    {
                        sshCommand remote: remote, command: """docker login --username ${dockerRegistryAccountName} --password '${dockerRegistryAccountPassword}'"""
                        sshCommand remote: remote, command: """docker pull registry.hub.docker.com/hoangtan250997/kafka_consumer"""
                        sshCommand remote: remote, command: """docker run -d --name ${CONTAINER_NAME} \
					-e TZ=Asia/Ho_Chi_Minh\
                    -p ${SPRING_BOOT_PORT}:8080 \
                    -p ${SAYHI_SOCKET_SERVER_PORT}:4001\
                                       -e SPRING_APPLICATION_JSON='{
                        "topic.name": "${TOPIC}", \
                        "spring.kafka.bootstrap-servers": "${KAFKA}"}' \
					--restart unless-stopped \
					registry.hub.docker.com/hoangtan250997/kafka_consumer"""
                    }
        }

    }

}


