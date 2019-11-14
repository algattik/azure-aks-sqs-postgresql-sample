#!/bin/bash

set -euo pipefail

echo 'building application'

mvn -f spring-app verify

echo 'building docker image'

az acr build --registry $ACR_NAME \
  --image $ACR_NAME.azurecr.io/sqs-to-postgresql:latest \
  spring-app/target/assembly

echo 'creating Flyway image'
az acr build --registry $ACR_NAME \
  --image $ACR_NAME.azurecr.io/flyway-migration:latest \
  spring-app/src/main/flyway

echo 'deploying application'

echo "Getting PostgreSQL endpoint"
server_fqdn=$(az postgres server show -g $RESOURCE_GROUP -n $POSTGRESQL_SERVER_NAME --query fullyQualifiedDomainName -o tsv)
jdbc_url="jdbc:postgresql://$server_fqdn/$POSTGRESQL_DATABASE_NAME"

set -x

#"helm upgrade --install" is the idempotent version of "helm install --name"
helm --kube-context $AKS_CLUSTER upgrade --install --recreate-pods spring-app spring-app/helm \
  --set image=$ACR_NAME.azurecr.io/sqs-to-postgresql \
  --set imageTag=latest \
  --set jdbc.url="$jdbc_url" \
  --set jdbc.username="serveradmin@$POSTGRESQL_SERVER_NAME" \
  --set postgres.secrets.password="$POSTGRESQL_ADMIN_PASS" \
  --set aws.access_key_id="unused" \
  --set aws.secrets.secret_access_key="unused" \
  --set sqs.queue="http://sqs:9324/queue/default" \
  --set flyway.image=$ACR_NAME.azurecr.io/flyway-migration \
  --set flyway.imageTag=latest \

