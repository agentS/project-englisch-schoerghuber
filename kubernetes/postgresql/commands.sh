#!/bin/bash

kubectl create -f postgresql-configuration.yaml
kubectl create -f postgresql-storage.yaml
kubectl create -f postgresql-deployment.yaml
kubectl create -f postgresql-service.yaml
