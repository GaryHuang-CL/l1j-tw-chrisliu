#----------------------------
# Table structure for beginner
#----------------------------
CREATE TABLE `beginner` (
  `id` int(10) NOT NULL auto_increment,
  `item_id` int(6) NOT NULL default '0',
  `count` int(10) NOT NULL default '0',
  `charge_count` int(10) NOT NULL default '0',
  `enchantlvl` int(6) NOT NULL default '0',
  `item_name` varchar(50) NOT NULL default '',
  `activate` char(1) NOT NULL default 'A',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=sjis;
#----------------------------
# Records for table beginner
#----------------------------


insert into `beginner` values 
(1, 40005, 1, 0, 0, '�L�����h��', 'A'),
(2, 40005, 1, 0, 0, '�L�����h��', 'A'),
(3, 40641, 1, 0, 0, '�g�[�L���O�X�N���[��', 'A'),
(4, 40383, 1, 0, 0, '�n�}�F�̂���', 'P'),
(5, 40378, 1, 0, 0, '�n�}�F�G���t�̐X', 'E'),
(6, 40380, 1, 0, 0, '�n�}�F�V���o�[�i�C�g�̑�', 'E'),
(7, 40384, 1, 0, 0, '�n�}�F�B���ꂽ�k�J', 'K'),
(8, 40383, 1, 0, 0, '�n�}�F�̂���', 'W'),
(9, 40389, 1, 0, 0, '�n�}�F���ق̓��A', 'D'),
(10, 40383, 1, 0, 0, '�n�}�F�̂���', 'D');
