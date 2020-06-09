#!/bin/bash

kubectl create -f jaeger-configuration.yaml
kubectl create -f jaeger-deployment.yaml
kubectl create -f jaeger-service-spring.yaml
kubectl create -f jaeger-ui-service.yaml

read -p "Press enter to continue"