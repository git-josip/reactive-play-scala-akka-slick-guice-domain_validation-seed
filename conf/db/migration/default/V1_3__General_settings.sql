-- create general_settings table
CREATE TABLE public.general_settings
(
  id                               BIGSERIAL   PRIMARY KEY NOT NULL,
  company_data__country            VARCHAR(80)             NOT NULL,
  company_data__street             VARCHAR(80)             NOT NULL,
  company_data__city               VARCHAR(80)             NOT NULL,
  company_data__zip                VARCHAR(80)             NOT NULL,
  company_data__phone              VARCHAR(80)             NOT NULL,
  company_data__mobile             VARCHAR(80)             NOT NULL,
  company_data__oib                VARCHAR(80)             NOT NULL,
  business_data__profit_percentage SMALLINT                NOT NULL
);

-- initialize general_settings singleton row
INSERT INTO public.general_settings (
  id, company_data__country, company_data__street, company_data__city, company_data__zip,
  company_data__phone, company_data__mobile, company_data__oib, business_data__profit_percentage
)
VALUES (0, 'Croatia', 'Street example', 'Split', '21000', '+38521', '+38598', '0000000000', 100);