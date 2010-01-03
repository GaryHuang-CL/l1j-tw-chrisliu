#----------------------------
# Table structure for petitem
#----------------------------
CREATE TABLE `petitem` (
  `item_id` int(10) unsigned NOT NULL auto_increment,
  `note` varchar(45) NOT NULL default '',
  `hitmodifier` int(3) NOT NULL default '0',
  `dmgmodifier` int(3) NOT NULL default '0',
  `ac` int(3) NOT NULL default '0',
  `add_str` int(2) NOT NULL default '0',
  `add_con` int(2) NOT NULL default '0',
  `add_dex` int(2) NOT NULL default '0',
  `add_int` int(2) NOT NULL default '0',
  `add_wis` int(2) NOT NULL default '0',
  `add_hp` int(10) NOT NULL default '0',
  `add_mp` int(10) NOT NULL default '0',
  `add_sp` int(10) NOT NULL default '0',
  `m_def` int(2) NOT NULL default '0',
  PRIMARY KEY  (`item_id`)
) ENGINE=MyISAM DEFAULT CHARSET=sjis;
#----------------------------
# Records for table petitem
#----------------------------

INSERT INTO `petitem` VALUES
(40749, '�n���^�[ �t�@���O', 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(40750, '���[�C�� �t�@���O', -3, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(40751, '�R���o�b�g �t�@���O', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(40752, '�S�[���h �t�@���O', 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0),
(40756, '�f�B�o�C�� �t�@���O', 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0),
(40757, '�X�`�[�� �t�@���O', 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(40758, '���B�N�g���[ �t�@���O', 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(40761, '���U�[ �y�b�g�A�[�}�[', 0, 0, -4, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(40762, '�{�[�� �y�b�g�A�[�}�[', 0, 0, -7, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(40763, '�X�`�[�� �y�b�g�A�[�}�[', 0, 0, -8, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(40764, '�~�X���� �y�b�g�A�[�}�[', 0, 0, -12, 0, 0, 0, 1, 1, 0, 0, 0, 10),
(40765, '�N���X �y�b�g�A�[�}�[', 0, 0, -13, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(40766, '�`�F�[�� �y�b�g�A�[�}�[', 0, 0, -20, 0, 0, 0, 0, 0, 0, 0, 0, 0);
