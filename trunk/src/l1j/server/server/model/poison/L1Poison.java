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
package l1j.server.server.model.poison;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_ServerMessage;

public abstract class L1Poison {
	protected static boolean isValidTarget(L1Character cha) {
		if (cha == null) {
			return false;
		}
		// �ł͏d�����Ȃ�
		if (cha.getPoison() != null) {
			return false;
		}

		if (!(cha instanceof L1PcInstance)) {
			return true;
		}

		L1PcInstance player = (L1PcInstance) cha;
		// �[�j�X �����O�������A�o�t�H ���b�g�A�[�}�[������ �A�x�m�� ���W�X�g��
		if (player.getInventory().checkEquipped(20298)
				|| player.getInventory().checkEquipped(20117)
				|| player.hasSkillEffect(104)) {
			return false;
		}
		return true;
	}

	// �����E�E�E�f����sendPackets��L1Character�ֈ����グ��ׂ���������Ȃ�
	protected static void sendMessageIfPlayer(L1Character cha, int msgId) {
		if (!(cha instanceof L1PcInstance)) {
			return;
		}

		L1PcInstance player = (L1PcInstance) cha;
		player.sendPackets(new S_ServerMessage(msgId));
	}

	/**
	 * ���̓ł̃G�t�F�N�gID��Ԃ��B
	 * 
	 * @see S_Poison#S_Poison(int, int)
	 * 
	 * @return S_Poison�Ŏg�p�����G�t�F�N�gID
	 */
	public abstract int getEffectId();

	/**
	 * ���̓ł̌��ʂ���菜���B<br>
	 * 
	 * @see L1Character#curePoison()
	 */
	public abstract void cure();
}
