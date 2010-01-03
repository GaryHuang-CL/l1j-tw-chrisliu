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

import java.util.logging.Logger;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1ItemInstance;

public class S_IdentifyDesc extends ServerBasePacket {

	private static Logger _log = Logger.getLogger(S_IdentifyDesc.class.getName());

	private byte[] _byte = null;

	/**
	 * �m�F�X�N���[���g�p���̃��b�Z�[�W��\������
	 */
	public S_IdentifyDesc(L1ItemInstance item) {
		buildPacket(item);
	}

	private void buildPacket(L1ItemInstance item) {
		writeC(Opcodes.S_OPCODE_IDENTIFYDESC);
		writeH(item.getItem().getItemDescId());

		StringBuilder name = new StringBuilder();

		if (item.getItem().getBless() == 0) {
			name.append("$227 "); // �j�����ꂽ
		} else if (item.getItem().getBless() == 2) {
			name.append("$228 "); // ���ꂽ
		}

		name.append(item.getItem().getIdentifiedNameId());

		if (item.getItem().getType2() == 1) { // weapon
			writeH(134); // \f1%0�F�����ȃ����X�^�[�Ō�%1 �傫�ȃ����X�^�[�Ō�%2
			writeC(3);
			writeS(name.toString());
			writeS(item.getItem().getDmgSmall()
					+ "+" + item.getEnchantLevel());
			writeS(item.getItem().getDmgLarge()
					+ "+" + item.getEnchantLevel());

		} else if (item.getItem().getType2() == 2) { // armor
			if (item.getItem().getItemId() == 20383) { // �R�n�p�w����
				writeH(137); // \f1%0�F�g�p�\��%1�m�d��%2�n
				writeC(3);
				writeS(name.toString());
				writeS(String.valueOf(item.getChargeCount()));
			} else {
				writeH(135); // \f1%0�F�h���%1 �h���
				writeC(2);
				writeS(name.toString());
				writeS(Math.abs(item.getItem().get_ac())
						+ "+" + item.getEnchantLevel());
			}

		} else if (item.getItem().getType2() == 0) { // etcitem
			if (item.getItem().getType() == 1) { // wand
				writeH(137); // \f1%0�F�g�p�\��%1�m�d��%2�n
				writeC(3);
				writeS(name.toString());
				writeS(String.valueOf(item.getChargeCount()));
			} else if (item.getItem().getType() == 2) { // light�n�A�C�e��
				writeH(138);
				writeC(2);
				name.append(": $231 "); // �c��̔R��
				name.append(String.valueOf(item.getRemainingTime()));
				writeS(name.toString());
			} else if (item.getItem().getType() == 7) { // food
				writeH(136); // \f1%0�F�����x%1�m�d��%2�n
				writeC(3);
				writeS(name.toString());
				writeS(String.valueOf(item.getItem().getFoodVolume()));
			} else {
				writeH(138); // \f1%0�F�m�d��%1�n
				writeC(2);
				writeS(name.toString());
			}
			writeS(String.valueOf(item.getWeight()));
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}
}
