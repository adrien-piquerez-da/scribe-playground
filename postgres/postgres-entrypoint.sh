#!/bin/bash
# Copyright (c) 2024 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
# SPDX-License-Identifier: Apache-2.0

set -Eeo pipefail

docker-ensure-initdb.sh
source docker-entrypoint.sh

execute() {
  psql -v ON_ERROR_STOP=1 --username canton-admin --dbname postgres "$@"
}

docker_temp_server_start

execute -tc "SELECT 1 FROM pg_database WHERE datname = 'participant'" | grep -q 1 || \
  { execute -c "CREATE DATABASE \"participant\"" && echo "Database 'participant' created."; }

execute -tc "SELECT 1 FROM pg_database WHERE datname = 'sequencer'" | grep -q 1 || \
  { execute -c "CREATE DATABASE \"sequencer\"" && echo "Database 'sequencer' created."; }

execute -tc "SELECT 1 FROM pg_database WHERE datname = 'driver'" | grep -q 1 || \
  { execute -c "CREATE DATABASE \"driver\"" && echo "Database 'driver' created."; }

execute -tc "SELECT 1 FROM pg_database WHERE datname = 'mediator'" | grep -q 1 || \
  { execute -c "CREATE DATABASE \"mediator\"" && echo "Database 'mediator' created."; }

execute -tc "SELECT 1 FROM pg_database WHERE datname = 'pqs'" | grep -q 1 || \
  { execute -c "CREATE DATABASE \"pqs\"" && echo "Database 'pqs' created."; }

docker_temp_server_stop

# run the supplied command
exec "$@"
