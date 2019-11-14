#!/bin/bash

set -euo pipefail

echo "deploying azure postgres"
echo ". server: $POSTGRESQL_SERVER_NAME"
echo ". database: $POSTGRESQL_DATABASE_NAME"

# Create a logical server in the resource group
az postgres server create \
    --name $POSTGRESQL_SERVER_NAME \
    --resource-group $RESOURCE_GROUP \
    --admin-user serveradmin \
    --admin-password "$POSTGRESQL_ADMIN_PASS" \
    --sku-name "$POSTGRESQL_SKU" \
    -o tsv >> log.txt

echo "Enabling access from VNET service endpoint"

az postgres server vnet-rule create \
    --resource-group $RESOURCE_GROUP \
    --server $POSTGRESQL_SERVER_NAME \
    -n kubernetes-subnet \
    --vnet-name $VNET_NAME \
    --subnet kubernetes-subnet \
    -o tsv >> log.txt

echo "deploying PostgreSQL db"
az postgres db create --resource-group "$RESOURCE_GROUP" \
    --server $POSTGRESQL_SERVER_NAME \
    --name $POSTGRESQL_DATABASE_NAME \
    -o tsv >> log.txt
