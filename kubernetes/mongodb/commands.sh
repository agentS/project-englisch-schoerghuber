#!/bin/bash

kubectl create -f mongodb-configuration.yaml
kubectl create -f mongodb-storage.yaml
kubectl create -f mongodb-deployment.yaml
kubectl create -f mongodb-service.yaml
