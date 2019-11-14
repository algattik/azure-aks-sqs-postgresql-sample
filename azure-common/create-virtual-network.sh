#!/bin/bash

# Strict mode, fail on any error
set -euo pipefail

echo 'creating virtual network'
echo ". name: $VNET_NAME"

az group create -n $RESOURCE_GROUP -l $LOCATION --tags auto_generated=1 \
-o tsv >> log.txt

if ! az network vnet show -n $VNET_NAME -g $RESOURCE_GROUP -o none 2>/dev/null; then
  az network vnet create -n $VNET_NAME -g $RESOURCE_GROUP \
    --address-prefix 10.0.0.0/16 \
    -o tsv >> log.txt
fi

if ! az network vnet subnet show -g $RESOURCE_GROUP --vnet-name $VNET_NAME -n kubernetes-subnet -o none 2>/dev/null; then
  az network vnet subnet create -g $RESOURCE_GROUP --vnet-name $VNET_NAME \
    -n kubernetes-subnet --address-prefixes 10.0.2.0/24 \
    --service-endpoints Microsoft.SQL \
    -o tsv >> log.txt
fi
