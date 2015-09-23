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
  FOREIGN KEY (user_id) REFERENCES users(id)
);
