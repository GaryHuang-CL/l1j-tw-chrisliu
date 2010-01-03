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

package l1j.server.server.clientpackets;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.ClientThread;
import l1j.server.server.WarTimeController;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.HouseTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1ChatParty;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.serverpackets.S_ChangeName;
import l1j.server.server.serverpackets.S_CharTitle;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Resurrection;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_Trade;
import l1j.server.server.templates.L1House;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Pet;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Attr extends ClientBasePacket {

	private static final Logger _log = Logger.getLogger(C_Attr.class.getName());
	private static final String C_ATTR = "[C] C_Attr";

	private static final int HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
	private static final int HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	public C_Attr(byte abyte0[], ClientThread clientthread) throws Exception {
		super(abyte0);
		int i = readH();
		int c;
		String name;

		L1PcInstance pc = clientthread.getActiveChar();

		switch (i) {
		case 97: // %0�������ɉ������������Ă��܂��B�������܂����H�iY/N�j
			c = readC();
			L1PcInstance joinPc = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
			if (joinPc != null) {
				if (c == 0) { // No
					joinPc.sendPackets(new S_ServerMessage(96, pc
							.getName())); // \f1%0�͂��Ȃ��̗v�������₵�܂����B
				} else if (c == 1) { // Yes
					int clan_id = pc.getClanid();
					String clanName = pc.getClanname();
					L1Clan clan = L1World.getInstance().getClan(clanName);
					if (clan != null) {
						int maxMember = 0;
						int charisma = pc.getCha();
						boolean lv45quest = false;
						if (pc.getQuest().isEnd(L1Quest.QUEST_LEVEL45)) {
							lv45quest = true;
						}
						if (pc.getLevel() >= 50) { // Lv50�ȏ�
							if (lv45quest == true) { // Lv45�N�G�X�g�N���A�ς�
								maxMember = charisma * 9;
							} else {
								maxMember = charisma * 3;
							}
						} else { // Lv50����
							if (lv45quest == true) { // Lv45�N�G�X�g�N���A�ς�
								maxMember = charisma * 6;
							} else {
								maxMember = charisma * 2;
							}
						}
						if (Config.MAX_CLAN_MEMBER > 0) { // Clan�l���̏���̐ݒ肠��
							maxMember = Config.MAX_CLAN_MEMBER;
						}

						if (joinPc.getClanid() == 0) { // �N����������
							String clanMembersName[] = clan.getAllMembers();
							if (maxMember <= clanMembersName.length) { // �󂫂��Ȃ�
								joinPc.sendPackets( // %0�͂��Ȃ����������Ƃ��Ď󂯓���邱�Ƃ��ł��܂���B
										new S_ServerMessage(188, pc.getName()));
								return;
							}
							for (L1PcInstance clanMembers : clan
									.getOnlineClanMember()) {
								clanMembers.sendPackets(new S_ServerMessage(94,
										joinPc.getName())); // \f1%0�������̈���Ƃ��Ď󂯓�����܂����B
							}
							joinPc.setClanid(clan_id);
							joinPc.setClanname(clanName);
							joinPc.setClanRank(L1Clan.CLAN_RANK_PUBLIC);
							joinPc.setTitle("");
							joinPc.sendPackets(new S_CharTitle(joinPc.getId(), ""));
							joinPc.broadcastPacket(new S_CharTitle(joinPc
									.getId(), ""));
							joinPc.save(); // DB�ɃL�����N�^�[������������
							clan.addMemberName(joinPc.getName());
							joinPc.sendPackets(new S_ServerMessage(95,
									clanName)); // \f1%0�����ɉ������܂����B
						} else { // �N���������ς݁i�N�����A���j
							if (Config.CLAN_ALLIANCE) {
								changeClan(clientthread, pc, joinPc, maxMember);
							} else {
								joinPc.sendPackets(new S_ServerMessage(89)); // \f1���Ȃ��͂��łɌ����ɉ������Ă��܂��B
							}
						}
					}
				}
			}
			break;

		case 217: // %0������%1�����Ȃ��̌����Ƃ̐푈��]��ł��܂��B�푈�ɉ����܂����H�iY/N�j
		case 221: // %0�������~����]��ł��܂��B�󂯓���܂����H�iY/N�j
		case 222: // %0�������푈�̏I����]��ł��܂��B�I�����܂����H�iY/N�j
			c = readC();
			L1PcInstance enemyLeader = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			if (enemyLeader == null) {
				return;
			}
			pc.setTempID(0);
			String clanName = pc.getClanname();
			String enemyClanName = enemyLeader.getClanname();
			if (c == 0) { // No
				if (i == 217) {
					enemyLeader.sendPackets(new S_ServerMessage(236, clanName)); // %0���������Ȃ��̌����Ƃ̐푈�����₵�܂����B
				} else if (i == 221 || i == 222) {
					enemyLeader.sendPackets(new S_ServerMessage(237, clanName)); // %0���������Ȃ��̒�Ă����₵�܂����B
				}
			} else if (c == 1) { // Yes
				if (i == 217) {
					L1War war = new L1War();
					war.handleCommands(2, enemyClanName, clanName); // �͋[��J�n
				} else if (i == 221 || i == 222) {
					// �S�푈���X�g���擾
					for (L1War war : L1World.getInstance().getWarList()) {
						if (war.CheckClanInWar(clanName)) { // ���N�������s���Ă���푈�𔭌�
							if (i == 221) {
								war.SurrenderWar(enemyClanName, clanName); // �~��
							} else if (i == 222) {
								war.CeaseWar(enemyClanName, clanName); // �I��
							}
							break;
						}
					}
				}
			}
			break;

		case 252: // %0%s�����Ȃ��ƃA�C�e���̎����]��ł��܂��B������܂����H�iY/N�j
			c = readC();
			L1PcInstance trading_partner = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTradeID());
			if (trading_partner != null) {
				if (c == 0) // No
				{
					trading_partner.sendPackets(new S_ServerMessage(253, pc
							.getName())); // %0%d�͂��Ȃ��Ƃ̎���ɉ����܂���ł����B
					pc.setTradeID(0);
					trading_partner.setTradeID(0);
				} else if (c == 1) // Yes
				{
					pc.sendPackets(new S_Trade(trading_partner.getName()));
					trading_partner.sendPackets(new S_Trade(pc.getName()));
				}
			}
			break;

		case 321: // �܂������������ł����H�iY/N�j
			c = readC();
			L1PcInstance resusepc1 = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
			if (resusepc1 != null) { // �����X�N���[��
				if (c == 0) { // No
					;
				} else if (c == 1) { // Yes
					pc.sendPackets(new S_SkillSound(pc.getId(), '\346'));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), '\346'));
					// pc.resurrect(pc.getLevel());
					// pc.setCurrentHp(pc.getLevel());
					pc.resurrect(pc.getMaxHp() / 2);
					pc.setCurrentHp(pc.getMaxHp() / 2);
					pc.startHpRegeneration();
					pc.startMpRegeneration();
					pc.startMpRegenerationByDoll();
					pc.stopPcDeleteTimer();
					pc.sendPackets(new S_Resurrection(pc, resusepc1, 0));
					pc.broadcastPacket(new S_Resurrection(pc, resusepc1, 0));
					pc.sendPackets(new S_CharVisualUpdate(pc));
					pc.broadcastPacket(new S_CharVisualUpdate(pc));
				}
			}
			break;

		case 322: // �܂������������ł����H�iY/N�j
			c = readC();
			L1PcInstance resusepc2 = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
			if (resusepc2 != null) { // �j�����ꂽ �����X�N���[���A���U���N�V�����A�O���[�^�[ ���U���N�V����
				if (c == 0) { // No
					;
				} else if (c == 1) { // Yes
					pc.sendPackets(new S_SkillSound(pc.getId(), '\346'));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), '\346'));
					pc.resurrect(pc.getMaxHp());
					pc.setCurrentHp(pc.getMaxHp());
					pc.startHpRegeneration();
					pc.startMpRegeneration();
					pc.startMpRegenerationByDoll();
					pc.stopPcDeleteTimer();
					pc.sendPackets(new S_Resurrection(pc, resusepc2, 0));
					pc.broadcastPacket(new S_Resurrection(pc, resusepc2, 0));
					pc.sendPackets(new S_CharVisualUpdate(pc));
					pc.broadcastPacket(new S_CharVisualUpdate(pc));
					// EXP���X�g���Ă���AG-RES���|����ꂽ�AEXP���X�g�������S
					// �S�Ă𖞂����ꍇ�̂�EXP����
					if (pc.getExpRes() == 1 && pc.isGres() && pc.isGresValid()) {
						pc.resExp();
						pc.setExpRes(0);
						pc.setGres(false);
					}
				}
			}
			break;

		case 325: // �����̖��O�����߂Ă��������F
			c = readC(); // ?
			name = readS();
			L1PetInstance pet = (L1PetInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
			renamePet(pet, name);
			break;

		case 512: // �Ƃ̖��O�́H
			c = readC(); // ?
			name = readS();
			int houseId = pc.getTempID();
			pc.setTempID(0);
			if (name.length() <= 16) {
				L1House house = HouseTable.getInstance().getHouseTable(houseId);
				house.setHouseName(name);
				HouseTable.getInstance().updateHouse(house); // DB�ɏ�������
			} else {
				pc.sendPackets(new S_ServerMessage(513)); // �Ƃ̖��O���������܂��B
			}
			break;

		case 630: // %0%s�����Ȃ��ƌ�����]��ł��܂��B�����܂����H�iY/N�j
			c = readC();
			L1PcInstance fightPc = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getFightId());
			if (c == 0) {
				pc.setFightId(0);
				fightPc.setFightId(0);
				fightPc.sendPackets(new S_ServerMessage(631, pc.getName())); // %0%d�����Ȃ��Ƃ̌�����f��܂����B
			} else if (c == 1) {
				fightPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL,
						fightPc.getFightId(), fightPc.getId()));
				pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, pc
						.getFightId(), pc.getId()));
			}
			break;

		case 653: // ����������ƃ����O�͏����Ă��܂��܂��B������]�݂܂����H�iY/N�j
			c = readC();
			L1PcInstance target653 = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getPartnerId());
			if (c == 0) { // No
				return;
			} else if (c == 1) { // Yes
				if (target653 != null) {
					target653.setPartnerId(0);
					target653.save();
					target653.sendPackets(new S_ServerMessage(662)); // \f1���Ȃ��͌������Ă��܂���B
				} else {
					CharacterTable.getInstance().updatePartnerId(pc
							.getPartnerId());
				}
			}
			pc.setPartnerId(0);
			pc.save(); // DB�ɃL�����N�^�[������������
			pc.sendPackets(new S_ServerMessage(662)); // \f1���Ȃ��͌������Ă��܂���B
			break;

		case 654: // %0%s���Ȃ��ƌ������������Ă��܂��B%0�ƌ������܂����H�iY/N�j
			c = readC();
			L1PcInstance partner = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
			if (partner != null) {
				if (c == 0) { // No
					partner.sendPackets(new S_ServerMessage( // %0%s�͂��Ȃ��Ƃ̌��������₵�܂����B
							656, pc.getName()));
				} else if (c == 1) { // Yes
					pc.setPartnerId(partner.getId());
					pc.save();
					pc.sendPackets(new S_ServerMessage( // �F�̏j���̒��ŁA��l�̌������s���܂����B
							790));
					pc.sendPackets(new S_ServerMessage( // ���߂łƂ��������܂��I%0�ƌ������܂����B
							655, partner.getName()));

					partner.setPartnerId(pc.getId());
					partner.save();
					partner.sendPackets(new S_ServerMessage( // �F�̏j���̒��ŁA��l�̌������s���܂����B
							790));
					partner.sendPackets(new S_ServerMessage( // ���߂łƂ��������܂��I%0�ƌ������܂����B
							655, pc.getName()));
				}
			}
			break;

		// �R�[���N����
		case 729: // �N�傪�Ă�ł��܂��B�����ɉ����܂����H�iY/N�j
			c = readC();
			if (c == 0) { // No
				;
			} else if (c == 1) { // Yes
				callClan(pc);
			}
			break;

		case 738: // �o���l���񕜂���ɂ�%0�̃A�f�i���K�v�ł��B�o���l���񕜂��܂����H
			c = readC();
			if (c == 0) { // No
				;
			} else if (c == 1 && pc.getExpRes() == 1) { // Yes
				int cost = 0;
				int level = pc.getLevel();
				int lawful = pc.getLawful();
				if (level < 45) {
					cost = level * level * 100;
				} else {
					cost = level * level * 200;
				}
				if (lawful >= 0) {
					cost = (cost / 2);
				}
				if (pc.getInventory().consumeItem(L1ItemId.ADENA, cost)) {
					pc.resExp();
					pc.setExpRes(0);
				} else {
					pc.sendPackets(new S_ServerMessage(189)); // \f1�A�f�i���s�����Ă��܂��B
				}
			}
			break;

		case 951: // �`���b�g�p�[�e�B�[���҂������܂����H�iY/N�j
			c = readC();
			L1PcInstance chatPc = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getPartyID());
			if (chatPc != null) {
				if (c == 0) { // No
					chatPc.sendPackets(new S_ServerMessage(423, pc.getName())); // %0�����҂����ۂ��܂����B
					pc.setPartyID(0);
				} else if (c == 1) { // Yes
					if (chatPc.isInChatParty()) {
						if (chatPc.getChatParty().isVacancy() || chatPc
								.isGm()) {
							chatPc.getChatParty().addMember(pc);
						} else {
							chatPc.sendPackets(new S_ServerMessage(417)); // ����ȏ�p�[�e�B�[�����o�[���󂯓���邱�Ƃ͂ł��܂���B
						}
					} else {
						L1ChatParty chatParty = new L1ChatParty();
						chatParty.addMember(chatPc);
						chatParty.addMember(pc);
						chatPc.sendPackets(new S_ServerMessage(424, pc
								.getName())); // %0���p�[�e�B�[�ɓ���܂����B
					}
				}
			}
			break;

		case 953: // �p�[�e�B�[���҂������܂����H�iY/N�j
			c = readC();
			L1PcInstance target = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getPartyID());
			if (target != null) {
				if (c == 0) // No
				{
					target.sendPackets(new S_ServerMessage(423, pc.getName())); // %0�����҂����ۂ��܂����B
					pc.setPartyID(0);
				} else if (c == 1) // Yes
				{
					if (target.isInParty()) {
						// ���Ҏ傪�p�[�e�B�[��
						if (target.getParty().isVacancy() || target.isGm()) {
							// �p�[�e�B�[�ɋ󂫂�����
							target.getParty().addMember(pc);
						} else {
							// �p�[�e�B�[�ɋ󂫂��Ȃ�
							target.sendPackets(new S_ServerMessage(417)); // ����ȏ�p�[�e�B�[�����o�[���󂯓���邱�Ƃ͂ł��܂���B
						}
					} else {
						// ���Ҏ傪�p�[�e�B�[���łȂ�
						L1Party party = new L1Party();
						party.addMember(target);
						party.addMember(pc);
						target.sendPackets(new S_ServerMessage(424, pc
								.getName())); // %0���p�[�e�B�[�ɓ���܂����B
					}
				}
			}
			break;

		case 479: // �ǂ̔\�͒l�����コ���܂����H�istr�Adex�Aint�Acon�Awis�Acha�j
			if (readC() == 1) {
				String s = readS();
				if (!(pc.getLevel() - 50 > pc.getBonusStats())) {
					return;
				}
				if (s.toLowerCase().equals("str".toLowerCase())) {
					// if(l1pcinstance.get_str() < 255)
					if (pc.getBaseStr() < 35) {
						pc.addBaseStr((byte) 1); // �f��STR�l��+1
						pc.setBonusStats(pc.getBonusStats() + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save(); // DB�ɃL�����N�^�[������������
					} else {
						pc.sendPackets(new S_ServerMessage(481));
					}
				} else if (s.toLowerCase().equals("dex".toLowerCase())) {
					// if(l1pcinstance.get_dex() < 255)
					if (pc.getBaseDex() < 35) {
						pc.addBaseDex((byte) 1); // �f��DEX�l��+1
						pc.resetBaseAc();
						pc.setBonusStats(pc.getBonusStats() + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save(); // DB�ɃL�����N�^�[������������
					} else {
						pc.sendPackets(new S_ServerMessage(481)); // ��̔\�͒l�̍ő�l��25�ł��B���̔\�͒l��I�����Ă�������
					}
				} else if (s.toLowerCase().equals("con".toLowerCase())) {
					// if(l1pcinstance.get_con() < 255)
					if (pc.getBaseCon() < 35) {
						pc.addBaseCon((byte) 1); // �f��CON�l��+1
						pc.setBonusStats(pc.getBonusStats() + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save(); // DB�ɃL�����N�^�[������������
					} else {
						pc.sendPackets(new S_ServerMessage(481)); // ��̔\�͒l�̍ő�l��25�ł��B���̔\�͒l��I�����Ă�������
					}
				} else if (s.toLowerCase().equals("int".toLowerCase())) {
					// if(l1pcinstance.get_int() < 255)
					if (pc.getBaseInt() < 35) {
						pc.addBaseInt((byte) 1); // �f��INT�l��+1
						pc.setBonusStats(pc.getBonusStats() + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save(); // DB�ɃL�����N�^�[������������
					} else {
						pc.sendPackets(new S_ServerMessage(481)); // ��̔\�͒l�̍ő�l��25�ł��B���̔\�͒l��I�����Ă�������
					}
				} else if (s.toLowerCase().equals("wis".toLowerCase())) {
					// if(l1pcinstance.get_wis() < 255)
					if (pc.getBaseWis() < 35) {
						pc.addBaseWis((byte) 1); // �f��WIS�l��+1
						pc.resetBaseMr();
						pc.setBonusStats(pc.getBonusStats() + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save(); // DB�ɃL�����N�^�[������������
					} else {
						pc.sendPackets(new S_ServerMessage(481)); // ��̔\�͒l�̍ő�l��25�ł��B���̔\�͒l��I�����Ă�������
					}
				} else if (s.toLowerCase().equals("cha".toLowerCase())) {
					// if(l1pcinstance.get_cha() < 255)
					if (pc.getBaseCha() < 35) {
						pc.addBaseCha((byte) 1); // �f��CHA�l��+1
						pc.setBonusStats(pc.getBonusStats() + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save(); // DB�ɃL�����N�^�[������������
					} else {
						pc.sendPackets(new S_ServerMessage(481)); // ��̔\�͒l�̍ő�l��25�ł��B���̔\�͒l��I�����Ă�������
					}
				}
			}
			break;
		default:
			break;
		}
	}

	private void changeClan(ClientThread clientthread,
			L1PcInstance pc, L1PcInstance joinPc, int maxMember) {
		int clanId = pc.getClanid();
		String clanName = pc.getClanname();
		L1Clan clan = L1World.getInstance().getClan(clanName);
		String clanMemberName[] = clan.getAllMembers();
		int clanNum = clanMemberName.length;

		int oldClanId = joinPc.getClanid();
		String oldClanName = joinPc.getClanname();
		L1Clan oldClan = L1World.getInstance().getClan(oldClanName);
		String oldClanMemberName[] = oldClan.getAllMembers();
		int oldClanNum = oldClanMemberName.length;
		if (clan != null && oldClan != null && joinPc.isCrown() && // �������N��
				joinPc.getId() == oldClan.getLeaderId()) {
			if (maxMember < clanNum + oldClanNum) { // �󂫂��Ȃ�
				joinPc.sendPackets( // %0�͂��Ȃ����������Ƃ��Ď󂯓���邱�Ƃ��ł��܂���B
						new S_ServerMessage(188, pc.getName()));
				return;
			}
			L1PcInstance clanMember[] = clan.getOnlineClanMember();
			for (int cnt = 0; cnt < clanMember.length; cnt++) {
				clanMember[cnt].sendPackets(new S_ServerMessage(94, joinPc
						.getName())); // \f1%0�������̈���Ƃ��Ď󂯓�����܂����B
			}

			for (int i = 0; i < oldClanMemberName.length; i++) {
				L1PcInstance oldClanMember = L1World.getInstance().getPlayer(
						oldClanMemberName[i]);
				if (oldClanMember != null) { // �I�����C�����̋��N���������o�[
					oldClanMember.setClanid(clanId);
					oldClanMember.setClanname(clanName);
					// �����A���ɉ��������N��̓K�[�f�B�A��
					// �N�傪�A��Ă����������͌��K��
					if (oldClanMember.getId() == joinPc.getId()) {
						oldClanMember.setClanRank(L1Clan.CLAN_RANK_GUARDIAN);
					} else {
						oldClanMember.setClanRank(L1Clan.CLAN_RANK_PROBATION);
					}
					try {
						// DB�ɃL�����N�^�[������������
						oldClanMember.save();
					} catch (Exception e) {
						_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
					}
					clan.addMemberName(oldClanMember.getName());
					oldClanMember.sendPackets(new S_ServerMessage(95,
							clanName)); // \f1%0�����ɉ������܂����B
				} else { // �I�t���C�����̋��N���������o�[
					try {
						L1PcInstance offClanMember = CharacterTable
								.getInstance().restoreCharacter(
										oldClanMemberName[i]);
						offClanMember.setClanid(clanId);
						offClanMember.setClanname(clanName);
						offClanMember.setClanRank(L1Clan.CLAN_RANK_PROBATION);
						offClanMember.save(); // DB�ɃL�����N�^�[������������
						clan.addMemberName(offClanMember.getName());
					} catch (Exception e) {
						_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
					}
				}
			}
			// ���N�����폜
			String emblem_file = String.valueOf(oldClanId);
			File file = new File("emblem/" + emblem_file);
			file.delete();
			ClanTable.getInstance().deleteClan(oldClanName);
		}
	}

	private static void renamePet(L1PetInstance pet, String name) {
		if (pet == null || name == null) {
			throw new NullPointerException();
		}

		int petItemObjId = pet.getItemObjId();
		L1Pet petTemplate = PetTable.getInstance().getTemplate(petItemObjId);
		if (petTemplate == null) {
			throw new NullPointerException();
		}

		L1PcInstance pc = (L1PcInstance) pet.getMaster();
		if (PetTable.isNameExists(name)) {
			pc.sendPackets(new S_ServerMessage(327)); // �������O�����łɑ��݂��Ă��܂��B
			return;
		}
		L1Npc l1npc = NpcTable.getInstance().getTemplate(pet.getNpcId());
		if (!(pet.getName().equalsIgnoreCase(l1npc.get_name())) ) {
			pc.sendPackets(new S_ServerMessage(326)); // ��x���߂����O�͕ύX�ł��܂���B
			return;
		}
 		pet.setName(name);
		petTemplate.set_name(name);
		PetTable.getInstance().storePet(petTemplate); // DB�ɏ�������
		L1ItemInstance item = pc.getInventory().getItem(pet.getItemObjId());
		pc.getInventory().updateItem(item); 
		pc.sendPackets(new S_ChangeName(pet.getId(), name));
		pc.broadcastPacket(new S_ChangeName(pet.getId(), name));
	}

	private void callClan(L1PcInstance pc) {
		L1PcInstance callClanPc = (L1PcInstance) L1World.getInstance()
				.findObject(pc.getTempID());
		pc.setTempID(0);
		if (callClanPc == null) {
			return;
		}
		if (!pc.getMap().isEscapable() && !pc.isGm()) {
			// ���ӂ̃G�l���M�[���e���|�[�g��W�Q���Ă��܂��B���̂��߁A�����Ńe���|�[�g�͎g�p�ł��܂���B
			pc.sendPackets(new S_ServerMessage(647));
			L1Teleport.teleport(pc, pc.getLocation(), pc.getHeading(), false);
			return;
		}
		if (pc.getId() != callClanPc.getCallClanId()) {
			return;
		}

		boolean isInWarArea = false;
		int castleId = L1CastleLocation.getCastleIdByArea(callClanPc);
		if (castleId != 0) {
			isInWarArea = true;
			if (WarTimeController.getInstance().isNowWar(castleId)) {
				isInWarArea = false; // �푈���Ԓ��͊����ł��g�p�\
			}
		}
		short mapId = callClanPc.getMapId();
		if (mapId != 0 && mapId != 4 && mapId != 304 || isInWarArea) {
			// \f1���Ȃ��̃p�[�g�i�[�͍����Ȃ����s���Ȃ����Ńv���C���ł��B
			pc.sendPackets(new S_ServerMessage(547));
			return;
		}

		L1Map map = callClanPc.getMap();
		int locX = callClanPc.getX();
		int locY = callClanPc.getY();
		int heading = callClanPc.getCallClanHeading();
		locX += HEADING_TABLE_X[heading];
		locY += HEADING_TABLE_Y[heading];
		heading = (heading + 4) % 4;

		boolean isExsistCharacter = false;
		for (L1Object object : L1World.getInstance()
				.getVisibleObjects(callClanPc, 1)) {
			if (object instanceof L1Character) {
				L1Character cha = (L1Character) object;
				if (cha.getX() == locX && cha.getY() == locY
						&& cha.getMapId() == mapId) {
					isExsistCharacter = true;
					break;
				}
			}
		}

		if (locX == 0 && locY == 0 || !map.isPassable(locX, locY)
				|| isExsistCharacter) {
			// ��Q���������Ă����܂ňړ����邱�Ƃ��ł��܂���B
			pc.sendPackets(new S_ServerMessage(627));
			return;
		}
		L1Teleport.teleport(pc, locX, locY, mapId, heading, true, L1Teleport
				.CALL_CLAN);
	}

	@Override
	public String getType() {
		return C_ATTR;
	}
}