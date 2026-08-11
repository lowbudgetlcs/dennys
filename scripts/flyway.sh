#!/usr/bin/env bash
# Single entry point for every Flyway invocation, so CI and a laptop run byte-identical commands.
#
#   FLYWAY_URL=... FLYWAY_USER=... FLYWAY_PASSWORD=... scripts/flyway.sh info
#   scripts/flyway.sh baseline -baselineVersion=010
#   scripts/flyway.sh migrate
#
# migrate refuses to run against a database that has no baseline. V001__LOCKED.sql opens with
# DROP SCHEMA IF EXISTS dennys CASCADE, so a run that starts from an empty ledger destroys
# production. baselineOnMigrate is deliberately left off; baselining is an explicit act.
set -euo pipefail

COMMAND="${1:?usage: flyway.sh <command> [args...]}"
shift

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
FLYWAY_IMAGE="${FLYWAY_IMAGE:-flyway/flyway:11-alpine}"
MIGRATIONS_DIR="${MIGRATIONS_DIR:-$REPO_ROOT/src/migrations/sql}"

: "${FLYWAY_URL:?FLYWAY_URL is not set}"
: "${FLYWAY_USER:?FLYWAY_USER is not set}"
: "${FLYWAY_PASSWORD:?FLYWAY_PASSWORD is not set}"

flyway() {
  docker run --rm --network host \
    -e FLYWAY_URL -e FLYWAY_USER -e FLYWAY_PASSWORD \
    -v "$MIGRATIONS_DIR:/flyway/sql:ro" \
    "$FLYWAY_IMAGE" \
    -locations=filesystem:/flyway/sql \
    -schemas=dennys \
    -cleanDisabled=true \
    -executeInTransaction=false \
    -outOfOrder=false \
    -validateMigrationNaming=true \
    -connectRetries=10 \
    -connectRetriesInterval=5 \
    "$@"
}

if [ "$COMMAND" = "migrate" ]; then
  if ! flyway info -outputType=json | jq -e '[.migrations[] | select(.type == "BASELINE")] | length > 0' >/dev/null; then
    echo "REFUSING TO MIGRATE: no baseline in the schema history." >&2
    echo "Migrating from an empty ledger would replay V001, which drops the dennys schema." >&2
    echo "Run 'flyway.sh baseline -baselineVersion=<current>' first." >&2
    exit 1
  fi
fi

exec_flyway() { flyway "$COMMAND" "$@"; }
exec_flyway "$@"
