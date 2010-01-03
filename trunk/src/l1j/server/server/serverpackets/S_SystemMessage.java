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

public class S_SystemMessage extends ServerBasePacket {
	private static final String S_SYSTEM_MESSAGE = "[S] S_SystemMessage";

	private static Logger _log = Logger.getLogger(S_SystemMessage.class
			.getName());

	private byte[] _byte = null;
	private final String _msg;

	/**
	 * �N���C�A���g�Ƀf�[�^�̑��݂��Ȃ��I���W�i���̃��b�Z�[�W��\������B
	 * ���b�Z�[�W��nameid($xxx)���܂܂�Ă���ꍇ�̓I�[�o�[���[�h���ꂽ����������g�p����B
	 * 
	 * @param msg -
	 *            �\�����镶����
	 */
	public S_SystemMessage(String msg) {
		_msg = msg;
		writeC(Opcodes.S_OPCODE_SYSMSG);
		writeC(0x09);
		writeS(msg);
	}

	/**
	 * �N���C�A���g�Ƀf�[�^�̑��݂��Ȃ��I���W�i���̃��b�Z�[�W��\������B
	 * 
	 * @param msg -
	 *            �\�����镶����
	 * @param nameid -
	 *            �������nameid($xxx)���܂܂�Ă���ꍇtrue�ɂ���B
	 */
	public S_SystemMessage(String msg, boolean nameid) {
		_msg = msg;
		writeC(Opcodes.S_OPCODE_NPCSHOUT);
		writeC(2);
		writeD(0);
		writeS(msg);
		// NPC�`���b�g�p�P�b�g�ł����nameid�����߂���邽�߂���𗘗p����
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String toString() {
		return String.format("%s: %s", S_SYSTEM_MESSAGE, _msg);
	}

	@Override
	public String getType() {
		return S_SYSTEM_MESSAGE;
	}
}
