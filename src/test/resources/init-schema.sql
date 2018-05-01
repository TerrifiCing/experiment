DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `account` char(10) NOT NULL,
  `password` varchar(200) NOT NULL,
  `position` varchar(20) DEFAULT NULL,
  `url` varchar(200) DEFAULT NULL,
  `salt` varchar(10) DEFAULT NULL,
  `name` varchar(16) DEFAULT NULL,
  `classname` varchar(20) DEFAULT NULL,
  `academy` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`account`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `name` varchar(6) NOT NULL,
  `stu_id` char(10) NOT NULL,
  `classname` varchar(20) DEFAULT NULL,
  `academy` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`,`stu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `report`;
CREATE TABLE `report` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `student_id` int(8) NOT NULL,
  `course_id` int(8) NOT NULL,
  `score` varchar(20) DEFAULT NULL,
  `report_url` varchar(200) DEFAULT NULL,
  `data_url` varchar(200) DEFAULT NULL,
  `status` int(8) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mail`;
CREATE TABLE `mail` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `type` varchar(10) NOT NULL,
  `content` text NOT NULL,
  `time` datetime NOT NULL,
  `from_id` int(8) NOT NULL,
  `to_id` int(8) DEFAULT NULL,
  `title` VARCHAR (30),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `loginticket`;
CREATE TABLE `loginticket` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `user_id` int(8) NOT NULL,
  `ticket` varchar(45) NOT NULL,
  `expired` datetime NOT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `exp`;
CREATE TABLE `exp` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `exp_name` varchar(20) NOT NULL,
  `period` int(8) DEFAULT NULL,
  `teacher_id` int(8) DEFAULT NULL,
  `credit` FLOAT  DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `exp_id` int(8) NOT NULL,
  `class_time` datetime DEFAULT NULL,
  `classroom` varchar(20) DEFAULT NULL,
  `count` int(8) DEFAULT NULL,
  `status` int(8) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;