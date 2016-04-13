CREATE TABLE public.action_log
(
  action_id      BIGSERIAL PRIMARY KEY NOT NULL,
  user_id        BIGINT       NOT NULL,
  domain_type    VARCHAR(80)  NOT NULL,
  domain_id      BIGINT       NOT NULL,
  action_type    VARCHAR(80)  NOT NULL,
  before         JSON,
  after          JSON,
  created_on     TIMESTAMP    NOT NULL,
  FOREIGN KEY (user_id) REFERENCES "app_user"(id)
);
CREATE INDEX action_log__created_on_idx ON action_log (created_on);
CREATE INDEX action_log__domain_id_idx ON action_log (domain_id);
CREATE INDEX action_log__domain_type_idx ON action_log (domain_type);
CREATE INDEX action_log__user_id_idx ON action_log (user_id);