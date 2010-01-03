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
package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;

public class S_PetMenuPacket extends ServerBasePacket {

	private byte[] _byte = null;

	public S_PetMenuPacket(L1NpcInstance npc, int exppercet) {
		buildpacket(npc, exppercet);
	}

	private void buildpacket(L1NpcInstance npc, int exppercet) {
		writeC(Opcodes.S_OPCODE_SHOWHTML);

		if (npc instanceof L1PetInstance) { // �y�b�g
			L1PetInstance pet = (L1PetInstance) npc;
			writeD(pet.getId());
			writeS("anicom");
			writeC(0x00);
			writeH(10);
			switch (pet.getCurrentPetStatus()) {
			case 1:
				writeS("$469"); // �U���Ԑ�
				break;
			case 2:
				writeS("$470"); // �h��Ԑ�
				break;
			case 3:
				writeS("$471"); // �x�e
				break;
			case 5:
				writeS("$472"); // �x��
				break;
			default:
				writeS("$471"); // �x�e
				break;
			}
			writeS(Integer.toString(pet.getCurrentHp())); // ���݂̂g�o
			writeS(Integer.toString(pet.getMaxHp())); // �ő�g�o
			writeS(Integer.toString(pet.getCurrentMp())); // ���݂̂l�o
			writeS(Integer.toString(pet.getMaxMp())); // �ő�l�o
			writeS(Integer.toString(pet.getLevel())); // ���x��

			// ���O�̕�������8�𒴂���Ɨ�����
			// �Ȃ���"�Z���g �o�[�i�[�h","�u���C�u ���r�b�g"��OK
			// String pet_name = pet.get_name();
			// if (pet_name.equalsIgnoreCase("�n�C �h�[�x���}��")) {
			// pet_name = "�n�C �h�[�x���}";
			// }
			// else if (pet_name.equalsIgnoreCase("�n�C �Z���g�o�[�i�[�h")) {
			// pet_name = "�n�C �Z���g�o�[";
			// }
			// writeS(pet_name);
			writeS(""); // �y�b�g�̖��O��\��������ƕs����ɂȂ�̂ŁA��\���ɂ���
			writeS("$611"); // ���������ς�
			writeS(Integer.toString(exppercet)); // �o���l
			writeS(Integer.toString(pet.getLawful())); // �A���C�����g
		} else if (npc instanceof L1SummonInstance) { // �T���������X�^�[
			L1SummonInstance summon = (L1SummonInstance) npc;
			writeD(summon.getId());
			writeS("moncom");
			writeC(0x00);
			writeH(6); // �n�����������̐��̖͗l
			switch (summon.get_currentPetStatus()) {
			case 1:
				writeS("$469"); // �U���Ԑ�
				break;
			case 2:
				writeS("$470"); // �h��Ԑ�
				break;
			case 3:
				writeS("$471"); // �x�e
				break;
			case 5:
				writeS("$472"); // �x��
				break;
			default:
				writeS("$471"); // �x�e
				break;
			}
			writeS(Integer.toString(summon.getCurrentHp())); // ���݂̂g�o
			writeS(Integer.toString(summon.getMaxHp())); // �ő�g�o
			writeS(Integer.toString(summon.getCurrentMp())); // ���݂̂l�o
			writeS(Integer.toString(summon.getMaxMp())); // �ő�l�o
			writeS(Integer.toString(summon.getLevel())); // ���x��
			// writeS(summon.getNpcTemplate().get_nameid());
			// writeS(Integer.toString(0));
			// writeS(Integer.toString(790));
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}

		return _byte;
	}
}
