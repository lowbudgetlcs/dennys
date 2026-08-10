#!/usr/bin/env bash
# Rehearses the production upgrade path: builds a database at V010 with no Flyway history,
# seeds it to production's shape, then baselines and migrates exactly as CI does against prod.
#
#   ./scripts/rehearsal/run.sh prod-shape    # migration must succeed and satisfy the assertions
#   ./scripts/rehearsal/run.sh fk-tripwire   # migration must fail on the game_results FK
#
# Connection defaults to a localhost Postgres; override with PGHOST/PGPORT/PGUSER/PGPASSWORD/PGDATABASE.
set -euo pipefail

LEG="${1:-prod-shape}"
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

PGHOST="${PGHOST:-127.0.0.1}"
PGPORT="${PGPORT:-5432}"
PGUSER="${PGUSER:-postgres}"
PGPASSWORD="${PGPASSWORD:-postgres}"
PGDATABASE="${PGDATABASE:-postgres}"

FLYWAY_IMAGE="${FLYWAY_IMAGE:-flyway/flyway:11-alpine}"
PSQL_IMAGE="${PSQL_IMAGE:-postgres:15-alpine}"

psql_run() {
  docker run --rm --network host \
    -e PGPASSWORD="$PGPASSWORD" \
    -v "$REPO_ROOT:/repo:ro" \
    "$PSQL_IMAGE" \
    psql -v ON_ERROR_STOP=1 -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "$PGDATABASE" "$@"
}

flyway_run() {
  FLYWAY_URL="jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}" \
  FLYWAY_USER="$PGUSER" \
  FLYWAY_PASSWORD="$PGPASSWORD" \
  FLYWAY_IMAGE="$FLYWAY_IMAGE" \
    "$REPO_ROOT/scripts/flyway.sh" "$@"
}

echo "==> Building the pre-1.4.0 schema (V001-V010, no Flyway history)"
for f in "$REPO_ROOT"/src/migrations/sql/V0{01,02,03,04,05,06,07,08,09,10}__*.sql; do
  echo "    $(basename "$f")"
  psql_run -q -f "/repo/src/migrations/sql/$(basename "$f")"
done

echo "==> Seeding production's shape"
psql_run -q -f /repo/scripts/rehearsal/seed_prod_shape.sql

if [ "$LEG" = "fk-tripwire" ]; then
  echo "==> Adding the game_results row that must abort V012"
  psql_run -q -f /repo/scripts/rehearsal/seed_fk_tripwire.sql
fi

echo "==> Baselining the ledger at V010"
flyway_run baseline -baselineVersion=010

echo "==> Applying pending migrations"
if [ "$LEG" = "fk-tripwire" ]; then
  if flyway_run migrate; then
    echo "FAIL: V012 succeeded with a non-empty game_results; the FK tripwire is not working" >&2
    exit 1
  fi
  echo "==> Migration failed as required"
  exit 0
fi

flyway_run migrate
flyway_run info

echo "==> Asserting the post-migration schema"
psql_run -q -f /repo/scripts/rehearsal/assert_post_migration.sql

echo "==> Rehearsal passed"
