#----------------------------
# Table structure for castle
#----------------------------
CREATE TABLE `castle` (
  `castle_id` int(11) NOT NULL default '0',
  `name` varchar(45) NOT NULL default '',
  `war_time` datetime,
  `tax_rate` int(11) NOT NULL default '0',
  `public_money` int(11) NOT NULL default '0',
  PRIMARY KEY  (`castle_id`)
) ENGINE=MyISAM DEFAULT CHARSET=sjis;


#----------------------------
# Records for table castle
#----------------------------
insert  into castle values 
('1', '�P���g��', '2007-10-03 22:00:00', '10', '0'),
('2', '�I�[�N�̐X', '2007-10-04 22:00:00', '10', '0'),
('3', '�E�B���_�E�b�h��', '2007-10-05 22:00:00', '10', '0'),
('4', '�M������', '2007-10-06 22:00:00', '10', '0'),
('5', '�n�C�l��', '2007-10-03 22:00:00', '10', '0'),
('6', '�h���[�t��', '2007-10-04 22:00:00', '10', '0'),
('7', '�A�f����', '2007-10-05 22:00:00', '10', '0'),
('8', '�f�B�A�h�v��', '2007-10-06 22:00:00', '10', '0');
