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
package l1j.server.server.model.Instance;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.datatables.UBTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1UltimateBattle;
import l1j.server.server.model.L1World;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.CalcExp;
import static l1j.server.server.model.skill.L1SkillId.*;

public class L1MonsterInstance extends L1NpcInstance {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(L1MonsterInstance.class
			.getName());

	private static Random _random = new Random();

	private boolean _storeDroped; // �h���b�v�A�C�e���̓Ǎ�������������

	// �A�C�e���g�p����
	@Override
	public void onItemUse() {
		if (!isActived() && _target != null) {
			useItem(USEITEM_HASTE, 40); // �S�O���̊m���Ńw�C�X�g�|�[�V�����g�p

			// �A�C�e������Ȃ����ǃh�b�y������
			if (getNpcTemplate().is_doppel() && _target instanceof L1PcInstance) {
				L1PcInstance targetPc = (L1PcInstance) _target;
				setName(_target.getName());
				setNameId(_target.getName());
				setTitle(_target.getTitle());
				setTempLawful(_target.getLawful());
				setTempCharGfx(targetPc.getClassId());
				setGfxId(targetPc.getClassId());
				setPassispeed(640);
				setAtkspeed(900); // ���m�Ȓl���킩���
				for (L1PcInstance pc : L1World.getInstance()
						.getRecognizePlayer(this)) {
					pc.sendPackets(new S_RemoveObject(this));
					pc.removeKnownObject(this);
					pc.updateObject();
				}
			}
		}
		if (getCurrentHp() * 100 / getMaxHp() < 40) { // �g�o���S�O����������
			useItem(USEITEM_HEAL, 50); // �T�O���̊m���ŉ񕜃|�[�V�����g�p
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		if (0 < getCurrentHp()) {
			if (getHiddenStatus() == HIDDEN_STATUS_SINK
					|| getHiddenStatus() == HIDDEN_STATUS_ICE) {
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(),
						ActionCodes.ACTION_Hide));
			} else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(),
						ActionCodes.ACTION_Moveup));
			}
			perceivedFrom.sendPackets(new S_NPCPack(this));
			onNpcAI(); // �����X�^�[�̂`�h���J�n
			if (getBraveSpeed() == 1) { // �����Ƃ������@���킩��Ȃ�
				perceivedFrom.sendPackets(new S_SkillBrave(getId(), 1, 600000));
			}
		} else {
			perceivedFrom.sendPackets(new S_NPCPack(this));
		}
	}

	// �^�[�Q�b�g��T��
	public static int[][] _classGfxId = { { 0, 1 }, { 48, 61 }, { 37, 138 },
			{ 734, 1186 }, { 2786, 2796 } };

	@Override
	public void searchTarget() {
		// �^�[�Q�b�g�{��
		L1PcInstance targetPlayer = null;

		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
			if (pc.getCurrentHp() <= 0 || pc.isDead() || pc.isGm()
					|| pc.isMonitor() || pc.isGhost()) {
				continue;
			}

			// ���Z����͕ϐg�^���ϐg�Ɍ��炸�S�ăA�N�e�B�u
			int mapId = getMapId();
			if (mapId == 88 || mapId == 98 || mapId == 92 || mapId == 91
					|| mapId == 95) {
				if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) { // �C���r�W�`�F�b�N
					targetPlayer = pc;
					break;
				}
			}

			if (getNpcId() == 45600){ // �J�[�c
				if (pc.isCrown() || pc.isDarkelf()
						|| pc.getTempCharGfx() != pc.getClassId()) { // ���ϐg�̌N��ADE�ɂ̓A�N�e�B�u
					targetPlayer = pc;
					break;
				}
			}

			// �ǂ��炩�̏����𖞂����ꍇ�A�F�D�ƌ��Ȃ���搧�U������Ȃ��B
			// �E�����X�^�[�̃J���}���}�C�i�X�l�i�o�����O�������X�^�[�j��PC�̃J���}���x����1�ȏ�i�o�����O�F�D�j
			// �E�����X�^�[�̃J���}���v���X�l�i���q�������X�^�[�j��PC�̃J���}���x����-1�ȉ��i���q�F�D�j
			if ((getNpcTemplate().getKarma() < 0 && pc.getKarmaLevel() >= 1)
					|| (getNpcTemplate().getKarma() > 0 && pc.getKarmaLevel() <= -1)) {
				continue;
			}
			// �����Ă�ꂽ�҂����̒n �J���}�N�G�X�g�̕ϐg���́A�e�w�c�̃����X�^�[����搧�U������Ȃ�
			if (pc.getTempCharGfx() == 6034 && getNpcTemplate().getKarma() < 0 
					|| pc.getTempCharGfx() == 6035 && getNpcTemplate().getKarma() > 0
					|| pc.getTempCharGfx() == 6035 && getNpcTemplate().get_npcId() == 46070
					|| pc.getTempCharGfx() == 6035 && getNpcTemplate().get_npcId() == 46072) {
				continue;
			}

			if (!getNpcTemplate().is_agro() && !getNpcTemplate().is_agrososc()
					&& getNpcTemplate().is_agrogfxid1() < 0
					&& getNpcTemplate().is_agrogfxid2() < 0) { // ���S�ȃm���A�N�e�B�u�����X�^�[
				if (pc.getLawful() < -1000) { // �v���C���[���J�I�e�B�b�N
					targetPlayer = pc;
					break;
				}
				continue;
			}

			if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) { // �C���r�W�`�F�b�N
				if (pc.hasSkillEffect(67)) { // �ϐg���Ă�
					if (getNpcTemplate().is_agrososc()) { // �ϐg�ɑ΂��ăA�N�e�B�u
						targetPlayer = pc;
						break;
					}
				} else if (getNpcTemplate().is_agro()) { // �A�N�e�B�u�����X�^�[
					targetPlayer = pc;
					break;
				}

				// ����̃N���Xor�O���t�B�b�N�h�c�ɃA�N�e�B�u
				if (getNpcTemplate().is_agrogfxid1() >= 0
						&& getNpcTemplate().is_agrogfxid1() <= 4) { // �N���X�w��
					if (_classGfxId[getNpcTemplate().is_agrogfxid1()][0] == pc
							.getTempCharGfx()
							|| _classGfxId[getNpcTemplate().is_agrogfxid1()][1] == pc
									.getTempCharGfx()) {
						targetPlayer = pc;
						break;
					}
				} else if (pc.getTempCharGfx() == getNpcTemplate()
						.is_agrogfxid1()) { // �O���t�B�b�N�h�c�w��
					targetPlayer = pc;
					break;
				}

				if (getNpcTemplate().is_agrogfxid2() >= 0
						&& getNpcTemplate().is_agrogfxid2() <= 4) { // �N���X�w��
					if (_classGfxId[getNpcTemplate().is_agrogfxid2()][0] == pc
							.getTempCharGfx()
							|| _classGfxId[getNpcTemplate().is_agrogfxid2()][1] == pc
									.getTempCharGfx()) {
						targetPlayer = pc;
						break;
					}
				} else if (pc.getTempCharGfx() == getNpcTemplate()
						.is_agrogfxid2()) { // �O���t�B�b�N�h�c�w��
					targetPlayer = pc;
					break;
				}
			}
		}
		if (targetPlayer != null) {
			_hateList.add(targetPlayer, 0);
			_target = targetPlayer;
		}
	}

	// �����N�̐ݒ�
	@Override
	public void setLink(L1Character cha) {
		if (cha != null && _hateList.isEmpty()) { // �^�[�Q�b�g�����Ȃ��ꍇ�̂ݒǉ�
			_hateList.add(cha, 0);
			checkTarget();
		}
	}

	public L1MonsterInstance(L1Npc template) {
		super(template);
		_storeDroped = false;
	}

	@Override
	public void onNpcAI() {
		if (isAiRunning()) {
			return;
		}
		if (!_storeDroped) // ���ʂȃI�u�W�F�N�g�h�c�𔭍s���Ȃ��悤�ɂ����ŃZ�b�g
		{
			DropTable.getInstance().setDrop(this, getInventory());
			getInventory().shuffle();
			_storeDroped = true;
		}
		setActived(false);
		startAI();
	}

	@Override
	public void onTalkAction(L1PcInstance pc) {
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		String htmlid = null;
		String[] htmldata = null;

			// html�\���p�P�b�g���M
			if (htmlid != null) { // htmlid���w�肳��Ă���ꍇ
				if (htmldata != null) { // html�w�肪����ꍇ�͕\��
					pc.sendPackets(new S_NPCTalkReturn(objid, htmlid,
							htmldata));
				} else {
					pc.sendPackets(new S_NPCTalkReturn(objid, htmlid));
				}
			} else {
				if (pc.getLawful() < -1000) { // �v���C���[���J�I�e�B�b�N
					pc.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
				} else {
					pc.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
				}
			}
		}

	@Override
	public void onAction(L1PcInstance pc) {
		if (getCurrentHp() > 0 && !isDead()) {
			L1Attack attack = new L1Attack(pc, this);
			if (attack.calcHit()) {
				attack.calcDamage();
				attack.calcStaffOfMana();
				attack.addPcPoisonAttack(pc, this);
				attack.addChaserAttack();
			}
			attack.action();
			attack.commit();
		}
	}

	@Override
	public void ReceiveManaDamage(L1Character attacker, int mpDamage) { // �U���łl�o�����炷�Ƃ��͂������g�p
		if (mpDamage > 0 && !isDead()) {
			// int Hate = mpDamage / 10 + 10; // ���ӁI�v�Z�K�� �_���[�W�̂P�O���̂P�{�q�b�g�w�C�g�P�O
			// setHate(attacker, Hate);
			setHate(attacker, mpDamage);

			onNpcAI();

			if (attacker instanceof L1PcInstance) { // ���Ԉӎ����������X�^�[�̃^�[�Q�b�g�ɐݒ�
				serchLink((L1PcInstance) attacker, getNpcTemplate()
						.get_family());
			}

			int newMp = getCurrentMp() - mpDamage;
			if (newMp < 0) {
				newMp = 0;
			}
			setCurrentMp(newMp);
		}
	}

	@Override
	public void receiveDamage(L1Character attacker, int damage) { // �U���łg�o�����炷�Ƃ��͂������g�p
		if (getCurrentHp() > 0 && !isDead()) {
			if (getHiddenStatus() == HIDDEN_STATUS_SINK
					|| getHiddenStatus() == HIDDEN_STATUS_FLY) {
				return;
			}
			if (damage >= 0) {
				if (!(attacker instanceof L1EffectInstance)) { // FW�̓w�C�g�Ȃ�
					setHate(attacker, damage);
				}
			}
			if (damage > 0) {
				removeSkillEffect(FOG_OF_SLEEPING);
			}

			onNpcAI();

			if (attacker instanceof L1PcInstance) { // ���Ԉӎ����������X�^�[�̃^�[�Q�b�g�ɐݒ�
				serchLink((L1PcInstance) attacker, getNpcTemplate()
						.get_family());
			}

			if (attacker instanceof L1PcInstance && damage > 0) {
				L1PcInstance player = (L1PcInstance) attacker;
				player.setPetTarget(this);

				if (getNpcTemplate().get_npcId() == 45681 // �����h�r�I��
						|| getNpcTemplate().get_npcId() == 45682 // �A���^���X
						|| getNpcTemplate().get_npcId() == 45683 // �p�v���I��
						|| getNpcTemplate().get_npcId() == 45684) // ���@���J�X
				{
					recall(player);
				}
			}

			int newHp = getCurrentHp() - damage;
			if (newHp <= 0 && !isDead()) {
				int transformId = getNpcTemplate().getTransformId();
				// �ϐg���Ȃ������X�^�[
				if (transformId == -1) {
					setCurrentHpDirect(0);
					setDead(true);
					setStatus(ActionCodes.ACTION_Die);
					openDoorWhenNpcDied(this);
					Death death = new Death(attacker);
					GeneralThreadPool.getInstance().execute(death);
					// Death(attacker);
				} else { // �ϐg���郂���X�^�[
// distributeExpDropKarma(attacker);
					transform(transformId);
				}
			}
			if (newHp > 0) {
				setCurrentHp(newHp);
				hide();
			}
		} else if (!isDead()) { // �O�̂���
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);
			Death death = new Death(attacker);
			GeneralThreadPool.getInstance().execute(death);
			// Death(attacker);
		}
	}

	private static void openDoorWhenNpcDied(L1NpcInstance npc) {
		int[] npcId = { 46143, 46144, 46145, 46146, 46147, 46148,
				46149, 46150, 46151, 46152};
		int[] doorId = { 5001, 5002, 5003, 5004, 5005, 5006,
				5007, 5008, 5009, 5010};

		for (int i = 0; i < npcId.length; i++) {
			if (npc.getNpcTemplate().get_npcId() == npcId[i]) {
				openDoorInCrystalCave(doorId[i]);
			}
		}
	}

	private static void openDoorInCrystalCave(int doorId) {
		for (L1Object object : L1World.getInstance().getObject()) {
			if (object instanceof L1DoorInstance) {
				L1DoorInstance door = (L1DoorInstance) object;
				if (door.getDoorId() == doorId) {
					door.open();
				}
			}
		}
	}

	/**
	 * ������5�ȏ㗣��Ă���pc������3�`4�̈ʒu�Ɉ����񂹂�B
	 * 
	 * @param pc
	 */
	private void recall(L1PcInstance pc) {
		if (getMapId() != pc.getMapId()) {
			return;
		}
		if (getLocation().getTileLineDistance(pc.getLocation()) > 4) {
			for (int count = 0; count < 10; count++) {
				L1Location newLoc = getLocation().randomLocation(3, 4, false);
				if (glanceCheck(newLoc.getX(), newLoc.getY())) {
					L1Teleport.teleport(pc, newLoc.getX(), newLoc.getY(),
							getMapId(), 5, true);
					break;
				}
			}
		}
	}

	@Override
	public void setCurrentHp(int i) {
		int currentHp = i;
		if (currentHp >= getMaxHp()) {
			currentHp = getMaxHp();
		}
		setCurrentHpDirect(currentHp);

		if (getMaxHp() > getCurrentHp()) {
			startHpRegeneration();
		}
	}

	@Override
	public void setCurrentMp(int i) {
		int currentMp = i;
		if (currentMp >= getMaxMp()) {
			currentMp = getMaxMp();
		}
		setCurrentMpDirect(currentMp);

		if (getMaxMp() > getCurrentMp()) {
			startMpRegeneration();
		}
	}

	class Death implements Runnable {
		L1Character _lastAttacker;

		public Death(L1Character lastAttacker) {
			_lastAttacker = lastAttacker;
		}

		@Override
		public void run() {
			setDeathProcessing(true);
			setCurrentHpDirect(0);
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);

			getMap().setPassable(getLocation(), true);

			broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));

			startChat(CHAT_TIMING_DEAD);

			distributeExpDropKarma(_lastAttacker);
			giveUbSeal();

			setDeathProcessing(false);

			setExp(0);
			setKarma(0);
			allTargetClear();

			startDeleteTimer();
		}
	}

	private void distributeExpDropKarma(L1Character lastAttacker) {
		if (lastAttacker == null) {
			return;
		}
		L1PcInstance pc = null;
		if (lastAttacker instanceof L1PcInstance) {
			pc = (L1PcInstance) lastAttacker;
		} else if (lastAttacker instanceof L1PetInstance) {
			pc = (L1PcInstance) ((L1PetInstance) lastAttacker).getMaster();
		} else if (lastAttacker instanceof L1SummonInstance) {
			pc = (L1PcInstance) ((L1SummonInstance) lastAttacker).getMaster();
		}

		if (pc != null) {
			ArrayList<L1Character> targetList = _hateList.toTargetArrayList();
			ArrayList<Integer> hateList = _hateList.toHateArrayList();
			int exp = getExp();
			CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
			// ���S�����ꍇ�̓h���b�v�ƃJ���}�����z�A���S�����ϐg�����ꍇ��EXP�̂�
			if (isDead()) {
				distributeDrop();
				giveKarma(pc);
			}
		} else if (lastAttacker instanceof L1EffectInstance) { // FW���|�����ꍇ
			ArrayList<L1Character> targetList = _hateList.toTargetArrayList();
			ArrayList<Integer> hateList = _hateList.toHateArrayList();
			// �w�C�g���X�g�ɃL�����N�^�[�����݂���
			if (hateList.size() != 0) {
				// �ő�w�C�g�����L�����N�^�[���|�������̂Ƃ���
				int maxHate = 0;
				for (int i = hateList.size() - 1; i >= 0; i--) {
					if (maxHate < ((Integer) hateList.get(i))) {
						maxHate = (hateList.get(i));
						lastAttacker = targetList.get(i);
					}
				}
				if (lastAttacker instanceof L1PcInstance) {
					pc = (L1PcInstance) lastAttacker;
				} else if (lastAttacker instanceof L1PetInstance) {
					pc = (L1PcInstance) ((L1PetInstance) lastAttacker)
							.getMaster();
				} else if (lastAttacker instanceof L1SummonInstance) {
					pc = (L1PcInstance) ((L1SummonInstance)
							lastAttacker).getMaster();
				}
				if (pc != null) {
					int exp = getExp();
					CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
					// ���S�����ꍇ�̓h���b�v�ƃJ���}�����z�A���S�����ϐg�����ꍇ��EXP�̂�
					if (isDead()) {
						distributeDrop();
						giveKarma(pc);
					}
				}
			}
		}
	}

	private void distributeDrop() {
		ArrayList<L1Character> dropTargetList = _dropHateList
				.toTargetArrayList();
		ArrayList<Integer> dropHateList = _dropHateList.toHateArrayList();
		try {
			int npcId = getNpcTemplate().get_npcId();
			if (npcId != 45640
					|| (npcId == 45640 && getTempCharGfx() == 2332)) { 
				DropTable.getInstance().dropShare(L1MonsterInstance.this,
						dropTargetList, dropHateList);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	private void giveKarma(L1PcInstance pc) {
		int karma = getKarma();
		if (karma != 0) {
			int karmaSign = Integer.signum(karma);
			int pcKarmaLevel = pc.getKarmaLevel();
			int pcKarmaLevelSign = Integer.signum(pcKarmaLevel);
			// �J���}�w�M�s�ׂ�5�{
			if (pcKarmaLevelSign != 0 && karmaSign != pcKarmaLevelSign) {
				karma *= 5;
			}
			// �J���}�͎~�߂��h�����v���C���[�ɐݒ�B�y�b�gor�T�����œ|�����ꍇ������B
			pc.addKarma((int) (karma * Config.RATE_KARMA));
		}
	}

	private void giveUbSeal() {
		if (getUbSealCount() != 0) { // UB�̗E�҂̏�
			L1UltimateBattle ub = UBTable.getInstance().getUb(getUbId());
			if (ub != null) {
				for (L1PcInstance pc : ub.getMembersArray()) {
					if (pc != null && !pc.isDead() && !pc.isGhost()) {
						L1ItemInstance item = pc.getInventory()
								.storeItem(41402, getUbSealCount());
						pc.sendPackets(new S_ServerMessage(403, item
								.getLogName())); // %0����ɓ���܂����B
					}
				}
			}
		}
	}

	public boolean is_storeDroped() {
		return _storeDroped;
	}

	public void set_storeDroped(boolean flag) {
		_storeDroped = flag;
	}

	private int _ubSealCount = 0; // UB�œ|���ꂽ���A�Q���҂ɗ^������E�҂̏؂̌�

	public int getUbSealCount() {
		return _ubSealCount;
	}

	public void setUbSealCount(int i) {
		_ubSealCount = i;
	}

	private int _ubId = 0; // UBID

	public int getUbId() {
		return _ubId;
	}

	public void setUbId(int i) {
		_ubId = i;
	}

	private void hide() {
		int npcid = getNpcTemplate().get_npcId();
		if (npcid == 45061 // �J�[�Y�h�X�p���g�C
				|| npcid == 45161 // �X�p���g�C
				|| npcid == 45181 // �X�p���g�C
				|| npcid == 45455) { // �f�b�h���[�X�p���g�C
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_Hide));
					setStatus(13);
					broadcastPacket(new S_NPCPack(this));
				}
			}
		} else if (npcid == 45682) { // �A���^���X
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(50);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_AntharasHide));
					setStatus(20);
					broadcastPacket(new S_NPCPack(this));
				}
			}
		} else if (npcid == 45067 // �o���[�n�[�s�[
				|| npcid == 45264 // �n�[�s�[
				|| npcid == 45452 // �n�[�s�[
				|| npcid == 45090 // �o���[�O���t�H��
				|| npcid == 45321 // �O���t�H��
				|| npcid == 45445) { // �O���t�H��
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_FLY);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_Moveup));
					setStatus(4);
					broadcastPacket(new S_NPCPack(this));
				}
			}
		} else if (npcid == 45681) { // �����h�r�I��
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(50);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_FLY);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_Moveup));
					setStatus(11);
					broadcastPacket(new S_NPCPack(this));
				}
			}
		} else if (npcid == 46107 // �e�[�x �}���h���S��(��)
				 || npcid == 46108) { // �e�[�x �}���h���S��(��)
			if (getMaxHp() / 4 > getCurrentHp()) {
				int rnd = _random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_Hide));
					setStatus(13);
					broadcastPacket(new S_NPCPack(this));
				}
			}
		}
	}

	public void initHide() {
		// �o������̉B��铮��
		// ����MOB�͈��̊m���Œn���ɐ�������ԂɁA
		// ���MOB�͔�񂾏�Ԃɂ��Ă���
		int npcid = getNpcTemplate().get_npcId();
		if (npcid == 45061 // �J�[�Y�h�X�p���g�C
				|| npcid == 45161 // �X�p���g�C
				|| npcid == 45181 // �X�p���g�C
				|| npcid == 45455) { // �f�b�h���[�X�p���g�C
			int rnd = _random.nextInt(3);
			if (1 > rnd) {
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(13);
			}
		} else if (npcid == 45045 // �N���C�S�[����
				|| npcid == 45126 // �X�g�[���S�[����
				|| npcid == 45134 // �X�g�[���S�[����
				|| npcid == 45281) { // �M�����X�g�[���S�[����
			int rnd = _random.nextInt(3);
			if (1 > rnd) {
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(4);
			}
		} else if (npcid == 45067 // �o���[�n�[�s�[
				|| npcid == 45264 // �n�[�s�[
				|| npcid == 45452 // �n�[�s�[
				|| npcid == 45090 // �o���[�O���t�H��
				|| npcid == 45321 // �O���t�H��
				|| npcid == 45445) { // �O���t�H��
			setHiddenStatus(HIDDEN_STATUS_FLY);
			setStatus(4);
		} else if (npcid == 45681) { // �����h�r�I��
			setHiddenStatus(HIDDEN_STATUS_FLY);
			setStatus(11);
		} else if (npcid == 46107 // �e�[�x �}���h���S��(��)
				 || npcid == 46108) { // �e�[�x �}���h���S��(��)
			int rnd = _random.nextInt(3);
			if (1 > rnd) {
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(13);
			}
		} else if (npcid >= 46125 && npcid <= 46128) {
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_ICE);
			setStatus(4);
		}
	}

	public void initHideForMinion(L1NpcInstance leader) {
		// �O���[�v�ɑ����郂���X�^�[�̏o������̉B��铮��i���[�_�[�Ɠ�������ɂ���j
		int npcid = getNpcTemplate().get_npcId();
		if (leader.getHiddenStatus() == HIDDEN_STATUS_SINK) {
			if (npcid == 45061 // �J�[�Y�h�X�p���g�C
					|| npcid == 45161 // �X�p���g�C
					|| npcid == 45181 // �X�p���g�C
					|| npcid == 45455) { // �f�b�h���[�X�p���g�C
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(13);
			} else if (npcid == 45045 // �N���C�S�[����
					|| npcid == 45126 // �X�g�[���S�[����
					|| npcid == 45134 // �X�g�[���S�[����
					|| npcid == 45281) { // �M�����X�g�[���S�[����
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(4);
			} else if (npcid == 46107 // �e�[�x �}���h���S��(��)
					 || npcid == 46108) { // �e�[�x �}���h���S��(��)
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(13);
			}
		} else if (leader.getHiddenStatus() == HIDDEN_STATUS_FLY) {
			if (npcid == 45067 // �o���[�n�[�s�[
					|| npcid == 45264 // �n�[�s�[
					|| npcid == 45452 // �n�[�s�[
					|| npcid == 45090 // �o���[�O���t�H��
					|| npcid == 45321 // �O���t�H��
					|| npcid == 45445) { // �O���t�H��
				setHiddenStatus(HIDDEN_STATUS_FLY);
				setStatus(4);
			} else if (npcid == 45681) { // �����h�r�I��
				setHiddenStatus(HIDDEN_STATUS_FLY);
				setStatus(11);
			}
		} else if (npcid >= 46125 && npcid <= 46128) {
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_ICE);
			setStatus(4);
		}
	}

	@Override
	protected void transform(int transformId) {
		super.transform(transformId);

		// DROP�̍Đݒ�
		getInventory().clearItems();
		DropTable.getInstance().setDrop(this, getInventory());
		getInventory().shuffle();
	}
}
