#!/bin/bash

echo 'Deploying SQS emulator in AKS'

kubectl apply -f azure-kubernetes-service/sqs.yaml
