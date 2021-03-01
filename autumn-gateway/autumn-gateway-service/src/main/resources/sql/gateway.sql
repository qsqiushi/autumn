CREATE TABLE `config`  (
  `ID` BIGINT NOT NULL,
  `CREATE_BY` BIGINT NULL DEFAULT NULL,
  `CREATE_TIME` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATE_BY` BIGINT NULL DEFAULT NULL,
  `UPDATE_TIME` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `DELETED` TINYINT NOT NULL DEFAULT 0,
  `REMARK` VARCHAR(255) NULL DEFAULT NULL,
  `VERSION` INT NOT NULL DEFAULT 0,
  `STATUS` TINYINT NOT NULL DEFAULT 0,
  `NAME` VARCHAR(255) NULL,
  `CODE` VARCHAR(255) NULL,
  `TYPE` TINYINT NULL COMMENT 'PREDICATE 和 FILTER',
  PRIMARY KEY (`ID`)
);

CREATE TABLE `config_param`  (
  `ID` BIGINT NOT NULL,
  `CREATE_BY` BIGINT NULL DEFAULT NULL,
  `CREATE_TIME` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATE_BY` BIGINT NULL DEFAULT NULL,
  `UPDATE_TIME` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `DELETED` TINYINT NOT NULL DEFAULT 0,
  `REMARK` VARCHAR(255) NULL DEFAULT NULL,
  `VERSION` INT NOT NULL DEFAULT 0,
  `STATUS` TINYINT NOT NULL DEFAULT 0,
  `CODE` VARCHAR(255) NULL,
  `NAME` VARCHAR(255) NULL,
  `CONFIG_ID` BIGINT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE `group`  (
  `ID` BIGINT NOT NULL,
  `CREATE_BY` BIGINT NULL DEFAULT NULL,
  `CREATE_TIME` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATE_BY` BIGINT NULL DEFAULT NULL,
  `UPDATE_TIME` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `DELETED` TINYINT NOT NULL DEFAULT 0,
  `REMARK` VARCHAR(255) NULL DEFAULT NULL,
  `VERSION` INT NOT NULL DEFAULT 0,
  `STATUS` TINYINT NOT NULL DEFAULT 0,
  `NAME` VARCHAR(255) NULL COMMENT '名称',
  `CODE` VARCHAR(255) NOT NULL,
  `DOMAIN` VARCHAR(255) NULL COMMENT '域名',
  PRIMARY KEY (`ID`)
);

CREATE TABLE `resource`  (
  `ID` BIGINT NOT NULL,
  `CREATE_BY` BIGINT NULL DEFAULT NULL,
  `CREATE_TIME` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATE_BY` BIGINT NULL DEFAULT NULL,
  `UPDATE_TIME` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `DELETED` TINYINT NOT NULL DEFAULT 0,
  `REMARK` VARCHAR(255) NULL DEFAULT NULL,
  `VERSION` INT NOT NULL DEFAULT 0,
  `STATUS` TINYINT NOT NULL DEFAULT 0,
  `URI` BIGINT NULL DEFAULT NULL COMMENT '网关URI，LB://TEST',
  `GROUP_ID` VARCHAR(255) NULL DEFAULT NULL COMMENT '组ID',
  PRIMARY KEY (`ID`)
) ENGINE = INNODB CHARACTER SET = UTF8MB4 COLLATE = UTF8MB4_0900_AI_CI ROW_FORMAT = DYNAMIC;

CREATE TABLE `resource_config`  (
  `ID` BIGINT NOT NULL,
  `CREATE_BY` BIGINT NULL DEFAULT NULL,
  `CREATE_TIME` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATE_BY` BIGINT NULL DEFAULT NULL,
  `UPDATE_TIME` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `DELETED` TINYINT NOT NULL DEFAULT 0,
  `REMARK` VARCHAR(255) NULL DEFAULT NULL,
  `VERSION` INT NOT NULL DEFAULT 0,
  `STATUS` TINYINT NOT NULL DEFAULT 0,
  `CONFIG_ID` BIGINT NULL,
  `RESOURCE_ID` BIGINT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE `resource_config_param`  (
  `ID` BIGINT NOT NULL,
  `CREATE_BY` BIGINT NULL DEFAULT NULL,
  `CREATE_TIME` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATE_BY` BIGINT NULL DEFAULT NULL,
  `UPDATE_TIME` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `DELETED` TINYINT NOT NULL DEFAULT 0,
  `REMARK` VARCHAR(255) NULL DEFAULT NULL,
  `VERSION` INT NOT NULL DEFAULT 0,
  `STATUS` TINYINT NOT NULL DEFAULT 0,
  `RESOURCE_CONFIG_ID` BIGINT NULL,
  `CONFIG_PARAM_ID` BIGINT NULL,
  `VALUE` VARCHAR(255) NULL,
  PRIMARY KEY (`ID`)
);

ALTER TABLE `resource` ADD FOREIGN KEY (`GROUP_ID`) REFERENCES `group` (`ID`);

