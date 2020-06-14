#!/bin/bash

kubectl delete deployment notification
kubectl create -f notification-deployment.yaml
