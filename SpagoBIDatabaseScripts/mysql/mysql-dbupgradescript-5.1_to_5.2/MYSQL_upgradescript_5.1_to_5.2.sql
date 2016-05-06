CREATE TABLE SBI_CACHE_ITEM (
	   SIGNATURE			VARCHAR(100) NOT NULL,
	   NAME					VARCHAR(100) NULL,
	   TABLE_NAME 			VARCHAR(100) NOT NULL,
	   DIMENSION 			NUMERIC NULL,
	   CREATION_DATE 		DATETIME NULL,
	   LAST_USED_DATE 		DATETIME NULL,
       PROPERTIES			TEXT NULL,
	   USER_IN              VARCHAR(100) NOT NULL,
       USER_UP              VARCHAR(100),
       USER_DE              VARCHAR(100),
       TIME_IN              TIMESTAMP NOT NULL,
       TIME_UP              TIMESTAMP NULL DEFAULT NULL,
       TIME_DE              TIMESTAMP NULL DEFAULT NULL,
       SBI_VERSION_IN       VARCHAR(10),
       SBI_VERSION_UP       VARCHAR(10),
       SBI_VERSION_DE       VARCHAR(10),
       META_VERSION         VARCHAR(100),
       ORGANIZATION         VARCHAR(20),
       UNIQUE XAK1SBI_CACHE_ITEM (TABLE_NAME),
	   PRIMARY KEY (SIGNATURE)
) ENGINE=InnoDB;
commit;

CREATE TABLE SBI_CACHE_JOINED_ITEM (
	   ID 					INTEGER  NOT NULL,
	   SIGNATURE			VARCHAR(100) NOT NULL,
	   JOINED_SIGNATURE		VARCHAR(100) NOT NULL,
       USER_IN              VARCHAR(100) NOT NULL,
       USER_UP              VARCHAR(100),
       USER_DE              VARCHAR(100),
       TIME_IN              TIMESTAMP NOT NULL,
       TIME_UP              TIMESTAMP NULL DEFAULT NULL,
       TIME_DE              TIMESTAMP NULL DEFAULT NULL,
       SBI_VERSION_IN       VARCHAR(10),
       SBI_VERSION_UP       VARCHAR(10),
       SBI_VERSION_DE       VARCHAR(10),
       META_VERSION         VARCHAR(100),
       ORGANIZATION         VARCHAR(20),
       UNIQUE XAK1SBI_CACHE_JOINED_ITEM (SIGNATURE, JOINED_SIGNATURE),
	   PRIMARY KEY (ID)
) ENGINE=InnoDB;
commit;

ALTER TABLE SBI_CACHE_JOINED_ITEM  ADD CONSTRAINT FK_SBI_CACHE_JOINED_ITEM_1 FOREIGN KEY ( SIGNATURE ) REFERENCES  SBI_CACHE_ITEM  ( SIGNATURE ) ON DELETE NO ACTION ON UPDATE CASCADE;
commit;
ALTER TABLE SBI_CACHE_JOINED_ITEM  ADD CONSTRAINT FK_SBI_CACHE_JOINED_ITEM_2 FOREIGN KEY ( JOINED_SIGNATURE ) REFERENCES  SBI_CACHE_ITEM  ( SIGNATURE ) ON DELETE CASCADE ON UPDATE CASCADE;
commit;

ALTER TABLE SBI_META_MODELS
	ADD COLUMN `MODEL_LOCKED` TINYINT(1) NULL DEFAULT NULL AFTER `DESCR`,
	ADD COLUMN `MODEL_LOCKER` VARCHAR(100) NULL DEFAULT NULL AFTER `MODEL_LOCKED`;
UPDATE SBI_META_MODELS SET MODEL_LOCKED = false WHERE MODEL_LOCKED IS NULL;

-- Date Range
ALTER TABLE SBI_PARUSE 
	ADD COLUMN OPTIONS VARCHAR(4000) NULL DEFAULT NULL AFTER ORGANIZATION;
	
ALTER TABLE SBI_THRESHOLD_VALUE CHANGE POSITION  KPI_POSITION  INT ;

-- Version number
INSERT INTO SBI_CONFIG ( ID, LABEL, NAME, DESCRIPTION, IS_ACTIVE, VALUE_CHECK, VALUE_TYPE_ID, CATEGORY, USER_IN, TIME_IN) VALUES 
((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_CONFIG'), 
'SPAGOBI.SPAGOBI_VERSION_NUMBER', 'SPAGOBI.SPAGOBI_VERSION_NUMBER', 
'SpagoBI version number', true, '5.2.0',
(select VALUE_ID from SBI_DOMAINS where VALUE_CD = 'STRING' AND DOMAIN_CD = 'PAR_TYPE'), 
'GENERIC_CONFIGURATION', 'biadmin', current_timestamp);
update hibernate_sequences set next_val = next_val+1 where sequence_name = 'SBI_CONFIG';
commit;


--  add mail subject to alarms
 ALTER TABLE SBI_ALARM ADD COLUMN MAIL_SUBJ VARCHAR(256);
  ALTER TABLE SBI_ALARM MODIFY COLUMN URL VARCHAR(256);
