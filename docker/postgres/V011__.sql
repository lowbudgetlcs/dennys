CREATE TABLE command_role_permissions(
  id SERIAL PRIMARY KEY,
  name VARCHAR NOT NULL,
  role_id BIGINT NOT NULL
);

CREATE TABLE command_channel_permissions (
  id SERIAL PRIMARY KEY,
  name VARCHAR NOT NULL,
  channel_id BIGINT NOT NULL
);