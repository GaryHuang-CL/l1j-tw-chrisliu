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

import java.util.List;
import java.util.logging.Logger;

import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1WarSpawn;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_CastleMaster;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Npc;

public class L1CrownInstance extends L1NpcInstance {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger _log = Logger.getLogger(L1CrownInstance.class
			.getName());

	public L1CrownInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance player) {
		boolean in_war = false;
		if (player.getClanid() == 0) { // �N����������
			return;
		}
		String playerClanName = player.getClanname();
		L1Clan clan = L1World.getInstance().getClan(playerClanName);
		if (clan == null) {
			return;
		}
		if (!player.isCrown()) { // �N��ȊO
			return;
		}
		if (player.getTempCharGfx() != 0 && // �ϐg��
				player.getTempCharGfx() != 1) {
			return;
		}
		if (player.getId() != clan.getLeaderId()) { // ������ȊO
			return;
		}
		if (!checkRange(player)) { // �N���E����1�Z���ȓ�
			return;
		}
		if (clan.getCastleId() != 0) {
			// ���N����
			// ���Ȃ��͂��łɏ�����L���Ă���̂ŁA���̏����邱�Ƃ͏o���܂���B
			player.sendPackets(new S_ServerMessage(474));
			return;
		}

		// �N���E���̍��W����castle_id���擾
		int castle_id = L1CastleLocation
				.getCastleId(getX(), getY(), getMapId());

		// �z�����Ă��邩�`�F�b�N�B�A���A��傪���Ȃ��ꍇ�͕z���s�v
		boolean existDefenseClan = false;
		L1Clan defence_clan = null;
		for (L1Clan defClan : L1World.getInstance().getAllClans()) {
			if (castle_id == defClan.getCastleId()) {
				// ���̏��N����
				defence_clan = L1World.getInstance().getClan(
						defClan.getClanName());
				existDefenseClan = true;
				break;
			}
		}
		List<L1War> wars = L1World.getInstance().getWarList(); // �S�푈���X�g���擾
		for (L1War war : wars) {
			if (castle_id == war.GetCastleId()) { // �������̐푈
				in_war = war.CheckClanInWar(playerClanName);
				break;
			}
		}
		if (existDefenseClan && in_war == false) { // ��傪���āA�z�����Ă��Ȃ��ꍇ
			return;
		}

		// clan_data��hascastle���X�V���A�L�����N�^�[�ɃN���E����t����
		if (existDefenseClan && defence_clan != null) { // ���̏��N����������
			defence_clan.setCastleId(0);
			ClanTable.getInstance().updateClan(defence_clan);
			L1PcInstance defence_clan_member[] = defence_clan
					.getOnlineClanMember();
			for (int m = 0; m < defence_clan_member.length; m++) {
				if (defence_clan_member[m].getId() == defence_clan
						.getLeaderId()) { // ���̏��N�����̌N��
					defence_clan_member[m].sendPackets(new S_CastleMaster(0,
							defence_clan_member[m].getId()));
					defence_clan_member[m].broadcastPacket(new S_CastleMaster(
							0, defence_clan_member[m].getId()));
					break;
				}
			}
		}
		clan.setCastleId(castle_id);
		ClanTable.getInstance().updateClan(clan);
		player.sendPackets(new S_CastleMaster(castle_id, player.getId()));
		player.broadcastPacket(new S_CastleMaster(castle_id, player.getId()));

		// �N�������ȊO���X�ɋ����e���|�[�g
		int[] loc = new int[3];
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (pc.getClanid() != player.getClanid() && !pc.isGm()) {

				if (L1CastleLocation.checkInWarArea(castle_id, pc)) {
					// �����ɋ���
					loc = L1CastleLocation.getGetBackLoc(castle_id);
					int locx = loc[0];
					int locy = loc[1];
					short mapid = (short) loc[2];
					L1Teleport.teleport(pc, locx, locy, mapid, 5, true);
				}
			}
		}

		// ���b�Z�[�W�\��
		for (L1War war : wars) {
			if (war.CheckClanInWar(playerClanName) && existDefenseClan) {
				// ���N�������Q�����ŁA��傪���
				war.WinCastleWar(playerClanName);
				break;
			}
		}
		L1PcInstance[] clanMember = clan.getOnlineClanMember();

		if (clanMember.length > 0) {
			// ���苒���܂����B
			S_ServerMessage s_serverMessage = new S_ServerMessage(643);
			for (L1PcInstance pc : clanMember) {
				pc.sendPackets(s_serverMessage);
			}
		}

		// �N���E��������
		deleteMe();

		// �^���[�������čďo��������
		for (L1Object l1object : L1World.getInstance().getObject()) {
			if (l1object instanceof L1TowerInstance) {
				L1TowerInstance tower = (L1TowerInstance) l1object;
				if (L1CastleLocation.checkInWarArea(castle_id, tower)) {
					tower.deleteMe();
				}
			}
		}
		L1WarSpawn warspawn = new L1WarSpawn();
		warspawn.SpawnTower(castle_id);

		// �������ɖ߂�
		for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
			if (L1CastleLocation.checkInWarArea(castle_id, door)) {
				door.repairGate();
			}
		}
	}

	@Override
	public void deleteMe() {
		_destroyed = true;
		if (getInventory() != null) {
			getInventory().clearItems();
		}
		allTargetClear();
		_master = null;
		L1World.getInstance().removeVisibleObject(this);
		L1World.getInstance().removeObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			pc.removeKnownObject(this);
			pc.sendPackets(new S_RemoveObject(this));
		}
		removeAllKnownObjects();
	}

	private boolean checkRange(L1PcInstance pc) {
		return (getX() - 1 <= pc.getX() && pc.getX() <= getX() + 1
				&& getY() - 1 <= pc.getY() && pc.getY() <= getY() + 1);
	}
}
