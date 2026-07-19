ALTER TABLE device
    ADD COLUMN hms_token varchar(300) NULL;

ALTER TABLE device
    ADD COLUMN push_type varchar(10) NULL DEFAULT 'FCM';

UPDATE device
SET push_type = 'FCM'
WHERE push_type IS NULL OR push_type = '';
