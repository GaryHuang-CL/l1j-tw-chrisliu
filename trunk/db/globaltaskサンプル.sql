CREATE TABLE global_tasks (id INT(11) NOT NULL DEFAULT 0 primary key, task varchar(50) NOT NULL DEFAULT 0, type varchar(50) NOT NULL DEFAULT 0, last_activation DECIMAL(20) NOT NULL DEFAULT 0, param1 varchar(100) NOT NULL DEFAULT 0, param2 varchar(100) NOT NULL DEFAULT 0, param3 varchar(255) NOT NULL DEFAULT 0);
INSERT INTO `global_tasks` (`id`, `task`, `type`, `last_activation`, `param1`, `param2`, `param3`) VALUES (1, 'restart', 'TYPE_GLOBAL_TASK', 0, '1', '12:55:00', '');
