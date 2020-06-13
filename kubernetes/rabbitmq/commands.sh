#!/bin/bash
kubectl create -f rabbitmq-deployment.yaml
kubectl create -f rabbitmq-service.yaml
kubectl create -f rabbitmq-ui-node-port-service.yaml
