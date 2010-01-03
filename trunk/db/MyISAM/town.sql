#----------------------------
# Table structure for town
#----------------------------
CREATE TABLE `town` (
  `town_id` int(10) unsigned NOT NULL default '0',
  `name` varchar(45) NOT NULL default '',
  `leader_id` int(10) unsigned NOT NULL default '0',
  `leader_name` varchar(45) default NULL,
  `tax_rate` int(10) unsigned NOT NULL default '0',
  `tax_rate_reserved` int(10) unsigned NOT NULL default '0',
  `sales_money` int(10) unsigned NOT NULL default '0',
  `sales_money_yesterday` int(10) unsigned NOT NULL default '0',
  `town_tax` int(10) unsigned NOT NULL default '0',
  `town_fix_tax` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`town_id`)
) ENGINE=MyISAM DEFAULT CHARSET=sjis;

#----------------------------
# Records 
#----------------------------
INSERT INTO `town` VALUES ('1', '�b���铇�̑�', '0', null, '0', '0', '0', '0', '0', '0');
INSERT INTO `town` VALUES ('2', '�V���o�[�i�C�g�^�E��', '0', null, '0', '0', '0', '0', '0', '0');
INSERT INTO `town` VALUES ('3', '�O���[�f�B����', '0', null, '0', '0', '0', '0', '0', '0');
INSERT INTO `town` VALUES ('4', '�Γc����', '0', null, '0', '0', '0', '0', '0', '0');
INSERT INTO `town` VALUES ('5', '�E�b�h�x�b�N��', '0', null, '0', '0', '0', '0', '0', '0');
INSERT INTO `town` VALUES ('6', '�P���g��', '0', null, '0', '0', '0', '0', '0', '0');
INSERT INTO `town` VALUES ('7', '�M�����s�s', '0', null, '0', '0', '0', '0', '0', '0');
INSERT INTO `town` VALUES ('8', '�n�C�l�s�s', '0', null, '0', '0', '0', '0', '0', '0');
INSERT INTO `town` VALUES ('9', '�E�F���_����', '0', null, '0', '0', '0', '0', '0', '0');
INSERT INTO `town` VALUES ('10', '�ۉ�̓��̑�', '0', null, '0', '0', '0', '0', '0', '0');
