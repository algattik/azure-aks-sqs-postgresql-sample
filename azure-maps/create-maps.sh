#!/bin/bash

set -euo pipefail

sku=S0

echo "deploying Azure Maps"
echo ". name: $MAPS_ACCOUNT"
echo ". SKU: $sku"

# Create a logical server in the resource group
az maps account create \
    --name $MAPS_ACCOUNT \
    --resource-group $RESOURCE_GROUP \
    --sku S0 \
    --accept-tos \
    -o tsv >> log.txt
