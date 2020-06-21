#!/bin/bash

yarn build
eval $(minikube -p minikube docker-env)
docker build -t frontend:0 .
