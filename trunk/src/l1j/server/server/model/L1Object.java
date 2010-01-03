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

package l1j.server.server.model;

import java.io.Serializable;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;

// Referenced classes of package l1j.server.server.model:
// L1PcInstance, L1Character

/**
 * ���[���h��ɑ��݂���S�ẴI�u�W�F�N�g�̃x�[�X�N���X
 */
public class L1Object implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * �I�u�W�F�N�g�����݂���}�b�v�̃}�b�vID��Ԃ�
	 * 
	 * @return �}�b�vID
	 */
	public short getMapId() {
		return (short) _loc.getMap().getId();
	}

	/**
	 * �I�u�W�F�N�g�����݂���}�b�v�̃}�b�vID��ݒ肷��
	 * 
	 * @param mapId
	 *            �}�b�vID
	 */
	public void setMap(short mapId) {
		_loc.setMap(L1WorldMap.getInstance().getMap(mapId));
	}

	/**
	 * �I�u�W�F�N�g�����݂���}�b�v��ێ�����L1Map�I�u�W�F�N�g��Ԃ�
	 * 
	 */
	public L1Map getMap() {
		return _loc.getMap();
	}

	/**
	 * �I�u�W�F�N�g�����݂���}�b�v��ݒ肷��
	 * 
	 * @param map
	 *            �I�u�W�F�N�g�����݂���}�b�v��ێ�����L1Map�I�u�W�F�N�g
	 */
	public void setMap(L1Map map) {
		if (map == null) {
			throw new NullPointerException();
		}
		_loc.setMap(map);
	}

	/**
	 * �I�u�W�F�N�g����ӂɎ��ʂ���ID��Ԃ�
	 * 
	 * @return �I�u�W�F�N�gID
	 */
	public int getId() {
		return _id;
	}

	/**
	 * �I�u�W�F�N�g����ӂɎ��ʂ���ID��ݒ肷��
	 * 
	 * @param id
	 *            �I�u�W�F�N�gID
	 */
	public void setId(int id) {
		_id = id;
	}

	/**
	 * �I�u�W�F�N�g�����݂�����W��X�l��Ԃ�
	 * 
	 * @return ���W��X�l
	 */
	public int getX() {
		return _loc.getX();
	}

	/**
	 * �I�u�W�F�N�g�����݂�����W��X�l��ݒ肷��
	 * 
	 * @param x
	 *            ���W��X�l
	 */
	public void setX(int x) {
		_loc.setX(x);
	}

	/**
	 * �I�u�W�F�N�g�����݂�����W��Y�l��Ԃ�
	 * 
	 * @return ���W��Y�l
	 */
	public int getY() {
		return _loc.getY();
	}

	/**
	 * �I�u�W�F�N�g�����݂�����W��Y�l��ݒ肷��
	 * 
	 * @param y
	 *            ���W��Y�l
	 */
	public void setY(int y) {
		_loc.setY(y);
	}

	private L1Location _loc = new L1Location();

	/**
	 * �I�u�W�F�N�g�����݂���ʒu��ێ�����AL1Location�I�u�W�F�N�g�ւ̎Q�Ƃ�Ԃ��B
	 * 
	 * @return ���W��ێ�����AL1Location�I�u�W�F�N�g�ւ̎Q��
	 */
	public L1Location getLocation() {
		return _loc;
	}

	public void setLocation(L1Location loc) {
		_loc.setX(loc.getX());
		_loc.setY(loc.getY());
		_loc.setMap(loc.getMapId());
	}

	public void setLocation(int x, int y, int mapid) {
		_loc.setX(x);
		_loc.setY(y);
		_loc.setMap(mapid);
	}

	/**
	 * �w�肳�ꂽ�I�u�W�F�N�g�܂ł̒���������Ԃ��B
	 */
	public double getLineDistance(L1Object obj) {
		return this.getLocation().getLineDistance(obj.getLocation());
	}

	/**
	 * �w�肳�ꂽ�I�u�W�F�N�g�܂ł̒����^�C������Ԃ��B
	 */
	public int getTileLineDistance(L1Object obj) {
		return this.getLocation().getTileLineDistance(obj.getLocation());
	}

	/**
	 * �w�肳�ꂽ�I�u�W�F�N�g�܂ł̃^�C������Ԃ��B
	 */
	public int getTileDistance(L1Object obj) {
		return this.getLocation().getTileDistance(obj.getLocation());
	}

	/**
	 * �I�u�W�F�N�g���v���C���[�̉�ʓ��ɓ�����(�F�����ꂽ)�ۂɌĂяo�����B
	 * 
	 * @param perceivedFrom
	 *            ���̃I�u�W�F�N�g��F������PC
	 */
	public void onPerceive(L1PcInstance perceivedFrom) {
	}

	/**
	 * �I�u�W�F�N�g�ւ̃A�N�V���������������ۂɌĂяo�����
	 * 
	 * @param actionFrom
	 *            �A�N�V�������N������PC
	 */
	public void onAction(L1PcInstance actionFrom) {
	}

	/**
	 * �I�u�W�F�N�g���b��������ꂽ�Ƃ��Ăяo�����
	 * 
	 * @param talkFrom
	 *            �b��������PC
	 */
	public void onTalkAction(L1PcInstance talkFrom) {
	}

	private int _id = 0;
}
