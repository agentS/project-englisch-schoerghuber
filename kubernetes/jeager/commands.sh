#!/bin/bash

kubectl create -f jaeger-configuration.yaml
kubectl create -f jaeger-deployment.yaml
kubectl create -f jaeger-service.yaml
