# Users schema

# --- !Ups

CREATE TABLE user (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`username` varchar(30) NOT NULL,
	`first_name` varchar(30) NOT NULL,
	`last_name` varchar(30) NOT NULL,
	`email` varchar(75) NOT NULL,
	`password` varchar(128) NOT NULL,
	`cellphone` varchar(50),
	`homephone` varchar(50),
	`street` varchar(50),
	`zip` varchar(50),
	`city` varchar(50),
	`notes` text,

	  PRIMARY KEY (`id`),
	  UNIQUE KEY `username` (`username`)
) DEFAULT CHARSET=utf8;

# --- !Downs

DROP TABLE user;
