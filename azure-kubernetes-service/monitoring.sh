#!/bin/bash

echo "Getting PostgreSQL endpoint"
server_fqdn=$(az postgres server show -g $RESOURCE_GROUP -n $POSTGRESQL_SERVER_NAME --query fullyQualifiedDomainName -o tsv)

echo 'Starting monitoring'

while true; do
  kubectl --context $AKS_CLUSTER run --generator=run-pod/v1 -it --rm --image=postgres --env PGPASSWORD=Strong_Passw0rd! --command psql-monitoring-$(uuidgen | tr A-Z a-z) -- psql --host=$server_fqdn --username=serveradmin@$POSTGRESQL_SERVER_NAME --dbname=$POSTGRESQL_DATABASE_NAME -t -P pager=off -c "SELECT 'Count of events in the database', count(*) FROM location"
  echo "Press Ctrl-C to exit."
done
