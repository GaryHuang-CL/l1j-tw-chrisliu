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

package l1j.server.server.utils;

import java.util.HashSet;
import java.util.logging.Logger;
import java.util.Random;

import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_DollPack;
import l1j.server.server.serverpackets.S_SkillIconWindShackle;
import l1j.server.server.serverpackets.S_MapID;
import l1j.server.server.serverpackets.S_OtherCharPacks;
import l1j.server.server.serverpackets.S_OwnCharPack;
import l1j.server.server.serverpackets.S_PetPack;
import l1j.server.server.serverpackets.S_SummonPack;
import static l1j.server.server.model.skill.L1SkillId.*;

// Referenced classes of package l1j.server.server.utils:
// FaceToFace

public class Teleportation {

	private static Logger _log = Logger
			.getLogger(Teleportation.class.getName());

	private static Random _random = new Random();

	private Teleportation() {
	}

	public static void Teleportation(L1PcInstance pc) {
		if (pc.isDead() || pc.isTeleport()) {
			return;
		}

		int x = pc.getTeleportX();
		int y = pc.getTeleportY();
		short mapId = pc.getTeleportMapId();
		int head = pc.getTeleportHeading();

		// �e���|�[�g�悪�s���ł���Ό��̍��W��(GM�͏���)
		L1Map map = L1WorldMap.getInstance().getMap(mapId);

		if (!map.isInMap(x, y) && !pc.isGm()) {
			x = pc.getX();
			y = pc.getY();
			mapId = pc.getMapId();
		}

		pc.setTeleport(true);

		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan != null) {
			if (clan.getWarehouseUsingChar() == pc.getId()) { // ���L�������N�����q�Ɏg�p��
				clan.setWarehouseUsingChar(0); // �N�����q�ɂ̃��b�N������
			}
		}

		L1World.getInstance().moveVisibleObject(pc, mapId);
		pc.setLocation(x, y, mapId);
		pc.setHeading(head);
		pc.sendPackets(new S_MapID(pc.getMapId(), pc.getMap().isUnderwater()));

		if (pc.isReserveGhost()) { // �S�[�X�g��ԉ���
			pc.endGhost();
		}
		if (pc.isGhost() || pc.isGmInvis()) {
		} else if (pc.isInvisble()) {
			pc.broadcastPacketForFindInvis(new S_OtherCharPacks(pc, true),
					true);
		} else {
			pc.broadcastPacket(new S_OtherCharPacks(pc));
		}
		pc.sendPackets(new S_OwnCharPack(pc));

		pc.removeAllKnownObjects();
		pc.sendVisualEffectAtTeleport(); // �N���E���A�ŁA�������̎��o���ʂ�\��
		pc.updateObject();
		// spr�ԍ�6310, 5641�̕ϐg���Ƀe���|�[�g����ƃe���|�[�g��Ɉړ��ł��Ȃ��Ȃ�
		// ����𒅒E����ƈړ��ł���悤�ɂȂ邽�߁AS_CharVisualUpdate�𑗐M����
		pc.sendPackets(new S_CharVisualUpdate(pc));

		pc.killSkillEffectTimer(MEDITATION);
		pc.setCallClanId(0); // �R�[���N��������������Ɉړ�����Ə�������

		/*
		 * subjects �y�b�g�ƃT�����̃e���|�[�g���ʓ��֋����v���C���[�B
		 * �e�y�b�g����UpdateObject���s�������R�[�h��ł̓X�}�[�g�����A
		 * �l�b�g���[�N���ׂ��傫���Ȃ�ׁA��USet�֊i�[���čŌ�ɂ܂Ƃ߂�UpdateObject����B
		 */
		HashSet<L1PcInstance> subjects = new HashSet<L1PcInstance>();
		subjects.add(pc);

		if (!pc.isGhost()) {
			if (pc.getMap().isTakePets()) {
				// �y�b�g�ƃT�������ꏏ�Ɉړ�������B
				for (L1NpcInstance petNpc : pc.getPetList().values()) {
					// �e���|�[�g��̐ݒ�
					L1Location loc = pc.getLocation().randomLocation(3, false);
					int nx = loc.getX();
					int ny = loc.getY();
					if (pc.getMapId() == 5125 || pc.getMapId() == 5131
							|| pc.getMapId() == 5132 || pc.getMapId() == 5133
							|| pc.getMapId() == 5134) { // �y�b�g�}�b�`���
						nx = 32799 + _random.nextInt(5) - 3;
						ny = 32864 + _random.nextInt(5) - 3;
					}
					teleport(petNpc, nx, ny, mapId, head);
					if (petNpc instanceof L1SummonInstance) { // �T���������X�^�[
						L1SummonInstance summon = (L1SummonInstance) petNpc;
						pc.sendPackets(new S_SummonPack(summon, pc));
					} else if (petNpc instanceof L1PetInstance) { // �y�b�g
						L1PetInstance pet = (L1PetInstance) petNpc;
						pc.sendPackets(new S_PetPack(pet, pc));
					}

					for (L1PcInstance visiblePc : L1World.getInstance()
							.getVisiblePlayer(petNpc)) {
						// �e���|�[�g���Ɛ�ɓ���PC�������ꍇ�A�������X�V����Ȃ��ׁA��xremove����B
						visiblePc.removeKnownObject(petNpc);
						subjects.add(visiblePc);
					}
				}

				// �}�W�b�N�h�[�����ꏏ�Ɉړ�������B
				for (L1DollInstance doll : pc.getDollList().values()) {
					// �e���|�[�g��̐ݒ�
					L1Location loc = pc.getLocation().randomLocation(3, false);
					int nx = loc.getX();
					int ny = loc.getY();

					teleport(doll, nx, ny, mapId, head);
					pc.sendPackets(new S_DollPack(doll, pc));

					for (L1PcInstance visiblePc : L1World.getInstance()
							.getVisiblePlayer(doll)) {
						// �e���|�[�g���Ɛ�ɓ���PC�������ꍇ�A�������X�V����Ȃ��ׁA��xremove����B
						visiblePc.removeKnownObject(doll);
						subjects.add(visiblePc);
					}
				}
			} else {
				for (L1DollInstance doll : pc.getDollList().values()) {
					// �e���|�[�g��̐ݒ�
					L1Location loc = pc.getLocation().randomLocation(3, false);
					int nx = loc.getX();
					int ny = loc.getY();

					teleport(doll, nx, ny, mapId, head);
					pc.sendPackets(new S_DollPack(doll, pc));

					for (L1PcInstance visiblePc : L1World.getInstance()
							.getVisiblePlayer(doll)) {
						// �e���|�[�g���Ɛ�ɓ���PC�������ꍇ�A�������X�V����Ȃ��ׁA��xremove����B
						visiblePc.removeKnownObject(doll);
						subjects.add(visiblePc);
					}
				}
			}
		}

		for (L1PcInstance updatePc : subjects) {
			updatePc.updateObject();
		}

		pc.setTeleport(false);

		if (pc.hasSkillEffect(WIND_SHACKLE)) {
			pc.sendPackets(new S_SkillIconWindShackle(pc.getId(),
					pc.getSkillEffectTimeSec(WIND_SHACKLE)));
		}
	}

	private static void teleport(L1NpcInstance npc, int x, int y, short map,
			int head) {
		L1World.getInstance().moveVisibleObject(npc, map);

		L1WorldMap.getInstance().getMap(npc.getMapId()).setPassable(npc.getX(),
				npc.getY(), true);
		npc.setX(x);
		npc.setY(y);
		npc.setMap(map);
		npc.setHeading(head);
		L1WorldMap.getInstance().getMap(npc.getMapId()).setPassable(npc.getX(),
				npc.getY(), false);
	}

}
