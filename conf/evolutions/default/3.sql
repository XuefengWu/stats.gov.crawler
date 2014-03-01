# --- !Ups

ALTER TABLE `data` CHANGE COLUMN `regin` `region` VARCHAR(32) NOT NULL;

# --- !Downs