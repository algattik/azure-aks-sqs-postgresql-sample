#!/bin/bash

set -euo pipefail

on_error() {
    set +e
    echo "There was an error, execution halted" >&2
    echo "Error at line $1"
    exit 1
}

trap 'on_error $LINENO' ERR

export PREFIX=''
export LOCATION="eastus"
export STEPS="CIDPTM"

usage() {
    echo "Usage: $0 -d <deployment-name> [-s <steps>]  [-l <location>]"
    echo "-s: specify which steps should be executed. Default=$STEPS"
    echo "    Possible values:"
    echo "      C=COMMON"
    echo "      I=INFRASTRUCTURE"
    echo "      D=DATABASE"
    echo "      P=PROCESSING"
    echo "      T=TEST clients"
    echo "      M=MONITORING"
    echo "-l: where to create the resources. Default=$LOCATION"
    exit 1;
}

# Initialize parameters specified from command line
while getopts ":d:s:l:" arg; do
	case "${arg}" in
		d)
			PREFIX=${OPTARG}
			;;
		s)
			STEPS=${OPTARG}
			;;
		l)
			LOCATION=${OPTARG}
			;;
		esac
done
shift $((OPTIND-1))

if [[ -z "$PREFIX" ]]; then
	echo "Enter a name for this deployment."
	usage
fi

export POSTGRESQL_SKU=GP_Gen5_2
export AKS_VM_SIZE=Standard_D2s_v3
export AKS_KUBERNETES_VERSION=1.14.7
export AKS_NODES=3

export RESOURCE_GROUP=$PREFIX

# remove log.txt if exists
rm -f log.txt

echo "Checking pre-requisites..."

source assert/has-local-kubectl.sh
source assert/has-local-az.sh
source assert/has-local-jq.sh
source assert/has-local-helm.sh

echo
echo "SQS Message Processing with Spring Cloud, AKS and PostgreSQL"
echo "============================================================"
echo

echo "Steps to be executed: $STEPS"
echo

echo "Configuration:"
echo ". Resource Group   => $RESOURCE_GROUP"
echo ". Region           => $LOCATION"
echo ". AKS              => Node Count: $AKS_NODES, VM: $AKS_VM_SIZE"
echo ". Azure PostgreSQL => SKU: $POSTGRESQL_SKU"
echo

echo "Deployment started..."
echo

echo "***** [C] Setting up COMMON resources"

    export AKS_CLUSTER=$PREFIX"aks"
    export SERVICE_PRINCIPAL_KV_NAME=$AKS_CLUSTER
    export SERVICE_PRINCIPAL_KEYVAULT=$PREFIX"spkv"
    export ACR_NAME=$PREFIX"acr"
    export AZURE_STORAGE_ACCOUNT=$PREFIX"storage"
    export VNET_NAME=$PREFIX"-vnet"

    RUN=`echo $STEPS | grep C -o || true`
    if [ ! -z "$RUN" ]; then
        source azure-common/create-resource-group.sh
        source azure-common/create-service-principal.sh
        source azure-common/create-virtual-network.sh
        source azure-storage/create-storage-account.sh
    fi
echo

echo "***** [I] Setting up INFRASTRUCTURE"

    source azure-monitor/generate-workspace-name.sh

    RUN=`echo $STEPS | grep I -o || true`
    if [ ! -z "$RUN" ]; then
        source azure-monitor/create-log-analytics.sh
        source azure-kubernetes-service/create-kubernetes-service.sh
        source azure-kubernetes-service/create-container-registry.sh
        source azure-kubernetes-service/deploy-sqs-emulator.sh
    fi
echo

echo "***** [D] Setting up DATABASE"

    export POSTGRESQL_SERVER_NAME=$PREFIX"sql"
    export POSTGRESQL_DATABASE_NAME="locationdb"
    export POSTGRESQL_ADMIN_PASS="Strong_Passw0rd!"

    RUN=`echo $STEPS | grep D -o || true`
    if [ ! -z "$RUN" ]; then
        source azure-postgresql/create-postgresql.sh
    fi
echo

echo "***** [P] Setting up PROCESSING"

    export MAPS_ACCOUNT=$PREFIX"maps"

    RUN=`echo $STEPS | grep P -o || true`
    if [ ! -z "$RUN" ]; then
        source ./azure-maps/create-maps.sh
        source ./deploy-spring-app.sh
    fi
echo

echo "***** [T] Starting up TEST clients"

    RUN=`echo $STEPS | grep T -o || true`
    if [ ! -z "$RUN" ]; then
        source azure-kubernetes-service/deploy-sqs-data-generator.sh
    fi
echo

echo "***** [M] Starting up MONITORING"

    RUN=`echo $STEPS | grep M -o || true`
    if [ ! -z "$RUN" ]; then
        source azure-kubernetes-service/monitoring.sh
    fi
echo


echo "***** Done"
