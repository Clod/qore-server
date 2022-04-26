
Las notas del proyecto están en: https://docs.google.com/document/d/10LVrSiR2a6cftdBJ3MsaKB2O4CvpA_hUdYRnam4cXX8/edit?usp=sharing

# Creating an HTTP API with Ktor

This repository is based on the code corresponding to the hands-on lab [Creating HTTP APIs](https://ktor.io/docs/creating-http-apis.html). 

It has been modified to use SSL

The location of the keystore file (named test.jks) con be overriden by defining the environmental variable JKS_PATH

Windows:

    PS> $Env:JKS_PATH="D:\home\Kotlin\ktor-http-api-sample-main\"

Linux:

    $ JKS_PATH=/home/clau (Creo una Shell variable)
    $ export JKS_PATH (Creo el ENV variable)
    $ export GOOGLE_APPLICATION_CREDENTIALS=/home/clau/fb.json

https://stackoverflow.com/questions/12102270/run-java-jar-file-on-a-server-as-background-process
    Para correr el proceso detachado: Parado en /home/clau
    $ nohup ./java -jar xxxxx &


