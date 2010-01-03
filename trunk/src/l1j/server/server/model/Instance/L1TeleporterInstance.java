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

import java.util.Random;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.templates.L1Npc;

// Referenced classes of package l1j.server.server.model:
// L1NpcInstance, L1Teleport, L1NpcTalkData, L1PcInstance,
// L1TeleporterPrices, L1TeleportLocations

public class L1TeleporterInstance extends L1NpcInstance {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public L1TeleporterInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance player) {
		L1Attack attack = new L1Attack(player, this);
		attack.calcHit();
		attack.action();
	}

	@Override
	public void onTalkAction(L1PcInstance player) {
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		int npcid = getNpcTemplate().get_npcId();
		L1Quest quest = player.getQuest();
		String htmlid = null;

		if (talking != null) {
			if (npcid == 50014) { // �f�B����
				if (player.isWizard()) { // �E�B�U�[�h
					if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1
							&& !player.getInventory().checkItem(40579)) { // �A���f�b�h�̍�
						htmlid = "dilong1";
					} else {
						htmlid = "dilong3";
					}
				}
			} else if (npcid == 70779) { // �Q�[�g�A���g
				if (player.getTempCharGfx() == 1037) { // �W���C�A���g�A���g�ϐg
					htmlid = "ants3";
				} else if (player.getTempCharGfx() == 1039) {// �W���C�A���g�A���g�\���W���[�ϐg
					if (player.isCrown()) { // �N��
						if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1) {
							if (player.getInventory().checkItem(40547)) { // �Z�������̈�i
								htmlid = "antsn";
							} else {
								htmlid = "ants1";
							}
						} else { // Step1�ȊO
							htmlid = "antsn";
						}
					} else { // �N��ȊO
						htmlid = "antsn";
					}
				}
			} else if (npcid == 70853) { // �t�F�A���[�v�����Z�X
				if (player.isElf()) { // �G���t
					if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1) {
						if (!player.getInventory().checkItem(40592)) { // ���ꂽ���쏑
							Random random = new Random();
							if (random.nextInt(100) < 50) { // 50%�Ń_�[�N�}�[���_���W����
								htmlid = "fairyp2";
							} else { // �_�[�N�G���t�_���W����
								htmlid = "fairyp1";
							}
						}
					}
				}
			} else if (npcid == 50031) { // �Z�s�A
				if (player.isElf()) { // �G���t
					if (quest.get_step(L1Quest.QUEST_LEVEL45) == 2) {
						if (!player.getInventory().checkItem(40602)) { // �u���[�t���[�g
							htmlid = "sepia1";
						}
					}
				}
			} else if (npcid == 50043) { // �����_
				if (quest.get_step(L1Quest.QUEST_LEVEL50) == L1Quest.QUEST_END) {
					htmlid = "ramuda2";
				} else if (quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // �f�B�K���f�B�����Ӎς�
					if (player.isCrown()) { // �N��
						if (_isNowDely) { // �e���|�[�g�f�B���C��
							htmlid = "ramuda4";
						} else {
							htmlid = "ramudap1";
						}
					} else { // �N��ȊO
						htmlid = "ramuda1";
					}
				} else {
					htmlid = "ramuda3";
				}
			}
			// �̂����̃e���|�[�^�[
			else if (npcid == 50082) {
				if (player.getLevel() < 13) {
					htmlid = "en0221";
				} else {
					if (player.isElf()) {
						htmlid = "en0222e";
					} else if (player.isDarkelf()) {
						htmlid = "en0222d";
					} else {
						htmlid = "en0222";
					}
				}
			}
			// �o���j�A
			else if (npcid == 50001) {
				if (player.isElf()) {
					htmlid = "barnia3";
				} else if (player.isKnight() || player.isCrown()) {
					htmlid = "barnia2";
				} else if (player.isWizard() || player.isDarkelf()) {
					htmlid = "barnia1";
				}
			}

			// html�\��
			if (htmlid != null) { // htmlid���w�肳��Ă���ꍇ
				player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
			} else {
				if (player.getLawful() < -1000) { // �v���C���[���J�I�e�B�b�N
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
				} else {
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
				}
			}
		} else {
			_log.finest((new StringBuilder())
					.append("No actions for npc id : ").append(objid)
					.toString());
		}
	}

	@Override
	public void onFinalAction(L1PcInstance player, String action) {
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		if (action.equalsIgnoreCase("teleportURL")) {
			L1NpcHtml html = new L1NpcHtml(talking.getTeleportURL());
			player.sendPackets(new S_NPCTalkReturn(objid, html));
		} else if (action.equalsIgnoreCase("teleportURLA")) {
			L1NpcHtml html = new L1NpcHtml(talking.getTeleportURLA());
			player.sendPackets(new S_NPCTalkReturn(objid, html));
		}
		if (action.startsWith("teleport ")) {
			_log.finest((new StringBuilder()).append("Setting action to : ")
					.append(action).toString());
			doFinalAction(player, action);
		}
	}

	private void doFinalAction(L1PcInstance player, String action) {
		int objid = getId();

		int npcid = getNpcTemplate().get_npcId();
		String htmlid = null;
		boolean isTeleport = true;

		if (npcid == 50014) { // �f�B����
			if (!player.getInventory().checkItem(40581)) { // �A���f�b�h�̃L�[
				isTeleport = false;
				htmlid = "dilongn";
			}
		} else if (npcid == 50043) { // �����_
			if (_isNowDely) { // �e���|�[�g�f�B���C��
				isTeleport = false;
			}
		} else if (npcid == 50625) { // �Ñ�l�iLv50�N�G�X�g�Ñ�̋��2F�j
			if (_isNowDely) { // �e���|�[�g�f�B���C��
				isTeleport = false;
			}
		}

		if (isTeleport) { // �e���|�[�g���s
			try {
				// �~���[�^���g�A���g�_���W����(�N��Lv30�N�G�X�g)
				if (action.equalsIgnoreCase("teleport mutant-dungen")) {
					// 3�}�X�ȓ���Pc
					for (L1PcInstance otherPc : L1World.getInstance()
							.getVisiblePlayer(player, 3)) {
						if (otherPc.getClanid() == player.getClanid()
								&& otherPc.getId() != player.getId()) {
							L1Teleport.teleport(otherPc, 32740, 32800, (short) 217, 5,
									true);
						}
					}
					L1Teleport.teleport(player, 32740, 32800, (short) 217, 5,
							true);
				}
				// �����̃_���W�����i�E�B�U�[�hLv30�N�G�X�g�j
				else if (action.equalsIgnoreCase("teleport mage-quest-dungen")) {
					L1Teleport.teleport(player, 32791, 32788, (short) 201, 5,
							true);
				} else if (action.equalsIgnoreCase("teleport 29")) { // �����_
					L1PcInstance kni = null;
					L1PcInstance elf = null;
					L1PcInstance wiz = null;
					// 3�}�X�ȓ���Pc
					for (L1PcInstance otherPc : L1World.getInstance()
							.getVisiblePlayer(player, 3)) {
						L1Quest quest = otherPc.getQuest();
						if (otherPc.isKnight() // �i�C�g
								&& quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // �f�B�K���f�B�����Ӎς�
							if (kni == null) {
								kni = otherPc;
							}
						} else if (otherPc.isElf() // �G���t
								&& quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // �f�B�K���f�B�����Ӎς�
							if (elf == null) {
								elf = otherPc;
							}
						} else if (otherPc.isWizard() // �E�B�U�[�h
								&& quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // �f�B�K���f�B�����Ӎς�
							if (wiz == null) {
								wiz = otherPc;
							}
						}
					}
					if (kni != null && elf != null && wiz != null) { // �S�N���X�����Ă���
						L1Teleport.teleport(player, 32723, 32850, (short) 2000,
								2, true);
						L1Teleport.teleport(kni, 32750, 32851, (short) 2000, 6,
								true);
						L1Teleport.teleport(elf, 32878, 32980, (short) 2000, 6,
								true);
						L1Teleport.teleport(wiz, 32876, 33003, (short) 2000, 0,
								true);
						TeleportDelyTimer timer = new TeleportDelyTimer();
						GeneralThreadPool.getInstance().execute(timer);
					}
				} else if (action.equalsIgnoreCase("teleport barlog")) { // �Ñ�l�iLv50�N�G�X�g�Ñ�̋��2F�j
					L1Teleport.teleport(player, 32755, 32844, (short) 2002, 5,
							true);
					TeleportDelyTimer timer = new TeleportDelyTimer();
					GeneralThreadPool.getInstance().execute(timer);
				}
			} catch (Exception e) {
			}
		}
		if (htmlid != null) { // �\������html������ꍇ
			player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
		}
	}

	class TeleportDelyTimer implements Runnable {

		public TeleportDelyTimer() {
		}

		public void run() {
			try {
				_isNowDely = true;
				Thread.sleep(900000); // 15��
			} catch (Exception e) {
				_isNowDely = false;
			}
			_isNowDely = false;
		}
	}

	private boolean _isNowDely = false;
	private static Logger _log = Logger
			.getLogger(l1j.server.server.model.Instance.L1TeleporterInstance.class
					.getName());

}