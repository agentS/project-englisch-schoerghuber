#!/bin/bash

kubectl delete -f postgresql-configuration.yaml
kubectl delete -f postgresql-storage.yaml
kubectl delete -f postgresql-deployment.yaml
kubectl delete -f postgresql-service.yaml
