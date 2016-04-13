CREATE TABLE public.app_user
(
    id                BIGSERIAL     PRIMARY KEY NOT NULL,
    external_id       VARCHAR(255)              NOT NULL,
    created_on        TIMESTAMP                 NOT NULL,
    modified_on       TIMESTAMP                 NOT NULL,
    first_name        VARCHAR(255),
    last_name         VARCHAR(255),
    email             VARCHAR(255)               NOT NULL,
    password          VARCHAR(255)               NOT NULL,
    date_of_birth     DATE                       NOT NULL,
    gender            VARCHAR(255)               NOT NULL,
    status            VARCHAR(255)               NOT NULL,
    role              VARCHAR(255)               NOT NULL,
    contact__address  VARCHAR(255),
    contact__city     VARCHAR(255),
    contact__zip      VARCHAR(255),
    contact__phone    VARCHAR(255),
    height            INTEGER,
    weight            INTEGER,

    UNIQUE(external_id),
    UNIQUE(email)
);
CREATE INDEX user__created_on_idx ON "app_user" (created_on);
CREATE INDEX user__modified_on_idx ON "app_user" (modified_on);
INSERT INTO app_user(id, external_id, created_on, modified_on, first_name, last_name, email, password, date_of_birth, gender, status, role)
VALUES(
  0,
  'system_user_external_id_123456',
  NOW(),
  NOW(),
  'SYSTEM',
  'USER',
  'system.user@system.com',
  '84ab0645a080468afebd4df2f374dc685d1cd8bfefb000805cf630a31cf6941c',
  NOW(),
  'M',
  'NOT_VERIFIED',
  'SYSTEM_USER'
);