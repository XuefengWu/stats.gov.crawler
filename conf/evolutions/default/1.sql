# visitor schema

# --- !Ups

CREATE TABLE `moment` (
`id`  bigint(20) unsigned NOT NULL AUTO_INCREMENT,
`dimension` VARCHAR(32) NOT NULL,
`dbcode` VARCHAR(32) NOT NULL,
`value` TEXT(512) NOT NULL,
PRIMARY KEY (`id`)
);

CREATE TABLE `index` (
`dbcode` VARCHAR(32) NOT NULL,
`id`  VARCHAR(32),
`pid` VARCHAR(32) NOT NULL,
`name` VARCHAR(32) NOT NULL,
`isParent` tinyint(1)  NOT NULL,
`ifData` INT NOT NULL
);



CREATE TABLE `data` (
`id`  bigint(20) unsigned NOT NULL AUTO_INCREMENT,
`a` VARCHAR(32) NOT NULL,
`decode` VARCHAR(32) NOT NULL,
`regin` VARCHAR(32) NOT NULL,
`index` VARCHAR(32) NOT NULL,
`date` VARCHAR(32) NOT NULL,
`value` DOUBLE NOT NULL,
PRIMARY KEY (`id`)
);

CREATE TABLE `region` (
`id`  VARCHAR(32),
`name` VARCHAR(32) NOT NULL,
PRIMARY KEY (`id`)
);

# --- !Downs

DROP TABLE `init`;
DROP TABLE `index`;
DROP TABLE `data`;
DROP TABLE `region`;