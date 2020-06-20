#!/bin/bash

eval $(minikube -p minikube docker-env)
/usr/local/java/intelliJ/plugins/maven/lib/maven3/bin/mvn clean package -Dmaven.test.skip=true
