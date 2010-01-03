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

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_Poison extends ServerBasePacket {

	/**
	 * �L�����N�^�[�̊O����ŏ�Ԃ֕ύX����ۂɑ��M����p�P�b�g���\�z����
	 * 
	 * @param objId
	 *            �O����ς���L�����N�^�[��ID
	 * @param type
	 *            �O���̃^�C�v 0 = �ʏ�F, 1 = �ΐF, 2 = �D�F
	 */
	public S_Poison(int objId, int type) {
		writeC(Opcodes.S_OPCODE_POISON);
		writeD(objId);

		if (type == 0) { // �ʏ�
			writeC(0);
			writeC(0);
		} else if (type == 1) { // �ΐF
			writeC(1);
			writeC(0);
		} else if (type == 2) { // �D�F
			writeC(0);
			writeC(1);
		} else {
			throw new IllegalArgumentException("�s���Ȉ����ł��Btype = " + type);
		}
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_POISON;
	}

	private static final String S_POISON = "[S] S_Poison";
}
