CREATE TABLE `db_lock` (
  `id` int(11) DEFAULT NULL,
  `host` varchar(50) DEFAULT NULL,
  `pid` varchar(50) DEFAULT NULL,
  `datetime` datetime DEFAULT NULL,
  UNIQUE KEY `id` (`id`)
);

insert into db_lock values ( 1, '', 0, '2000-01-01 12:00:00.0');
