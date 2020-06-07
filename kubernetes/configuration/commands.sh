#!/bin/bash
kubectl delete configmap timetable-configuration
kubectl create configmap timetable-configuration --from-file=./timetable/application.properties

