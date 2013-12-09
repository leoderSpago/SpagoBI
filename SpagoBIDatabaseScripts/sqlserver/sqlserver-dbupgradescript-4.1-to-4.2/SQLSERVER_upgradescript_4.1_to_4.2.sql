INSERT INTO SBI_USER_FUNC (USER_FUNCT_ID, NAME, DESCRIPTION, USER_IN, TIME_IN)
VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_USER_FUNC'),
'TenantManagement','TenantManagement', 'server', CURRENT_TIMESTAMP);
UPDATE hibernate_sequences SET next_val = next_val+1 WHERE sequence_name = 'SBI_USER_FUNC';

INSERT INTO SBI_ROLE_TYPE_USER_FUNC (ROLE_TYPE_ID, USER_FUNCT_ID)
VALUES ((SELECT VALUE_ID FROM SBI_DOMAINS WHERE VALUE_CD = 'ADMIN' AND DOMAIN_CD = 'ROLE_TYPE'),
(SELECT USER_FUNCT_ID FROM SBI_USER_FUNC WHERE NAME = 'TenantManagement'));
COMMIT;

ALTER TABLE SBI_ORGANIZATIONS ADD COLUMN THEME VARCHAR(100) NULL DEFAULT 'SPAGOBI.THEMES.THEME.default';
ALTER TABLE SBI_USER ADD COLUMN IS_SUPERADMIN TINYINT(1) DEFAULT 0;

UPDATE SBI_USER us SET us.IS_SUPERADMIN = 1 WHERE us.ID IN(
	SELECT ur.ID FROM SBI_EXT_USER_ROLES ur WHERE ur.EXT_ROLE_ID IN( 
		SELECT role.EXT_ROLE_ID FROM SBI_EXT_ROLES role WHERE role.ROLE_TYPE_CD = 'ADMIN'
	)
);

CREATE TABLE SBI_ORGANIZATION_ENGINE (
  ENGINE_ID int(11) NOT NULL,
  ORGANIZATION_ID int(11) NOT NULL,
  CREATION_DATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  LAST_CHANGE_DATE timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  USER_IN varchar(100) NOT NULL,
  USER_UP varchar(100) DEFAULT NULL,
  USER_DE varchar(100) DEFAULT NULL,
  TIME_IN timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  TIME_UP timestamp NULL DEFAULT NULL,
  TIME_DE timestamp NULL DEFAULT NULL,
  SBI_VERSION_IN varchar(10) DEFAULT NULL,
  SBI_VERSION_UP varchar(10) DEFAULT NULL,
  SBI_VERSION_DE varchar(10) DEFAULT NULL,
  META_VERSION varchar(100) DEFAULT NULL,
  ORGANIZATION varchar(20) DEFAULT NULL,
  PRIMARY KEY (ENGINE_ID,ORGANIZATION_ID ),
  CONSTRAINT FK_ENGINE_1 FOREIGN KEY (ENGINE_ID) REFERENCES SBI_ENGINES (ENGINE_ID) ON DELETE NO ACTION ON UPDATE NO ACTION,

  CONSTRAINT FK_ORGANIZATION_1 FOREIGN KEY (ORGANIZATION_ID) REFERENCES SBI_ORGANIZATIONS (ID) ON DELETE NO ACTION ON UPDATE NO ACTION
) ;

CREATE TABLE SBI_ORGANIZATION_DATASOURCE (
  DATASOURCE_ID int(11) NOT NULL,
  ORGANIZATION_ID int(11) NOT NULL,
  CREATION_DATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  LAST_CHANGE_DATE timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  USER_IN varchar(100) NOT NULL,
  USER_UP varchar(100) DEFAULT NULL,
  USER_DE varchar(100) DEFAULT NULL,
  TIME_IN timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  TIME_UP timestamp NULL DEFAULT NULL,
  TIME_DE timestamp NULL DEFAULT NULL,
  SBI_VERSION_IN varchar(10) DEFAULT NULL,
  SBI_VERSION_UP varchar(10) DEFAULT NULL,
  SBI_VERSION_DE varchar(10) DEFAULT NULL,
  META_VERSION varchar(100) DEFAULT NULL,
  ORGANIZATION varchar(20) DEFAULT NULL,
  PRIMARY KEY (DATASOURCE_ID,ORGANIZATION_ID ),
  CONSTRAINT FK_DATASOURCE_2 FOREIGN KEY (DATASOURCE_ID) REFERENCES SBI_DATA_SOURCE (DS_ID) ON DELETE NO ACTION ON UPDATE NO ACTION,

  CONSTRAINT FK_ORGANIZATION_2 FOREIGN KEY (ORGANIZATION_ID) REFERENCES SBI_ORGANIZATIONS (ID) ON DELETE NO ACTION ON UPDATE NO ACTION
) ;

INSERT INTO SBI_ORGANIZATION_DATASOURCE (DATASOURCE_ID, ORGANIZATION_ID, CREATION_DATE, LAST_CHANGE_DATE, USER_IN, TIME_IN, SBI_VERSION_IN)
  SELECT ds.ds_id, org.id, SYSDATE(), SYSDATE(), "server", SYSDATE(), "4.1"
  FROM SBI_DATA_SOURCE ds, SBI_ORGANIZATIONS org WHERE ds.organization = org.name;
  
 INSERT INTO SBI_ORGANIZATION_ENGINE (ENGINE_ID, ORGANIZATION_ID, CREATION_DATE, LAST_CHANGE_DATE, USER_IN, TIME_IN, SBI_VERSION_IN)
  SELECT eng.engine_id, org.id, SYSDATE(), SYSDATE(), "server", SYSDATE(), "4.1"
  FROM SBI_ENGINES eng, SBI_ORGANIZATIONS org WHERE eng.organization = org.name;
COMMIT;

UPDATE SBI_OBJECTS r JOIN (
SELECT B.ENGINE_ID AS OK, A.ENGINE_ID AS KO
FROM
(SELECT ENGINE_ID, LABEL, ORGANIZATION FROM SBI_ENGINES WHERE ORGANIZATION !='SPAGOBI') A,
(SELECT ENGINE_ID, LABEL, ORGANIZATION FROM SBI_ENGINES WHERE ORGANIZATION ='SPAGOBI') B
WHERE A.LABEL=B.LABEL
) t ON (r.ENGINE_ID = t.KO)
SET r.ENGINE_ID = t.OK
WHERE r.ENGINE_ID = t.KO;
(SELECT ENGINE_ID, LABEL, ORGANIZATION FROM SBI_ENGINES WHERE ORGANIZATION !='SPAGOBI');

DELETE FROM SBI_EXPORTERS where engine_id IN (SELECT ENGINE_ID FROM SBI_ENGINES WHERE ORGANIZATION !='SPAGOBI');
COMMIT;

DELETE from SBI_ENGINES where organization !='SPAGOBI';
COMMIT;