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
package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

// Referenced classes of package l1j.server.server:
// IdFactory

public class LetterTable {

	private static Logger _log = Logger.getLogger(LetterTable.class.getName());

	private static LetterTable _instance;

	public LetterTable() {
	}

	public static LetterTable getInstance() {
		if (_instance == null) {
			_instance = new LetterTable();
		}
		return _instance;
	}

	// �e���v���[�gID�ꗗ
	// 16:�L�����N�^�[�����݂��Ȃ�
	// 32:�ו�����������
	// 48:���������݂��Ȃ�
	// 64:�����e���\������Ȃ�(����)
	// 80:�����e���\������Ȃ�(����)
	// 96:�����e���\������Ȃ�(����)
	// 112:���߂łƂ��������܂��B%n���Ȃ����Q�����ꂽ�����͍ŏI���i%0�A�f�i�̉��i�ŗ��D����܂����B
	// 128:���Ȃ����񎦂��ꂽ���z���������ƍ������z��񎦂����������ꂽ���߁A�c�O�Ȃ�����D�Ɏ��s���܂����B
	// 144:���Ȃ����Q�����������͐������܂������A���݉Ƃ����L�ł����Ԃɂ���܂���B
	// 160:���Ȃ������L���Ă����Ƃ��ŏI���i%1�A�f�i�ŗ��D����܂����B
	// 176:���Ȃ����\���Ȃ����������́A�������ԓ��ɒ񎦂������z�ȏ�ł̎x������\��������������Ȃ��������߁A���ǎ�������܂����B
	// 192:���Ȃ����\���Ȃ����������́A�������ԓ��ɒ񎦂������z�ȏ�ł̎x������\��������������Ȃ��������߁A���ǎ�������܂����B
	// 208:���Ȃ��̌��������L���Ă���Ƃ́A�{�̎�̗̒n�ɋA�����Ă��邽�߁A���㗘�p�������̂Ȃ瓖���ɐŋ������߂Ȃ���΂Ȃ�܂���B
	// 224:���Ȃ��́A���Ȃ��̉Ƃɉۂ���ꂽ�ŋ�%0�A�f�i���܂��[�߂Ă��܂���B
	// 240:���Ȃ��́A���ǂ��Ȃ��̉Ƃɉۂ��ꂽ�ŋ�%0��[�߂Ȃ������̂ŁA�x���ǂ���ɂ��Ȃ��̉Ƃɑ΂��鏊�L���𔍒D���܂��B

	public void writeLetter(int itemObjectId, int code, String sender,
			String receiver, String date, int templateId, byte[] subject,
			byte[] content) {

		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con
					.prepareStatement("SELECT * FROM letter ORDER BY item_object_id");
			rs = pstm1.executeQuery();
			pstm2 = con
					.prepareStatement("INSERT INTO letter SET item_object_id=?, code=?, sender=?, receiver=?, date=?, template_id=?, subject=?, content=?");
			pstm2.setInt(1, itemObjectId);
			pstm2.setInt(2, code);
			pstm2.setString(3, sender);
			pstm2.setString(4, receiver);
			pstm2.setString(5, date);
			pstm2.setInt(6, templateId);
			pstm2.setBytes(7, subject);
			pstm2.setBytes(8, content);
			pstm2.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
	}

	public void deleteLetter(int itemObjectId) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("DELETE FROM letter WHERE item_object_id=?");
			pstm.setInt(1, itemObjectId);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

}
