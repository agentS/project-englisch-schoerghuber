#!/bin/bash

kubectl create -f gateway-deployment.yaml
kubectl create -f gateway-service.yaml

read -p "Press enter to continue"