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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1FollowerInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.poison.L1Poison;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillTimer;
import l1j.server.server.model.skill.L1SkillTimerCreator;
import l1j.server.server.serverpackets.S_Light;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.types.Point;
import l1j.server.server.utils.IntRange;
import static l1j.server.server.model.skill.L1SkillId.*;

// Referenced classes of package l1j.server.server.model:
// L1Object, Die, L1PcInstance, L1MonsterInstance,
// L1World, ActionFailed

public class L1Character extends L1Object {

	private static final long serialVersionUID = 1L;

	private static final Logger _log = Logger.getLogger(L1Character.class
			.getName());

	private L1Poison _poison = null;
	private boolean _paralyzed;
	private boolean _sleeped;

	private final Map<Integer, L1NpcInstance> _petlist = new HashMap<Integer, L1NpcInstance>();
	private final Map<Integer, L1DollInstance> _dolllist = new HashMap<Integer, L1DollInstance>();
	private final Map<Integer, L1SkillTimer> _skillEffect = new HashMap<Integer, L1SkillTimer>();
	private final Map<Integer, L1ItemDelay.ItemDelayTimer> _itemdelay = new HashMap<Integer, L1ItemDelay.ItemDelayTimer>();
	private final Map<Integer, L1FollowerInstance> _followerlist = new HashMap<Integer, L1FollowerInstance>();

	public L1Character() {
		_level = 1;
	}

	/**
	 * �L�����N�^�[�𕜊�������B
	 * 
	 * @param hp
	 *            �������HP
	 */
	public void resurrect(int hp) {
		if (!isDead()) {
			return;
		}
		if (hp <= 0) {
			hp = 1;
		}
		setCurrentHp(hp);
		setDead(false);
		setStatus(0);
		L1PolyMorph.undoPoly(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			pc.sendPackets(new S_RemoveObject(this));
			pc.removeKnownObject(this);
			pc.updateObject();
		}
	}

	private int _currentHp;

	/**
	 * �L�����N�^�[�̌��݂�HP��Ԃ��B
	 * 
	 * @return ���݂�HP
	 */
	public int getCurrentHp() {
		return _currentHp;
	}

	/**
	 * �L�����N�^�[��HP��ݒ肷��B
	 * 
	 * @param i
	 *            �L�����N�^�[�̐V����HP
	 */
	// ����ȏ���������ꍇ�͂��������I�[�o���C�h�i�p�P�b�g���M���j
	public void setCurrentHp(int i) {
		_currentHp = i;
		if (_currentHp >= getMaxHp()) {
			_currentHp = getMaxHp();
		}
	}

	/**
	 * �L�����N�^�[��HP��ݒ肷��B
	 * 
	 * @param i
	 *            �L�����N�^�[�̐V����HP
	 */
	public void setCurrentHpDirect(int i) {
		_currentHp = i;
	}

	private int _currentMp;

	/**
	 * �L�����N�^�[�̌��݂�MP��Ԃ��B
	 * 
	 * @return ���݂�MP
	 */
	public int getCurrentMp() {
		return _currentMp;
	}

	/**
	 * �L�����N�^�[��MP��ݒ肷��B
	 * 
	 * @param i
	 *            �L�����N�^�[�̐V����MP
	 */
	// ����ȏ���������ꍇ�͂��������I�[�o���C�h�i�p�P�b�g���M���j
	public void setCurrentMp(int i) {
		_currentMp = i;
		if (_currentMp >= getMaxMp()) {
			_currentMp = getMaxMp();
		}
	}

	/**
	 * �L�����N�^�[��MP��ݒ肷��B
	 * 
	 * @param i
	 *            �L�����N�^�[�̐V����MP
	 */
	public void setCurrentMpDirect(int i) {
		_currentMp = i;
	}

	/**
	 * �L�����N�^�[�̖����Ԃ�Ԃ��B
	 * 
	 * @return �����Ԃ�\���l�B�����Ԃł����true�B
	 */
	public boolean isSleeped() {
		return _sleeped;
	}

	/**
	 * �L�����N�^�[�̖����Ԃ�ݒ肷��B
	 * 
	 * @param sleeped
	 *            �����Ԃ�\���l�B�����Ԃł����true�B
	 */
	public void setSleeped(boolean sleeped) {
		_sleeped = sleeped;
	}

	/**
	 * �L�����N�^�[�̖�჏�Ԃ�Ԃ��B
	 * 
	 * @return ��჏�Ԃ�\���l�B��჏�Ԃł����true�B
	 */
	public boolean isParalyzed() {
		return _paralyzed;
	}

	/**
	 * �L�����N�^�[�̖�჏�Ԃ�ݒ肷��B
	 * 
	 * @param i
	 *            ��჏�Ԃ�\���l�B��჏�Ԃł����true�B
	 */
	public void setParalyzed(boolean paralyzed) {
		_paralyzed = paralyzed;
	}

	L1Paralysis _paralysis;

	public L1Paralysis getParalysis() {
		return _paralysis;
	}

	public void setParalaysis(L1Paralysis p) {
		_paralysis = p;
	}

	public void cureParalaysis() {
		if (_paralysis != null) {
			_paralysis.cure();
		}
	}

	/**
	 * �L�����N�^�[�̉��͈͂ɋ���v���C���[�ցA�p�P�b�g�𑗐M����B
	 * 
	 * @param packet
	 *            ���M����p�P�b�g��\��ServerBasePacket�I�u�W�F�N�g�B
	 */
	public void broadcastPacket(ServerBasePacket packet) {
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
			pc.sendPackets(packet);
		}
	}

	/**
	 * �L�����N�^�[�̉��͈͂ɋ���v���C���[�ցA�p�P�b�g�𑗐M����B �������^�[�Q�b�g�̉�ʓ��ɂ͑��M���Ȃ��B
	 * 
	 * @param packet
	 *            ���M����p�P�b�g��\��ServerBasePacket�I�u�W�F�N�g�B
	 */
	public void broadcastPacketExceptTargetSight(ServerBasePacket packet,
			L1Character target) {
		for (L1PcInstance pc : L1World.getInstance()
				.getVisiblePlayerExceptTargetSight(this, target)) {
			pc.sendPackets(packet);
		}
	}

	/**
	 * �L�����N�^�[�̉��͈͂ŃC���r�W�����j���or���j��Ȃ��v���C���[����ʂ��āA�p�P�b�g�𑗐M����B
	 * 
	 * @param packet
	 *            ���M����p�P�b�g��\��ServerBasePacket�I�u�W�F�N�g�B
	 * @param isFindInvis
	 *            true : ���j���v���C���[�ɂ����p�P�b�g�𑗐M����B false : ���j��Ȃ��v���C���[�ɂ����p�P�b�g�𑗐M����B
	 */
	public void broadcastPacketForFindInvis(ServerBasePacket packet,
			boolean isFindInvis) {
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
			if (isFindInvis) {
				if (pc.hasSkillEffect(GMSTATUS_FINDINVIS)) {
					pc.sendPackets(packet);
				}
			} else {
				if (!pc.hasSkillEffect(GMSTATUS_FINDINVIS)) {
					pc.sendPackets(packet);
				}
			}
		}
	}

	/**
	 * �L�����N�^�[��50�}�X�ȓ��ɋ���v���C���[�ցA�p�P�b�g�𑗐M����B
	 * 
	 * @param packet
	 *            ���M����p�P�b�g��\��ServerBasePacket�I�u�W�F�N�g�B
	 */
	public void wideBroadcastPacket(ServerBasePacket packet) {
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this,
				50)) {
			pc.sendPackets(packet);
		}
	}

	/**
	 * �L�����N�^�[�̐��ʂ̍��W��Ԃ��B
	 * 
	 * @return ���ʂ̍��W
	 */
	public int[] getFrontLoc() {
		int[] loc = new int[2];
		int x = getX();
		int y = getY();
		int heading = getHeading();
		if (heading == 0) {
			y--;
		} else if (heading == 1) {
			x++;
			y--;
		} else if (heading == 2) {
			x++;
		} else if (heading == 3) {
			x++;
			y++;
		} else if (heading == 4) {
			y++;
		} else if (heading == 5) {
			x--;
			y++;
		} else if (heading == 6) {
			x--;
		} else if (heading == 7) {
			x--;
			y--;
		}
		loc[0] = x;
		loc[1] = y;
		return loc;
	}

	/**
	 * �w�肳�ꂽ���W�ɑ΂��������Ԃ��B
	 * 
	 * @param tx
	 *            ���W��X�l
	 * @param ty
	 *            ���W��Y�l
	 * @return �w�肳�ꂽ���W�ɑ΂������
	 */
	public int targetDirection(int tx, int ty) {
		float dis_x = Math.abs(getX() - tx); // �w�����̃^�[�Q�b�g�܂ł̋���
		float dis_y = Math.abs(getY() - ty); // �x�����̃^�[�Q�b�g�܂ł̋���
		float dis = Math.max(dis_x, dis_y); // �^�[�Q�b�g�܂ł̋���
		if (dis == 0) {
			return getHeading(); // �����ʒu�Ȃ炢�܌����Ă������Ԃ��Ƃ�
		}
		int avg_x = (int) Math.floor((dis_x / dis) + 0.59f); // �㉺���E��������ƗD��Ȋۂ�
		int avg_y = (int) Math.floor((dis_y / dis) + 0.59f); // �㉺���E��������ƗD��Ȋۂ�

		int dir_x = 0;
		int dir_y = 0;
		if (getX() < tx) {
			dir_x = 1;
		}
		if (getX() > tx) {
			dir_x = -1;
		}
		if (getY() < ty) {
			dir_y = 1;
		}
		if (getY() > ty) {
			dir_y = -1;
		}

		if (avg_x == 0) {
			dir_x = 0;
		}
		if (avg_y == 0) {
			dir_y = 0;
		}

		if (dir_x == 1 && dir_y == -1) {
			return 1; // ��
		}
		if (dir_x == 1 && dir_y == 0) {
			return 2; // �E��
		}
		if (dir_x == 1 && dir_y == 1) {
			return 3; // �E
		}
		if (dir_x == 0 && dir_y == 1) {
			return 4; // �E��
		}
		if (dir_x == -1 && dir_y == 1) {
			return 5; // ��
		}
		if (dir_x == -1 && dir_y == 0) {
			return 6; // ����
		}
		if (dir_x == -1 && dir_y == -1) {
			return 7; // ��
		}
		if (dir_x == 0 && dir_y == -1) {
			return 0; // ����
		}
		return getHeading(); // �����ɂ͂��Ȃ��B�͂�
	}

	/**
	 * �w�肳�ꂽ���W�܂ł̒�����ɁA��Q��������*���Ȃ���*��Ԃ��B
	 * 
	 * @param tx
	 *            ���W��X�l
	 * @param ty
	 *            ���W��Y�l
	 * @return ��Q�����������true�A�����false��Ԃ��B
	 */
	public boolean glanceCheck(int tx, int ty) {
		L1Map map = getMap();
		int chx = getX();
		int chy = getY();
		int arw = 0;
		for (int i = 0; i < 15; i++) {
			if ((chx == tx && chy == ty) || (chx + 1 == tx && chy - 1 == ty)
					|| (chx + 1 == tx && chy == ty)
					|| (chx + 1 == tx && chy + 1 == ty)
					|| (chx == tx && chy + 1 == ty)
					|| (chx - 1 == tx && chy + 1 == ty)
					|| (chx - 1 == tx && chy == ty)
					|| (chx - 1 == tx && chy - 1 == ty)
					|| (chx == tx && chy - 1 == ty)) {
				break;

			} else if (chx < tx && chy == ty) {
// if (!map.isArrowPassable(chx, chy, 2)) {
				if (!map.isArrowPassable(chx, chy, targetDirection(tx, ty))) {
					return false;
				}
				chx++;
			} else if (chx == tx && chy < ty) {
// if (!map.isArrowPassable(chx, chy, 4)) {
				if (!map.isArrowPassable(chx, chy, targetDirection(tx, ty))) {
					return false;
				}
				chy++;
			} else if (chx > tx && chy == ty) {
// if (!map.isArrowPassable(chx, chy, 6)) {
				if (!map.isArrowPassable(chx, chy, targetDirection(tx, ty))) {
					return false;
				}
				chx--;
			} else if (chx == tx && chy > ty) {
// if (!map.isArrowPassable(chx, chy, 0)) {
				if (!map.isArrowPassable(chx, chy, targetDirection(tx, ty))) {
					return false;
				}
				chy--;
			} else if (chx < tx && chy > ty) {
// if (!map.isArrowPassable(chx, chy, 1)) {
				if (!map.isArrowPassable(chx, chy, targetDirection(tx, ty))) {
					return false;
				}
				chx++;
				chy--;
			} else if (chx < tx && chy < ty) {
// if (!map.isArrowPassable(chx, chy, 3)) {
				if (!map.isArrowPassable(chx, chy, targetDirection(tx, ty))) {
					return false;
				}
				chx++;
				chy++;
			} else if (chx > tx && chy < ty) {
// if (!map.isArrowPassable(chx, chy, 5)) {
				if (!map.isArrowPassable(chx, chy, targetDirection(tx, ty))) {
					return false;
				}
				chx--;
				chy++;
			} else if (chx > tx && chy > ty) {
// if (!map.isArrowPassable(chx, chy, 7)) {
				if (!map.isArrowPassable(chx, chy, targetDirection(tx, ty))) {
					return false;
				}
				chx--;
				chy--;
			}
		}
		if (arw == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �w�肳�ꂽ���W�֍U���\�ł��邩��Ԃ��B
	 * 
	 * @param x
	 *            ���W��X�l�B
	 * @param y
	 *            ���W��Y�l�B
	 * @param range
	 *            �U���\�Ȕ͈�(�^�C����)
	 * @return �U���\�ł����true,�s�\�ł����false
	 */
	public boolean isAttackPosition(int x, int y, int range) {
		if (range >= 7) // ���u����i�V�ȏ�̏ꍇ�΂߂��l������Ɖ�ʊO�ɏo��)
		{
			if (getLocation().getTileDistance(new Point(x, y)) > range) {
				return false;
			}
		} else // �ߐڕ���
		{
			if (getLocation().getTileLineDistance(new Point(x, y)) > range) {
				return false;
			}
		}
		return glanceCheck(x, y);
	}

	/**
	 * �L�����N�^�[�̃C���x���g����Ԃ��B
	 * 
	 * @return �L�����N�^�[�̃C���x���g����\���AL1Inventory�I�u�W�F�N�g�B
	 */
	public L1Inventory getInventory() {
		return null;
	}

	/**
	 * �L�����N�^�[�ցA�V���ɃX�L�����ʂ�ǉ�����B
	 * 
	 * @param skillId
	 *            �ǉ�������ʂ̃X�L��ID�B
	 * @param timeMillis
	 *            �ǉ�������ʂ̎������ԁB�����̏ꍇ��0�B
	 */
	private void addSkillEffect(int skillId, int timeMillis) {
		L1SkillTimer timer = null;
		if (0 < timeMillis) {
			timer = L1SkillTimerCreator.create(this, skillId, timeMillis);
			timer.begin();
		}
		_skillEffect.put(skillId, timer);
	}

	/**
	 * �L�����N�^�[�ցA�X�L�����ʂ�ݒ肷��B<br>
	 * �d������X�L�����Ȃ��ꍇ�́A�V���ɃX�L�����ʂ�ǉ�����B<br>
	 * �d������X�L��������ꍇ�́A�c����ʎ��Ԃƃp�����[�^�̌��ʎ��Ԃ̒�������D�悵�Đݒ肷��B
	 * 
	 * @param skillId
	 *            �ݒ肷����ʂ̃X�L��ID�B
	 * @param timeMillis
	 *            �ݒ肷����ʂ̎������ԁB�����̏ꍇ��0�B
	 */
	public void setSkillEffect(int skillId, int timeMillis) {
		if (hasSkillEffect(skillId)) {
			int remainingTimeMills = getSkillEffectTimeSec(skillId) * 1000;

			// �c�莞�Ԃ��L���ŁA�p�����[�^�̌��ʎ��Ԃ̕��������������̏ꍇ�͏㏑������B
			if (remainingTimeMills >= 0
					&& (remainingTimeMills < timeMillis || timeMillis == 0)) {
				killSkillEffectTimer(skillId);
				addSkillEffect(skillId, timeMillis);
			}
		} else {
			addSkillEffect(skillId, timeMillis);
		}
	}

	/**
	 * �L�����N�^�[����A�X�L�����ʂ��폜����B
	 * 
	 * @param skillId
	 *            �폜������ʂ̃X�L��ID
	 */
	public void removeSkillEffect(int skillId) {
		L1SkillTimer timer = _skillEffect.remove(skillId);
		if (timer != null) {
			timer.end();
		}
	}

	/**
	 * �L�����N�^�[����A�X�L�����ʂ̃^�C�}�[���폜����B �X�L�����ʂ͍폜����Ȃ��B
	 * 
	 * @param skillId
	 *            �폜����^�C�}�[�̃X�L���h�c
	 */
	public void killSkillEffectTimer(int skillId) {
		L1SkillTimer timer = _skillEffect.remove(skillId);
		if (timer != null) {
			timer.kill();
		}
	}

	/**
	 * �L�����N�^�[����A�S�ẴX�L�����ʃ^�C�}�[���폜����B�X�L�����ʂ͍폜����Ȃ��B
	 */
	public void clearSkillEffectTimer() {
		for (L1SkillTimer timer : _skillEffect.values()) {
			if (timer != null) {
				timer.kill();
			}
		}
		_skillEffect.clear();
	}

	/**
	 * �L�����N�^�[�ɁA�X�L�����ʂ��|�����Ă��邩��Ԃ��B
	 * 
	 * @param skillId
	 *            ���ׂ���ʂ̃X�L��ID�B
	 * @return ���@���ʂ������true�A�Ȃ����false�B
	 */
	public boolean hasSkillEffect(int skillId) {
		return _skillEffect.containsKey(skillId);
	}

	/**
	 * �L�����N�^�[�̃X�L�����ʂ̎������Ԃ�Ԃ��B
	 * 
	 * @param skillId
	 *            ���ׂ���ʂ̃X�L��ID
	 * @return �X�L�����ʂ̎c�莞��(�b)�B�X�L�����������Ă��Ȃ������ʎ��Ԃ������̏ꍇ�A-1�B
	 */
	public int getSkillEffectTimeSec(int skillId) {
		L1SkillTimer timer = _skillEffect.get(skillId);
		if (timer == null) {
			return -1;
		}
		return timer.getRemainingTime();
	}

	private boolean _isSkillDelay = false;

	/**
	 * �L�����N�^�[�ցA�X�L���f�B���C��ǉ�����B
	 * 
	 * @param flag
	 */
	public void setSkillDelay(boolean flag) {
		_isSkillDelay = flag;
	}

	/**
	 * �L�����N�^�[�̓ŏ�Ԃ�Ԃ��B
	 * 
	 * @return �X�L���f�B���C�����B
	 */
	public boolean isSkillDelay() {
		return _isSkillDelay;
	}

	/**
	 * �L�����N�^�[�ցA�A�C�e���f�B���C��ǉ�����B
	 * 
	 * @param delayId
	 *            �A�C�e���f�B���CID�B �ʏ�̃A�C�e���ł����0�A�C���r�W�r���e�B �N���[�N�A�o�����O �u���b�f�B �N���[�N�ł����1�B
	 * @param timer
	 *            �f�B���C���Ԃ�\���AL1ItemDelay.ItemDelayTimer�I�u�W�F�N�g�B
	 */
	public void addItemDelay(int delayId, L1ItemDelay.ItemDelayTimer timer) {
		_itemdelay.put(delayId, timer);
	}

	/**
	 * �L�����N�^�[����A�A�C�e���f�B���C���폜����B
	 * 
	 * @param delayId
	 *            �A�C�e���f�B���CID�B �ʏ�̃A�C�e���ł����0�A�C���r�W�r���e�B �N���[�N�A�o�����O �u���b�f�B �N���[�N�ł����1�B
	 */
	public void removeItemDelay(int delayId) {
		_itemdelay.remove(delayId);
	}

	/**
	 * �L�����N�^�[�ɁA�A�C�e���f�B���C�����邩��Ԃ��B
	 * 
	 * @param delayId
	 *            ���ׂ�A�C�e���f�B���CID�B �ʏ�̃A�C�e���ł����0�A�C���r�W�r���e�B �N���[�N�A�o�����O �u���b�f�B
	 *            �N���[�N�ł����1�B
	 * @return �A�C�e���f�B���C�������true�A�Ȃ����false�B
	 */
	public boolean hasItemDelay(int delayId) {
		return _itemdelay.containsKey(delayId);
	}

	/**
	 * �L�����N�^�[�̃A�C�e���f�B���C���Ԃ�\���AL1ItemDelay.ItemDelayTimer��Ԃ��B
	 * 
	 * @param delayId
	 *            ���ׂ�A�C�e���f�B���CID�B �ʏ�̃A�C�e���ł����0�A�C���r�W�r���e�B �N���[�N�A�o�����O �u���b�f�B
	 *            �N���[�N�ł����1�B
	 * @return �A�C�e���f�B���C���Ԃ�\���AL1ItemDelay.ItemDelayTimer�B
	 */
	public L1ItemDelay.ItemDelayTimer getItemDelayTimer(int delayId) {
		return _itemdelay.get(delayId);
	}

	/**
	 * �L�����N�^�[�ցA�V���Ƀy�b�g�A�T���������X�^�[�A�e�C�~���O�����X�^�[�A���邢�̓N���G�C�g�]���r��ǉ�����B
	 * 
	 * @param npc
	 *            �ǉ�����Npc��\���AL1NpcInstance�I�u�W�F�N�g�B
	 */
	public void addPet(L1NpcInstance npc) {
		_petlist.put(npc.getId(), npc);
	}

	/**
	 * �L�����N�^�[����A�y�b�g�A�T���������X�^�[�A�e�C�~���O�����X�^�[�A���邢�̓N���G�C�g�]���r���폜����B
	 * 
	 * @param npc
	 *            �폜����Npc��\���AL1NpcInstance�I�u�W�F�N�g�B
	 */
	public void removePet(L1NpcInstance npc) {
		_petlist.remove(npc.getId());
	}

	/**
	 * �L�����N�^�[�̃y�b�g���X�g��Ԃ��B
	 * 
	 * @return �L�����N�^�[�̃y�b�g���X�g��\���AHashMap�I�u�W�F�N�g�B���̃I�u�W�F�N�g��Key�̓I�u�W�F�N�gID�AValue��L1NpcInstance�B
	 */
	public Map<Integer, L1NpcInstance> getPetList() {
		return _petlist;
	}

	/**
	 * �L�����N�^�[�փ}�W�b�N�h�[����ǉ�����B
	 * 
	 * @param doll
	 *            �ǉ�����doll��\���AL1DollInstance�I�u�W�F�N�g�B
	 */
	public void addDoll(L1DollInstance doll) {
		_dolllist.put(doll.getId(), doll);
	}

	/**
	 * �L�����N�^�[����}�W�b�N�h�[�����폜����B
	 * 
	 * @param doll
	 *            �폜����doll��\���AL1DollInstance�I�u�W�F�N�g�B
	 */
	public void removeDoll(L1DollInstance doll) {
		_dolllist.remove(doll.getId());
	}

	/**
	 * �L�����N�^�[�̃}�W�b�N�h�[�����X�g��Ԃ��B
	 * 
	 * @return �L�����N�^�[�̖��@�l�`���X�g��\���AHashMap�I�u�W�F�N�g�B���̃I�u�W�F�N�g��Key�̓I�u�W�F�N�gID�AValue��L1DollInstance�B
	 */
	public Map<Integer, L1DollInstance> getDollList() {
		return _dolllist;
	}

	/**
	 * �L�����N�^�[�֏]�҂�ǉ�����B
	 * 
	 * @param follower
	 *            �ǉ�����follower��\���AL1FollowerInstance�I�u�W�F�N�g�B
	 */
	public void addFollower(L1FollowerInstance follower) {
		_followerlist.put(follower.getId(), follower);
	}

	/**
	 * �L�����N�^�[����]�҂��폜����B
	 * 
	 * @param follower
	 *            �폜����follower��\���AL1FollowerInstance�I�u�W�F�N�g�B
	 */
	public void removeFollower(L1FollowerInstance follower) {
		_followerlist.remove(follower.getId());
	}

	/**
	 * �L�����N�^�[�̏]�҃��X�g��Ԃ��B
	 * 
	 * @return �L�����N�^�[�̏]�҃��X�g��\���AHashMap�I�u�W�F�N�g�B���̃I�u�W�F�N�g��Key�̓I�u�W�F�N�gID�AValue��L1FollowerInstance�B
	 */
	public Map<Integer, L1FollowerInstance> getFollowerList() {
		return _followerlist;
	}





	/**
	 * �L�����N�^�[�ցA�ł�ǉ�����B
	 * 
	 * @param poison
	 *            �ł�\���AL1Poison�I�u�W�F�N�g�B
	 */
	public void setPoison(L1Poison poison) {
		_poison = poison;
	}

	/**
	 * �L�����N�^�[�̓ł����Â���B
	 */
	public void curePoison() {
		if (_poison == null) {
			return;
		}
		_poison.cure();
	}

	/**
	 * �L�����N�^�[�̓ŏ�Ԃ�Ԃ��B
	 * 
	 * @return �L�����N�^�[�̓ł�\���AL1Poison�I�u�W�F�N�g�B
	 */
	public L1Poison getPoison() {
		return _poison;
	}

	/**
	 * �L�����N�^�[�֓ł̃G�t�F�N�g��t������
	 * 
	 * @param effectId
	 * @see S_Poison#S_Poison(int, int)
	 */
	public void setPoisonEffect(int effectId) {
		broadcastPacket(new S_Poison(getId(), effectId));
	}

	/**
	 * �L�����N�^�[�����݂�����W���A�ǂ̃]�[���ɑ����Ă��邩��Ԃ��B
	 * 
	 * @return ���W�̃]�[����\���l�B�Z�[�t�e�B�[�]�[���ł����1�A�R���o�b�g�]�[���ł����-1�A�m�[�}���]�[���ł����0�B
	 */
	public int getZoneType() {
		if (getMap().isSafetyZone(getLocation())) {
			return 1;
		} else if (getMap().isCombatZone(getLocation())) {
			return -1;
		} else { // �m�[�}���]�[��
			return 0;
		}
	}

	private int _exp; // �� �o���l

	/**
	 * �L�����N�^�[���ێ����Ă���o���l��Ԃ��B
	 * 
	 * @return �o���l�B
	 */
	public int getExp() {
		return _exp;
	}

	/**
	 * �L�����N�^�[���ێ�����o���l��ݒ肷��B
	 * 
	 * @param exp
	 *            �o���l�B
	 */
	public void setExp(int exp) {
		_exp = exp;
	}

	// �������������������� L1PcInstance�ֈړ�����v���p�e�B ��������������������
	private final List<L1Object> _knownObjects = new CopyOnWriteArrayList<L1Object>();
	private final List<L1PcInstance> _knownPlayer = new CopyOnWriteArrayList<L1PcInstance>();

	/**
	 * �w�肳�ꂽ�I�u�W�F�N�g���A�L�����N�^�[���F�����Ă��邩��Ԃ��B
	 * 
	 * @param obj
	 *            ���ׂ�I�u�W�F�N�g�B
	 * @return �I�u�W�F�N�g���L�����N�^�[���F�����Ă����true�A���Ă��Ȃ����false�B �������g�ɑ΂��Ă�false��Ԃ��B
	 */
	public boolean knownsObject(L1Object obj) {
		return _knownObjects.contains(obj);
	}

	/**
	 * �L�����N�^�[���F�����Ă���S�ẴI�u�W�F�N�g��Ԃ��B
	 * 
	 * @return �L�����N�^�[���F�����Ă���I�u�W�F�N�g��\��L1Object���i�[���ꂽArrayList�B
	 */
	public List<L1Object> getKnownObjects() {
		return _knownObjects;
	}

	/**
	 * �L�����N�^�[���F�����Ă���S�Ẵv���C���[��Ԃ��B
	 * 
	 * @return �L�����N�^�[���F�����Ă���I�u�W�F�N�g��\��L1PcInstance���i�[���ꂽArrayList�B
	 */
	public List<L1PcInstance> getKnownPlayers() {
		return _knownPlayer;
	}

	/**
	 * �L�����N�^�[�ɁA�V���ɔF������I�u�W�F�N�g��ǉ�����B
	 * 
	 * @param obj
	 *            �V���ɔF������I�u�W�F�N�g�B
	 */
	public void addKnownObject(L1Object obj) {
		if (!_knownObjects.contains(obj)) {
			_knownObjects.add(obj);
			if (obj instanceof L1PcInstance) {
				_knownPlayer.add((L1PcInstance) obj);
			}
		}
	}

	/**
	 * �L�����N�^�[����A�F�����Ă���I�u�W�F�N�g���폜����B
	 * 
	 * @param obj
	 *            �폜����I�u�W�F�N�g�B
	 */
	public void removeKnownObject(L1Object obj) {
		_knownObjects.remove(obj);
		if (obj instanceof L1PcInstance) {
			_knownPlayer.remove(obj);
		}
	}

	/**
	 * �L�����N�^�[����A�S�Ă̔F�����Ă���I�u�W�F�N�g���폜����B
	 */
	public void removeAllKnownObjects() {
		_knownObjects.clear();
		_knownPlayer.clear();
	}

	// �������������������� �v���p�e�B ��������������������

	private String _name; // �� ���O

	public String getName() {
		return _name;
	}

	public void setName(String s) {
		_name = s;
	}

	private int _level; // �� ���x��

	public synchronized int getLevel() {
		return _level;
	}

	public synchronized void setLevel(long level) {
		_level = (int) level;
	}

	private short _maxHp = 0; // �� �l�`�w�g�o�i1�`32767�j
	private int _trueMaxHp = 0; // �� �{���̂l�`�w�g�o

	public short getMaxHp() {
		return _maxHp;
	}

	public void setMaxHp(int hp) {
		_trueMaxHp = hp;
		_maxHp = (short) IntRange.ensure(_trueMaxHp, 1, 32767);
		_currentHp = Math.min(_currentHp, _maxHp);
	}

	public void addMaxHp(int i) {
		setMaxHp(_trueMaxHp + i);
	}

	private short _maxMp = 0; // �� �l�`�w�l�o�i0�`32767�j
	private int _trueMaxMp = 0; // �� �{���̂l�`�w�l�o

	public short getMaxMp() {
		return _maxMp;
	}

	public void setMaxMp(int mp) {
		_trueMaxMp = mp;
		_maxMp = (short) IntRange.ensure(_trueMaxMp, 0, 32767);
		_currentMp = Math.min(_currentMp, _maxMp);
	}

	public void addMaxMp(int i) {
		setMaxMp(_trueMaxMp + i);
	}

	private int _ac = 0; // �� �`�b�i-128�`127�j
	private int _trueAc = 0; // �� �{���̂`�b

	public int getAc() {
		return _ac;
	}

	public void setAc(int i) {
		_trueAc = i;
		_ac = IntRange.ensure(i, -128, 127);
	}

	public void addAc(int i) {
		setAc(_trueAc + i);
	}

	private byte _str = 0; // �� �r�s�q�i1�`127�j
	private short _trueStr = 0; // �� �{���̂r�s�q

	public byte getStr() {
		return _str;
	}

	public void setStr(int i) {
		_trueStr = (short) i;
		_str = (byte) IntRange.ensure(i, 1, 127);
	}

	public void addStr(int i) {
		setStr(_trueStr + i);
	}

	private byte _con = 0; // �� �b�n�m�i1�`127�j
	private short _trueCon = 0; // �� �{���̂b�n�m

	public byte getCon() {
		return _con;
	}

	public void setCon(int i) {
		_trueCon = (short) i;
		_con = (byte) IntRange.ensure(i, 1, 127);
	}

	public void addCon(int i) {
		setCon(_trueCon + i);
	}

	private byte _dex = 0; // �� �c�d�w�i1�`127�j
	private short _trueDex = 0; // �� �{���̂c�d�w

	public byte getDex() {
		return _dex;
	}

	public void setDex(int i) {
		_trueDex = (short) i;
		_dex = (byte) IntRange.ensure(i, 1, 127);
	}

	public void addDex(int i) {
		setDex(_trueDex + i);
	}

	private byte _cha = 0; // �� �b�g�`�i1�`127�j
	private short _trueCha = 0; // �� �{���̂b�g�`

	public byte getCha() {
		return _cha;
	}

	public void setCha(int i) {
		_trueCha = (short) i;
		_cha = (byte) IntRange.ensure(i, 1, 127);
	}

	public void addCha(int i) {
		setCha(_trueCha + i);
	}

	private byte _int = 0; // �� �h�m�s�i1�`127�j
	private short _trueInt = 0; // �� �{���̂h�m�s

	public byte getInt() {
		return _int;
	}

	public void setInt(int i) {
		_trueInt = (short) i;
		_int = (byte) IntRange.ensure(i, 1, 127);
	}

	public void addInt(int i) {
		setInt(_trueInt + i);
	}

	private byte _wis = 0; // �� �v�h�r�i1�`127�j
	private short _trueWis = 0; // �� �{���̂v�h�r

	public byte getWis() {
		return _wis;
	}

	public void setWis(int i) {
		_trueWis = (short) i;
		_wis = (byte) IntRange.ensure(i, 1, 127);
	}

	public void addWis(int i) {
		setWis(_trueWis + i);
	}

	private int _wind = 0; // �� ���h��i-128�`127�j
	private int _trueWind = 0; // �� �{���̕��h��

	public int getWind() {
		return _wind;
	} // �g�p����Ƃ�

	public void addWind(int i) {
		_trueWind += i;
		if (_trueWind >= 127) {
			_wind = 127;
		} else if (_trueWind <= -128) {
			_wind = -128;
		} else {
			_wind = _trueWind;
		}
	}

	private int _water = 0; // �� ���h��i-128�`127�j
	private int _trueWater = 0; // �� �{���̐��h��

	public int getWater() {
		return _water;
	} // �g�p����Ƃ�

	public void addWater(int i) {
		_trueWater += i;
		if (_trueWater >= 127) {
			_water = 127;
		} else if (_trueWater <= -128) {
			_water = -128;
		} else {
			_water = _trueWater;
		}
	}

	private int _fire = 0; // �� �Ζh��i-128�`127�j
	private int _trueFire = 0; // �� �{���̉Ζh��

	public int getFire() {
		return _fire;
	} // �g�p����Ƃ�

	public void addFire(int i) {
		_trueFire += i;
		if (_trueFire >= 127) {
			_fire = 127;
		} else if (_trueFire <= -128) {
			_fire = -128;
		} else {
			_fire = _trueFire;
		}
	}

	private int _earth = 0; // �� �n�h��i-128�`127�j
	private int _trueEarth = 0; // �� �{���̒n�h��

	public int getEarth() {
		return _earth;
	} // �g�p����Ƃ�

	public void addEarth(int i) {
		_trueEarth += i;
		if (_trueEarth >= 127) {
			_earth = 127;
		} else if (_trueEarth <= -128) {
			_earth = -128;
		} else {
			_earth = _trueEarth;
		}
	}

	private int _addAttrKind; // �G�������^���t�H�[���_�E���Ō������������̎��

	public int getAddAttrKind() {
		return _addAttrKind;
	}

	public void setAddAttrKind(int i) {
		_addAttrKind = i;
	}

	// �X�^���ϐ�
	private int _registStun = 0;
	private int _trueRegistStun = 0;

	public int getRegistStun() {
		return _registStun;
	} // �g�p����Ƃ�

	public void addRegistStun(int i) {
		_trueRegistStun += i;
		if (_trueRegistStun > 127) {
			_registStun = 127;
		} else if (_trueRegistStun < -128) {
			_registStun = -128;
		} else {
			_registStun = _trueRegistStun;
		}
	}

	// �Ή��ϐ�
	private int _registStone = 0;
	private int _trueRegistStone = 0;

	public int getRegistStone() {
		return _registStone;
	} // �g�p����Ƃ�

	public void addRegistStone(int i) {
		_trueRegistStone += i;
		if (_trueRegistStone > 127) {
			_registStone = 127;
		} else if (_trueRegistStone < -128) {
			_registStone = -128;
		} else {
			_registStone = _trueRegistStone;
		}
	}

	// �����ϐ�
	private int _registSleep = 0;
	private int _trueRegistSleep = 0;

	public int getRegistSleep() {
		return _registSleep;
	} // �g�p����Ƃ�

	public void addRegistSleep(int i) {
		_trueRegistSleep += i;
		if (_trueRegistSleep > 127) {
			_registSleep = 127;
		} else if (_trueRegistSleep < -128) {
			_registSleep = -128;
		} else {
			_registSleep = _trueRegistSleep;
		}
	}

	// �����ϐ�
	private int _registFreeze = 0;
	private int _trueRegistFreeze = 0;

	public int getRegistFreeze() {
		return _registFreeze;
	} // �g�p����Ƃ�

	public void add_regist_freeze(int i) {
		_trueRegistFreeze += i;
		if (_trueRegistFreeze > 127) {
			_registFreeze = 127;
		} else if (_trueRegistFreeze < -128) {
			_registFreeze = -128;
		} else {
			_registFreeze = _trueRegistFreeze;
		}
	}

	// �z�[���h�ϐ�
	private int _registSustain = 0;
	private int _trueRegistSustain = 0;

	public int getRegistSustain() {
		return _registSustain;
	} // �g�p����Ƃ�

	public void addRegistSustain(int i) {
		_trueRegistSustain += i;
		if (_trueRegistSustain > 127) {
			_registSustain = 127;
		} else if (_trueRegistSustain < -128) {
			_registSustain = -128;
		} else {
			_registSustain = _trueRegistSustain;
		}
	}

	// �Èőϐ�
	private int _registBlind = 0;
	private int _trueRegistBlind = 0;

	public int getRegistBlind() {
		return _registBlind;
	} // �g�p����Ƃ�

	public void addRegistBlind(int i) {
		_trueRegistBlind += i;
		if (_trueRegistBlind > 127) {
			_registBlind = 127;
		} else if (_trueRegistBlind < -128) {
			_registBlind = -128;
		} else {
			_registBlind = _trueRegistBlind;
		}
	}

	private int _dmgup = 0; // �� �_���[�W�␳�i-128�`127�j
	private int _trueDmgup = 0; // �� �{���̃_���[�W�␳

	public int getDmgup() {
		return _dmgup;
	} // �g�p����Ƃ�

	public void addDmgup(int i) {
		_trueDmgup += i;
		if (_trueDmgup >= 127) {
			_dmgup = 127;
		} else if (_trueDmgup <= -128) {
			_dmgup = -128;
		} else {
			_dmgup = _trueDmgup;
		}
	}

	private int _bowDmgup = 0; // �� �|�_���[�W�␳�i-128�`127�j
	private int _trueBowDmgup = 0; // �� �{���̋|�_���[�W�␳

	public int getBowDmgup() {
		return _bowDmgup;
	} // �g�p����Ƃ�

	public void addBowDmgup(int i) {
		_trueBowDmgup += i;
		if (_trueBowDmgup >= 127) {
			_bowDmgup = 127;
		} else if (_trueBowDmgup <= -128) {
			_bowDmgup = -128;
		} else {
			_bowDmgup = _trueBowDmgup;
		}
	}

	private int _hitup = 0; // �� �����␳�i-128�`127�j
	private int _trueHitup = 0; // �� �{���̖����␳

	public int getHitup() {
		return _hitup;
	} // �g�p����Ƃ�

	public void addHitup(int i) {
		_trueHitup += i;
		if (_trueHitup >= 127) {
			_hitup = 127;
		} else if (_trueHitup <= -128) {
			_hitup = -128;
		} else {
			_hitup = _trueHitup;
		}
	}

	private int _bowHitup = 0; // �� �|�����␳�i-128�`127�j
	private int _trueBowHitup = 0; // �� �{���̋|�����␳

	public int getBowHitup() {
		return _bowHitup;
	} // �g�p����Ƃ�

	public void addBowHitup(int i) {
		_trueBowHitup += i;
		if (_trueBowHitup >= 127) {
			_bowHitup = 127;
		} else if (_trueBowHitup <= -128) {
			_bowHitup = -128;
		} else {
			_bowHitup = _trueBowHitup;
		}
	}

	private int _mr = 0; // �� ���@�h��i0�`�j
	private int _trueMr = 0; // �� �{���̖��@�h��

	public int getMr() {
		if (hasSkillEffect(153) == true) {
			return _mr / 4;
		} else {
			return _mr;
		}
	} // �g�p����Ƃ�

	public int getTrueMr() {
		return _trueMr;
	} // �Z�b�g����Ƃ�

	public void addMr(int i) {
		_trueMr += i;
		if (_trueMr <= 0) {
			_mr = 0;
		} else {
			_mr = _trueMr;
		}
	}

	private int _sp = 0; // �� ���������r�o

	public int getSp() {
		return getTrueSp() + _sp;
	}

	public int getTrueSp() {
		return getMagicLevel() + getMagicBonus();
	}

	public void addSp(int i) {
		_sp += i;
	}

	private boolean _isDead; // �� ���S���

	public boolean isDead() {
		return _isDead;
	}

	public void setDead(boolean flag) {
		_isDead = flag;
	}

	private int _status; // �� ��ԁH

	public int getStatus() {
		return _status;
	}

	public void setStatus(int i) {
		_status = i;
	}

	private String _title; // �� �^�C�g��

	public String getTitle() {
		return _title;
	}

	public void setTitle(String s) {
		_title = s;
	}

	private int _lawful; // �� �A���C�����g

	public int getLawful() {
		return _lawful;
	}

	public void setLawful(int i) {
		_lawful = i;
	}

	public synchronized void addLawful(int i) {
		_lawful += i;
		if (_lawful > 32767) {
			_lawful = 32767;
		} else if (_lawful < -32768) {
			_lawful = -32768;
		}
	}

	private int _heading; // �� ���� 0.���� 1.�� 2.�E�� 3.�E 4.�E�� 5.�� 6.���� 7.��

	public int getHeading() {
		return _heading;
	}

	public void setHeading(int i) {
		_heading = i;
	}

	private int _moveSpeed; // �� �X�s�[�h 0.�ʏ� 1.�w�C�X�g 2.�X���[

	public int getMoveSpeed() {
		return _moveSpeed;
	}

	public void setMoveSpeed(int i) {
		_moveSpeed = i;
	}

	private int _braveSpeed; // �� �u���C�u��� 0.�ʏ� 1.�u���C�u

	public int getBraveSpeed() {
		return _braveSpeed;
	}

	public void setBraveSpeed(int i) {
		_braveSpeed = i;
	}

	private int _tempCharGfx; // �� �x�[�X�O���t�B�b�N�h�c

	public int getTempCharGfx() {
		return _tempCharGfx;
	}

	public void setTempCharGfx(int i) {
		_tempCharGfx = i;
	}

	private int _gfxid; // �� �O���t�B�b�N�h�c

	public int getGfxId() {
		return _gfxid;
	}

	public void setGfxId(int i) {
		_gfxid = i;
	}

	public int getMagicLevel() {
		return getLevel() / 4;
	}

	public int getMagicBonus() {
		int i = getInt();
		if (i <= 5) {
			return -2;
		} else if (i <= 8) {
			return -1;
		} else if (i <= 11) {
			return 0;
		} else if (i <= 14) {
			return 1;
		} else if (i <= 17) {
			return 2;
		} else if (i <= 24) {
			return i - 15;
		} else if (i <= 35) {
			return 10;
		} else if (i <= 42) {
			return 11;
		} else if (i <= 49) {
			return 12;
		} else if (i <= 50) {
			return 13;
		} else {
			return i - 25;
		}
	}

	public boolean isInvisble() {
		return (hasSkillEffect(INVISIBILITY)
				|| hasSkillEffect(BLIND_HIDING));
	}

	public void healHp(int pt) {
		setCurrentHp(getCurrentHp() + pt);
	}

	private int _karma;

	/**
	 * �L�����N�^�[���ێ����Ă���J���}��Ԃ��B
	 * 
	 * @return �J���}�B
	 */
	public int getKarma() {
		return _karma;
	}

	/**
	 * �L�����N�^�[���ێ�����J���}��ݒ肷��B
	 * 
	 * @param karma
	 *            �J���}�B
	 */
	public void setKarma(int karma) {
		_karma = karma;
	}

	public void setMr(int i) {
		_trueMr = i;
		if (_trueMr <= 0) {
			_mr = 0;
		} else {
			_mr = _trueMr;
		}
	}

	public void turnOnOffLight() {
		int lightSize = 0;
		if (this instanceof L1NpcInstance) {
			L1NpcInstance npc = (L1NpcInstance) this;
			lightSize = npc.getLightSize(); // npc.sql�̃��C�g�T�C�Y
		}
		if (hasSkillEffect(LIGHT)) {
			lightSize = 14;
		}

		for (L1ItemInstance item : getInventory().getItems()) {
			if (item.getItem().getType2() == 0 && item.getItem()
					.getType() == 2) { // light�n�A�C�e��
				int itemlightSize = item.getItem().getLightRange();
				if (itemlightSize != 0 && item.isNowLighting()) {
					if (itemlightSize > lightSize) {
						lightSize = itemlightSize;
					}
				}
			}
		}

		if (this instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) this;
			pc.sendPackets(new S_Light(pc.getId(), lightSize));
		}
		if (!isInvisble()) {
			broadcastPacket(new S_Light(getId(), lightSize));
		}

		setOwnLightSize(lightSize); // S_OwnCharPack�̃��C�g�͈�
		setChaLightSize(lightSize); // S_OtherCharPack, S_NPCPack�Ȃǂ̃��C�g�͈�
	}

	private int _chaLightSize; // �� ���C�g�͈̔�

	public int getChaLightSize() {
		if (isInvisble()) {
			return 0;
		}
		return _chaLightSize;
	}

	public void setChaLightSize(int i) {
		_chaLightSize = i;
	}

	private int _ownLightSize; // �� ���C�g�͈̔�(S_OwnCharPack�p)

	public int getOwnLightSize() {
		return _ownLightSize;
	}

	public void setOwnLightSize(int i) {
		_ownLightSize = i;
	}

}
