#!/bin/bash

kubectl delete -f jaeger-configuration.yaml
kubectl delete -f jaeger-deployment.yaml
kubectl delete -f jaeger-service-spring.yaml
kubectl delete -f jaeger-ui-service.yaml

read -p "Press enter to continue"