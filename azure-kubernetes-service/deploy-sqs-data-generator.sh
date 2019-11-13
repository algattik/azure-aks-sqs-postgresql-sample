#!/bin/bash

echo 'Deploying SQS data generator in AKS'

kubectl apply -f azure-kubernetes-service/data-generator.yaml
