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

import java.io.IOException;

import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_RetrievePledgeList extends ServerBasePacket {
	public S_RetrievePledgeList(int objid, L1PcInstance pc) {
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan == null) {
			return;
		}

		if (clan.getWarehouseUsingChar() != 0
				&& clan.getWarehouseUsingChar() != pc.getId()) // ���L�����ȊO���N�����q�Ɏg�p��
		{
			pc.sendPackets(new S_ServerMessage(209)); // \f1���̌��������q�ɂ��g�p���ł��B���΂炭�o���Ă��痘�p���Ă��������B
			return;
		}

		if (pc.getInventory().getSize() < 180) {
			int size = clan.getDwarfForClanInventory().getSize();
			if (size > 0) {
				clan.setWarehouseUsingChar(pc.getId()); // �N�����q�ɂ����b�N
				writeC(Opcodes.S_OPCODE_SHOWRETRIEVELIST);
				writeD(objid);
				writeH(size);
				writeC(5); // �����q��
				for (Object itemObject : clan.getDwarfForClanInventory()
						.getItems()) {
					L1ItemInstance item = (L1ItemInstance) itemObject;
					writeD(item.getId());
					writeC(0);
					writeH(item.get_gfxid());
					writeC(item.getBless());
					writeD(item.getCount());
					writeC(item.isIdentified() ? 1 : 0);
					writeS(item.getViewName());
				}
			}
		} else {
			pc.sendPackets(new S_ServerMessage(263)); // \f1��l�̃L�����N�^�[�������ĕ�����A�C�e���͍ő�180�܂łł��B
		}
	}

	@Override
	public byte[] getContent() throws IOException {
		return getBytes();
	}
}
