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

import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.ClientThread;
import l1j.server.server.datatables.AuctionBoardTable;
import l1j.server.server.datatables.HouseTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NpcActionTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.model.npc.action.L1NpcAction;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.storage.CharactersItemStorage;
import l1j.server.server.templates.L1AuctionBoard;
import l1j.server.server.templates.L1House;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket, C_Amount

public class C_Amount extends ClientBasePacket {

	private static final Logger _log = Logger.getLogger(C_Amount.class
			.getName());
	private static final String C_AMOUNT = "[C] C_Amount";

	public C_Amount(byte[] decrypt, ClientThread client) throws Exception {
		super(decrypt);
		int objectId = readD();
		int amount = readD();
		int c = readC();
		String s = readS();

		L1PcInstance pc = client.getActiveChar();
		L1NpcInstance npc = (L1NpcInstance) L1World.getInstance().findObject(
				objectId);
		if (npc == null) {
			return;
		}

		String s1 = "";
		String s2 = "";
		try {
			StringTokenizer stringtokenizer = new StringTokenizer(s);
			s1 = stringtokenizer.nextToken();
			s2 = stringtokenizer.nextToken();
		} catch (NoSuchElementException e) {
			s1 = "";
			s2 = "";
		}
		if (s1.equalsIgnoreCase("agapply")) { // �����ɓ��D�����ꍇ
			String pcName = pc.getName();
			AuctionBoardTable boardTable = new AuctionBoardTable();
			for (L1AuctionBoard board : boardTable.getAuctionBoardTableList()) {
				if (pcName.equalsIgnoreCase(board.getBidder())) {
					pc.sendPackets(new S_ServerMessage(523)); // ���łɑ��̉Ƃ̋����ɎQ�����Ă��܂��B
					return;
				}
			}
			int houseId = Integer.valueOf(s2);
			L1AuctionBoard board = boardTable.getAuctionBoardTable(houseId);
			if (board != null) {
				int nowPrice = board.getPrice();
				int nowBidderId = board.getBidderId();
				if (pc.getInventory().consumeItem(L1ItemId.ADENA, amount)) {
					// �����f�����X�V
					board.setPrice(amount);
					board.setBidder(pcName);
					board.setBidderId(pc.getId());
					boardTable.updateAuctionBoard(board);
					if (nowBidderId != 0) {
						// ���D�҂ɃA�f�i��ԋ�
						L1PcInstance bidPc = (L1PcInstance) L1World
								.getInstance().findObject(nowBidderId);
						if (bidPc != null) { // �I�����C����
							bidPc.getInventory().storeItem(L1ItemId.ADENA,
									nowPrice);
							// ���Ȃ����񎦂��ꂽ���z���������ƍ������z��񎦂����������ꂽ���߁A�c�O�Ȃ�����D�Ɏ��s���܂����B%n
							// ���Ȃ��������ɗa����%0�A�f�i�����Ԃ����܂��B%n���肪�Ƃ��������܂����B%n%n
							bidPc.sendPackets(new S_ServerMessage(525, String
									.valueOf(nowPrice)));
						} else { // �I�t���C����
							L1ItemInstance item = ItemTable.getInstance()
									.createItem(L1ItemId.ADENA);
							item.setCount(nowPrice);
							CharactersItemStorage storage = CharactersItemStorage
									.create();
							storage.storeItem(nowBidderId, item);
						}
					}
				} else {
					pc.sendPackets(new S_ServerMessage(189)); // \f1�A�f�i���s�����Ă��܂��B
				}
			}
		} else if (s1.equalsIgnoreCase("agsell")) { // �Ƃ𔄂����ꍇ
			int houseId = Integer.valueOf(s2);
			AuctionBoardTable boardTable = new AuctionBoardTable();
			L1AuctionBoard board = new L1AuctionBoard();
			if (board != null) {
				// �����f���ɐV�K��������
				board.setHouseId(houseId);
				L1House house = HouseTable.getInstance().getHouseTable(houseId);
				board.setHouseName(house.getHouseName());
				board.setHouseArea(house.getHouseArea());
				TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
				Calendar cal = Calendar.getInstance(tz);
				cal.add(Calendar.DATE, 5); // 5����
				cal.set(Calendar.MINUTE, 0); // ���A�b�͐؂�̂�
				cal.set(Calendar.SECOND, 0);
				board.setDeadline(cal);
				board.setPrice(amount);
				board.setLocation(house.getLocation());
				board.setOldOwner(pc.getName());
				board.setOldOwnerId(pc.getId());
				board.setBidder("");
				board.setBidderId(0);
				boardTable.insertAuctionBoard(board);

				house.setOnSale(true); // �������ɐݒ�
				house.setPurchaseBasement(true); // �n���A�W�g���w���ɐݒ�
				HouseTable.getInstance().updateHouse(house); // DB�ɏ�������
			}
		} else {
			L1NpcAction action = NpcActionTable.getInstance().get(s, pc, npc);
			if (action != null) {
				L1NpcHtml result = action.executeWithAmount(s, pc, npc, amount);
				if (result != null) {
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), result));
				}
				return;
			}
		}
	}

	@Override
	public String getType() {
		return C_AMOUNT;
	}
}
