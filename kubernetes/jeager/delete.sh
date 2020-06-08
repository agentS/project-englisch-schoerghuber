#!/bin/bash

kubectl delete -f jaeger-configuration.yaml
kubectl delete -f jaeger-deployment.yaml
kubectl delete -f jaeger-service.yaml
kubectl delete -f jaeger-ui-service.yaml
