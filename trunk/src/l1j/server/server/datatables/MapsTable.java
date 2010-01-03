/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public final class MapsTable {
	private class MapData {
		public int startX = 0;
		public int endX = 0;
		public int startY = 0;
		public int endY = 0;
		public double monster_amount = 1;
		public double dropRate = 1;
		public boolean isUnderwater = false;
		public boolean markable = false;
		public boolean teleportable = false;
		public boolean escapable = false;
		public boolean isUseResurrection = false;
		public boolean isUsePainwand = false;
		public boolean isEnabledDeathPenalty = false;
		public boolean isTakePets = false;
		public boolean isRecallPets = false;
		public boolean isUsableItem = false;
		public boolean isUsableSkill = false;
	}

	private static Logger _log = Logger.getLogger(MapsTable.class.getName());

	private static MapsTable _instance;

	/**
	 * Key�Ƀ}�b�vID�AValue�Ƀe���|�[�g�ۃt���O���i�[�����HashMap
	 */
	private final Map<Integer, MapData> _maps = new HashMap<Integer, MapData>();

	/**
	 * �V����MapsTable�I�u�W�F�N�g�𐶐����A�}�b�v�̃e���|�[�g�ۃt���O��ǂݍ��ށB
	 */
	private MapsTable() {
		loadMapsFromDatabase();
	}

	/**
	 * �}�b�v�̃e���|�[�g�ۃt���O���f�[�^�x�[�X����ǂݍ��݁AHashMap _maps�Ɋi�[����B
	 */
	private void loadMapsFromDatabase() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM mapids");

			for (rs = pstm.executeQuery(); rs.next();) {
				MapData data = new MapData();
				int mapId = rs.getInt("mapid");
				// rs.getString("locationname");
				data.startX = rs.getInt("startX");
				data.endX = rs.getInt("endX");
				data.startY = rs.getInt("startY");
				data.endY = rs.getInt("endY");
				data.monster_amount = rs.getDouble("monster_amount");
				data.dropRate = rs.getDouble("drop_rate");
				data.isUnderwater = rs.getBoolean("underwater");
				data.markable = rs.getBoolean("markable");
				data.teleportable = rs.getBoolean("teleportable");
				data.escapable = rs.getBoolean("escapable");
				data.isUseResurrection = rs.getBoolean("resurrection");
				data.isUsePainwand = rs.getBoolean("painwand");
				data.isEnabledDeathPenalty = rs.getBoolean("penalty");
				data.isTakePets = rs.getBoolean("take_pets");
				data.isRecallPets = rs.getBoolean("recall_pets");
				data.isUsableItem = rs.getBoolean("usable_item");
				data.isUsableSkill = rs.getBoolean("usable_skill");

				_maps.put(new Integer(mapId), data);
			}

			_log.config("Maps " + _maps.size());
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * MapsTable�̃C���X�^���X��Ԃ��B
	 * 
	 * @return MapsTable�̃C���X�^���X
	 */
	public static MapsTable getInstance() {
		if (_instance == null) {
			_instance = new MapsTable();
		}
		return _instance;
	}

	/**
	 * �}�b�v����X�J�n���W��Ԃ��B
	 * 
	 * @param mapId
	 *            ���ׂ�}�b�v�̃}�b�vID
	 * @return X�J�n���W
	 */
	public int getStartX(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).startX;
	}

	/**
	 * �}�b�v����X�I�����W��Ԃ��B
	 * 
	 * @param mapId
	 *            ���ׂ�}�b�v�̃}�b�vID
	 * @return X�I�����W
	 */
	public int getEndX(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).endX;
	}

	/**
	 * �}�b�v����Y�J�n���W��Ԃ��B
	 * 
	 * @param mapId
	 *            ���ׂ�}�b�v�̃}�b�vID
	 * @return Y�J�n���W
	 */
	public int getStartY(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).startY;
	}

	/**
	 * �}�b�v����Y�I�����W��Ԃ��B
	 * 
	 * @param mapId
	 *            ���ׂ�}�b�v�̃}�b�vID
	 * @return Y�I�����W
	 */
	public int getEndY(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).endY;
	}

	/**
	 * �}�b�v�̃����X�^�[�ʔ{����Ԃ�
	 * 
	 * @param mapId
	 *            ���ׂ�}�b�v�̃}�b�vID
	 * @return �����X�^�[�ʂ̔{��
	 */
	public double getMonsterAmount(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return map.monster_amount;
	}

	/**
	 * �}�b�v�̃h���b�v�{����Ԃ�
	 * 
	 * @param mapId
	 *            ���ׂ�}�b�v�̃}�b�vID
	 * @return �h���b�v�{��
	 */
	public double getDropRate(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return map.dropRate;
	}

	/**
	 * �}�b�v���A�����ł��邩��Ԃ��B
	 * 
	 * @param mapId
	 *            ���ׂ�}�b�v�̃}�b�vID
	 * 
	 * @return �����ł����true
	 */
	public boolean isUnderwater(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUnderwater;
	}

	/**
	 * �}�b�v���A�u�b�N�}�[�N�\�ł��邩��Ԃ��B
	 * 
	 * @param mapId
	 *            ���ׂ�}�b�v�̃}�b�vID
	 * @return �u�b�N�}�[�N�\�ł����true
	 */
	public boolean isMarkable(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).markable;
	}

	/**
	 * �}�b�v���A�����_���e���|�[�g�\�ł��邩��Ԃ��B
	 * 
	 * @param mapId
	 *            ���ׂ�}�b�v�̃}�b�vID
	 * @return �\�ł����true
	 */
	public boolean isTeleportable(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).teleportable;
	}

	/**
	 * �}�b�v���AMAP�𒴂����e���|�[�g�\�ł��邩��Ԃ��B
	 * 
	 * @param mapId
	 *            ���ׂ�}�b�v�̃}�b�vID
	 * @return �\�ł����true
	 */
	public boolean isEscapable(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).escapable;
	}

	/**
	 * �}�b�v���A�����\�ł��邩��Ԃ��B
	 * 
	 * @param mapId
	 *            ���ׂ�}�b�v�̃}�b�vID
	 * 
	 * @return �����\�ł����true
	 */
	public boolean isUseResurrection(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUseResurrection;
	}

	/**
	 * �}�b�v���A�p�C�������h�g�p�\�ł��邩��Ԃ��B
	 * 
	 * @param mapId
	 *            ���ׂ�}�b�v�̃}�b�vID
	 * 
	 * @return �p�C�������h�g�p�\�ł����true
	 */
	public boolean isUsePainwand(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUsePainwand;
	}

	/**
	 * �}�b�v���A�f�X�y�i���e�B�����邩��Ԃ��B
	 * 
	 * @param mapId
	 *            ���ׂ�}�b�v�̃}�b�vID
	 * 
	 * @return �f�X�y�i���e�B�ł����true
	 */
	public boolean isEnabledDeathPenalty(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isEnabledDeathPenalty;
	}

	/**
	 * �}�b�v���A�y�b�g�E�T������A��čs���邩��Ԃ��B
	 * 
	 * @param mapId
	 *            ���ׂ�}�b�v�̃}�b�vID
	 * 
	 * @return �y�b�g�E�T������A��čs����Ȃ��true
	 */
	public boolean isTakePets(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isTakePets;
	}

	/**
	 * �}�b�v���A�y�b�g�E�T�������Ăяo���邩��Ԃ��B
	 * 
	 * @param mapId
	 *            ���ׂ�}�b�v�̃}�b�vID
	 * 
	 * @return �y�b�g�E�T�������Ăяo����Ȃ��true
	 */
	public boolean isRecallPets(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isRecallPets;
	}

	/**
	 * �}�b�v���A�A�C�e�����g�p�ł��邩��Ԃ��B
	 * 
	 * @param mapId
	 *            ���ׂ�}�b�v�̃}�b�vID
	 * 
	 * @return �A�C�e�����g�p�ł���Ȃ��true
	 */
	public boolean isUsableItem(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUsableItem;
	}

	/**
	 * �}�b�v���A�X�L�����g�p�ł��邩��Ԃ��B
	 * 
	 * @param mapId
	 *            ���ׂ�}�b�v�̃}�b�vID
	 * 
	 * @return �X�L�����g�p�ł���Ȃ��true
	 */
	public boolean isUsableSkill(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUsableSkill;
	}

}
