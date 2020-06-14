#!/bin/bash

kubectl delete -f mongodb-configuration.yaml
kubectl delete -f mongodb-storage.yaml
kubectl delete -f mongodb-deployment.yaml
kubectl delete -f mongodb-service.yaml
