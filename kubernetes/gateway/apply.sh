#!/bin/bash

kubectl apply -f gateway-deployment.yaml
kubectl apply -f gateway-service.yaml

read -p "Press enter to continue"