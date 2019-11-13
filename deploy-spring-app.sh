#!/bin/bash

set -euo pipefail

source azure-kubernetes-service/create-container-registry.sh

echo 'building application'

mvn -f spring-app verify

echo 'building docker image'

az acr build --registry $ACR_NAME \
  --image $ACR_NAME.azurecr.io/sqs-to-postgresql:latest \
  spring-app/target/assembly

echo 'deploying application'

echo "Getting PostgreSQL endpoint"
server_fqdn=$(az postgres server show -g $RESOURCE_GROUP -n $POSTGRESQL_SERVER_NAME --query fullyQualifiedDomainName -o tsv)
jdbc_url="jdbc:postgresql://$server_fqdn/$POSTGRESQL_DATABASE_NAME"

#"helm upgrade --install" is the idempotent version of "helm install --name"
helm --kube-context $AKS_CLUSTER upgrade --install --recreate-pods spring-app spring-app/helm \
  --set image=$ACR_NAME.azurecr.io/sqs-to-postgresql \
  --set imageTag=latest \
  --set jdbc.url="$jdbc_url" \
  --set jdbc.username="serveradmin@$POSTGRESQL_SERVER_NAME" \
  --set jdbc.password="$POSTGRESQL_ADMIN_PASS" \
  --set sqs.queue="http://sqs:9324/queue/default" \

