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

import java.util.List;
import java.util.logging.Logger;

import l1j.server.server.ClientThread;
import l1j.server.server.WarTimeController;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_ServerMessage;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_War extends ClientBasePacket {

	private static final String C_WAR = "[C] C_War";
	private static Logger _log = Logger.getLogger(C_War.class.getName());

	public C_War(byte abyte0[], ClientThread clientthread) throws Exception {
		super(abyte0);
		int type = readC();
		String s = readS();

		L1PcInstance player = clientthread.getActiveChar();
		String playerName = player.getName();
		String clanName = player.getClanname();
		int clanId = player.getClanid();

		if (!player.isCrown()) { // �N��ȊO
			player.sendPackets(new S_ServerMessage(478)); // \f1�v�����X�ƃv�����Z�X�̂ݐ푈��z���ł��܂��B
			return;
		}
		if (clanId == 0) { // �N����������
			player.sendPackets(new S_ServerMessage(272)); // \f1�푈���邽�߂ɂ͂܂�������n�݂��Ȃ���΂Ȃ�܂���B
			return;
		}
		L1Clan clan = L1World.getInstance().getClan(clanName);
		if (clan == null) { // ���N������������Ȃ�
			return;
		}

		if (player.getId() != clan.getLeaderId()) { // ������
			player.sendPackets(new S_ServerMessage(478)); // \f1�v�����X�ƃv�����Z�X�̂ݐ푈��z���ł��܂��B
			return;
		}

		if (clanName.toLowerCase().equals(s.toLowerCase())) { // ���N�������w��
			return;
		}

		L1Clan enemyClan = null;
		String enemyClanName = null;
		for (L1Clan checkClan : L1World.getInstance().getAllClans()) { // �N���������`�F�b�N
			if (checkClan.getClanName().toLowerCase().equals(s.toLowerCase())) {
				enemyClan = checkClan;
				enemyClanName = checkClan.getClanName();
				break;
			}
		}
		if (enemyClan == null) { // ����̃N������������Ȃ�����
			return;
		}

		boolean inWar = false;
		List<L1War> warList = L1World.getInstance().getWarList(); // �S�푈���X�g���擾
		for (L1War war : warList) {
			if (war.CheckClanInWar(clanName)) { // ���N���������ɐ푈��
				if (type == 0) { // ���z��
					player.sendPackets(new S_ServerMessage(234)); // \f1���Ȃ��̌����͂��łɐ푈���ł��B
					return;
				}
				inWar = true;
				break;
			}
		}
		if (!inWar && (type == 2 || type == 3)) { // ���N�������푈���ȊO�ŁA�~���܂��͏I��
			return;
		}

		if (clan.getCastleId() != 0) { // ���N���������
			if (type == 0) { // ���z��
				player.sendPackets(new S_ServerMessage(474)); // ���Ȃ��͂��łɏ�����L���Ă���̂ŁA���̏����邱�Ƃ͏o���܂���B
				return;
			} else if (type == 2 || type == 3) { // �~���A�I��
				return;
			}
		}

		if (enemyClan.getCastleId() == 0 && // ����N���������ł͂Ȃ��A���L������Lv15�ȉ�
				player.getLevel() <= 15) {
			player.sendPackets(new S_ServerMessage(232)); // \f1���x��15�ȉ��̌N��͐��z���ł��܂���B
			return;
		}

		if (enemyClan.getCastleId() != 0 && // ����N���������ŁA���L������Lv25����
				player.getLevel() < 25) {
			player.sendPackets(new S_ServerMessage(475)); // �U����錾����ɂ̓��x��25�ɒB���Ă��Ȃ���΂Ȃ�܂���B
			return;
		}

		if (enemyClan.getCastleId() != 0) { // ����N���������
			int castle_id = enemyClan.getCastleId();
			if (WarTimeController.getInstance().isNowWar(castle_id)) { // �푈���ԓ�
				L1PcInstance clanMember[] = clan.getOnlineClanMember();
				for (int k = 0; k < clanMember.length; k++) {
					if (L1CastleLocation.checkInWarArea(castle_id,
							clanMember[k])) {
						player.sendPackets(new S_ServerMessage(477)); // ���Ȃ����܂ޑS�Ă̌���������̊O�ɏo�Ȃ���΍U���͐錾�ł��܂���B
						return;
					}
				}
				boolean enemyInWar = false;
				for (L1War war : warList) {
					if (war.CheckClanInWar(enemyClanName)) { // ����N���������ɐ푈��
						if (type == 0) { // ���z��
							war.DeclareWar(clanName, enemyClanName);
							war.AddAttackClan(clanName);
						} else if (type == 2 || type == 3) {
							if (!war
									.CheckClanInSameWar(clanName, enemyClanName)) { // ���N�����Ƒ���N�������ʂ̐푈
								return;
							}
							if (type == 2) { // �~��
								war.SurrenderWar(clanName, enemyClanName);
							} else if (type == 3) { // �I��
								war.CeaseWar(clanName, enemyClanName);
							}
						}
						enemyInWar = true;
						break;
					}
				}
				if (!enemyInWar && type == 0) { // ����N�������푈���ȊO�ŁA���z��
					L1War war = new L1War();
					war.handleCommands(1, clanName, enemyClanName); // �U���J�n
				}
			} else { // �푈���ԊO
				if (type == 0) { // ���z��
					player.sendPackets(new S_ServerMessage(476)); // �܂��U���̎��Ԃł͂���܂���B
				}
			}
		} else { // ����N���������ł͂Ȃ�
			boolean enemyInWar = false;
			for (L1War war : warList) {
				if (war.CheckClanInWar(enemyClanName)) { // ����N���������ɐ푈��
					if (type == 0) { // ���z��
						player.sendPackets(new S_ServerMessage(236,
								enemyClanName)); // %0���������Ȃ��̌����Ƃ̐푈�����₵�܂����B
						return;
					} else if (type == 2 || type == 3) { // �~���܂��͏I��
						if (!war.CheckClanInSameWar(clanName, enemyClanName)) { // ���N�����Ƒ���N�������ʂ̐푈
							return;
						}
					}
					enemyInWar = true;
					break;
				}
			}
			if (!enemyInWar && (type == 2 || type == 3)) { // ����N�������푈���ȊO�ŁA�~���܂��͏I��
				return;
			}

			// �U���ł͂Ȃ��ꍇ�A����̌�����̏��F���K�v
			L1PcInstance enemyLeader = L1World.getInstance().getPlayer(
					enemyClan.getLeaderName());

			if (enemyLeader == null) { // ����̌����傪������Ȃ�����
				player.sendPackets(new S_ServerMessage(218, enemyClanName)); // \f1%0�����̌N��͌��݃��[���h�ɋ��܂���B
				return;
			}

			if (type == 0) { // ���z��
				enemyLeader.setTempID(player.getId()); // ����̃I�u�W�F�N�gID��ۑ����Ă���
				enemyLeader.sendPackets(new S_Message_YN(217, clanName,
						playerName)); // %0������%1�����Ȃ��̌����Ƃ̐푈��]��ł��܂��B�푈�ɉ����܂����H�iY/N�j
			} else if (type == 2) { // �~��
				enemyLeader.setTempID(player.getId()); // ����̃I�u�W�F�N�gID��ۑ����Ă���
				enemyLeader.sendPackets(new S_Message_YN(221, clanName)); // %0�������~����]��ł��܂��B�󂯓���܂����H�iY/N�j
			} else if (type == 3) { // �I��
				enemyLeader.setTempID(player.getId()); // ����̃I�u�W�F�N�gID��ۑ����Ă���
				enemyLeader.sendPackets(new S_Message_YN(222, clanName)); // %0�������푈�̏I����]��ł��܂��B�I�����܂����H�iY/N�j
			}
		}
	}

	@Override
	public String getType() {
		return C_WAR;
	}

}
