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

echo 'Getting PostgreSQL endpoint'

server_fqdn=$(az postgres server show -g $RESOURCE_GROUP -n $POSTGRESQL_SERVER_NAME --query fullyQualifiedDomainName -o tsv)
jdbc_url="jdbc:postgresql://$server_fqdn/$POSTGRESQL_DATABASE_NAME"

echo 'Getting Azure Maps credentials'

mapsClientId=$(az maps account show \
    --name $MAPS_ACCOUNT \
    --resource-group $RESOURCE_GROUP \
    --query 'properties."x-ms-client-id"' \
    -o tsv)

mapsPrimaryKey=$(az maps account keys list \
    --name $MAPS_ACCOUNT \
    --resource-group $RESOURCE_GROUP \
    --query primaryKey \
    -o tsv)

echo 'deploying application'

#"helm upgrade --install" is the idempotent version of "helm install --name"
helm --kube-context $AKS_CLUSTER upgrade --install --recreate-pods spring-app spring-app/helm \
  --set image=$ACR_NAME.azurecr.io/sqs-to-postgresql \
  --set imageTag=latest \
  --set jdbc.url="$jdbc_url" \
  --set jdbc.username="serveradmin@$POSTGRESQL_SERVER_NAME" \
  --set postgres.secrets.password="$POSTGRESQL_ADMIN_PASS" \
  --set aws.access_key_id="unused" \
  --set aws.secrets.secret_access_key="unused" \
  --set azure.secrets.maps_clientId="$mapsClientId" \
  --set azure.secrets.maps_subscriptionKey="$mapsPrimaryKey" \
  --set sqs.queue="http://sqs:9324/queue/default" \
  --set flyway.image=$ACR_NAME.azurecr.io/flyway-migration \
  --set flyway.imageTag=latest \

