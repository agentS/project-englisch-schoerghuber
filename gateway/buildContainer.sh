#!/bin/bash

eval $(minikube -p minikube docker-env)
docker build --file Dockerfile --tag gateway:0 .

