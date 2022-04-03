[![official JetBrains project](https://jb.gg/badges/official.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)


# Creating an HTTP API with Ktor

This repository is based on the code corresponding to the hands-on lab [Creating HTTP APIs](https://ktor.io/docs/creating-http-apis.html). 

It has been modified to use SSL

The location of the keystore file (named test.jks) con be overriden by defining the environmental variable JKS_PATH

Windows:

    PS> $Env:JKS_PATH="D:\home\Kotlin\ktor-http-api-sample-main\"

Linux:

    $ JKS_PATH=/home/clau (Creo una Shell variable)
    $ export JKS_PATH (Creo el ENV variable)