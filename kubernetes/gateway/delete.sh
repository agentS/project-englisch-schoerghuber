#!/bin/bash

kubectl delete -f gateway-deployment.yaml
kubectl delete -f gateway-service.yaml

read -p "Press enter to continue"