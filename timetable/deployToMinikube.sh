#!/bin/bash

/usr/local/java/intelliJ/plugins/maven/lib/maven3/bin/mvn package -Dmaven.test.skip=true -Dquarkus.kubernetes.deploy=true
