#!/bin/bash

eval $(minikube -p minikube docker-env)
"/c/Users/Daniel Englisch/AppData/Local/JetBrains/Toolbox/apps/IDEA-U/ch-0/201.7223.91/plugins/maven/lib/maven3/bin/mvn" package -Dmaven.test.skip=true -Dquarkus.kubernetes.deploy=true
read -p "Press enter to continue"