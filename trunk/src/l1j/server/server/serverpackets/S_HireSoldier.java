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
import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_HireSoldier extends ServerBasePacket {

	private static Logger _log = Logger
			.getLogger(S_HireSoldier.class.getName());

	private static final String S_HIRE_SOLDIER = "[S] S_HireSldier";

	private byte[] _byte = null;

	// HTML���J���Ă���Ƃ��ɂ��̃p�P�b�g�𑗂��npcdeloy-j.html���\�������
	// OK�{�^����������C_127�����
	public S_HireSoldier(L1PcInstance pc) {
		writeC(Opcodes.S_OPCODE_HIRESOLDIER);
		writeH(0); // ? �N���C�A���g���Ԃ��p�P�b�g�Ɋ܂܂��
		writeH(0); // ? �N���C�A���g���Ԃ��p�P�b�g�Ɋ܂܂��
		writeH(0); // �ٗp���ꂽ�b���̑���
		writeS(pc.getName());
		writeD(0); // ? �N���C�A���g���Ԃ��p�P�b�g�Ɋ܂܂��
		writeH(0); // �z�u�\�ȗb����
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_HIRE_SOLDIER;
	}
}
