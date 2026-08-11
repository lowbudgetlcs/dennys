START TRANSACTION;
SET search_path = dennys;

-- V012 created `games` fresh instead of renaming it, so it is owned by whoever ran the DDL rather
-- than the role the application connects as. Align it with the table V012 renamed, which by
-- construction still carries the schema's original ownership, so this holds in every environment
-- without naming a role.
DO $$
DECLARE
  app_role name;
BEGIN
  SELECT pg_get_userbyid(relowner) INTO app_role
    FROM pg_class WHERE oid = 'dennys.tournament_codes'::regclass;

  EXECUTE format('ALTER TABLE dennys.games OWNER TO %I', app_role);

  IF current_user <> app_role THEN
    EXECUTE format(
      'ALTER DEFAULT PRIVILEGES IN SCHEMA dennys GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO %I',
      app_role);
    EXECUTE format(
      'ALTER DEFAULT PRIVILEGES IN SCHEMA dennys GRANT USAGE, SELECT ON SEQUENCES TO %I',
      app_role);
  END IF;
END $$;

COMMIT;
