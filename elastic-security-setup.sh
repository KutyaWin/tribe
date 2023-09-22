#!/bin/bash

ELASTIC_PASSWORD=$(grep ELASTIC_PASSWORD .env | cut -d "=" -f2)

docker compose exec dev_elasticsearch sh -c \
"cd /usr/share/elasticsearch/bin && printf '%s\n' y $ELASTIC_PASSWORD $ELASTIC_PASSWORD |./elasticsearch-reset-password -u kibana_system -i"