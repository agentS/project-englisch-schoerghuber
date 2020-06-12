#!/bin/bash

kubectl create configmap booking-configuration --from-file=application.properties
kubectl create -f booking-deployment.yaml
kubectl create -f booking-service.yaml
