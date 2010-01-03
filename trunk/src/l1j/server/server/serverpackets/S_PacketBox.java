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

import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.Account;
import l1j.server.server.Opcodes;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;

/**
 * �X�L���A�C�R����Ւf���X�g�̕\���ȂǕ����̗p�r�Ɏg����p�P�b�g�̃N���X
 */
public class S_PacketBox extends ServerBasePacket {
	private static final String S_PACKETBOX = "[S] S_PacketBox";

	private static Logger _log = Logger.getLogger(S_PacketBox.class.getName());

	private byte[] _byte = null;

	// *** S_107 sub code list ***

	// 1:Kent 2:Orc 3:WW 4:Giran 5:Heine 6:Dwarf 7:Aden 8:Diad 9:�閼9 ...
	/** C(id) H(?): %s�̍U��킪�n�܂�܂����B */
	public static final int MSG_WAR_BEGIN = 0;

	/** C(id) H(?): %s�̍U��킪�I�����܂����B */
	public static final int MSG_WAR_END = 1;

	/** C(id) H(?): %s�̍U��킪�i�s���ł��B */
	public static final int MSG_WAR_GOING = 2;

	/** -: ��̎哱��������܂����B (���y���ς��) */
	public static final int MSG_WAR_INITIATIVE = 3;

	/** -: ���苒���܂����B */
	public static final int MSG_WAR_OCCUPY = 4;

	/** ?: �������I��܂����B (���y���ς��) */
	public static final int MSG_DUEL = 5;

	/** C(count): SMS�̑��M�Ɏ��s���܂����B / �S����%d�����M����܂����B */
	public static final int MSG_SMS_SENT = 6;

	/** -: �j���̒��A2�l�͕v�w�Ƃ��Č��΂�܂����B (���y���ς��) */
	public static final int MSG_MARRIED = 9;

	/** C(weight): �d��(30�i�K) */
	public static final int WEIGHT = 10;

	/** C(food): �����x(30�i�K) */
	public static final int FOOD = 11;

	/** C(0) C(level): ���̃A�C�e����%d���x���ȉ��̂ݎg�p�ł��܂��B (0~49�ȊO�͕\������Ȃ�) */
	public static final int MSG_LEVEL_OVER = 12;

	/** UB���HTML */
	public static final int HTML_UB = 14;

	/**
	 * C(id)<br>
	 * 1:�g�ɍ��߂��Ă�������̗͂���C�̒��ɗn���čs���̂������܂����B<br>
	 * 2:�̂̋��X�ɉ΂̐���͂����݂���ł��܂��B<br>
	 * 3:�̂̋��X�ɐ��̐���͂����݂���ł��܂��B<br>
	 * 4:�̂̋��X�ɕ��̐���͂����݂���ł��܂��B<br>
	 * 5:�̂̋��X�ɒn�̐���͂����݂���ł��܂��B<br>
	 */
	public static final int MSG_ELF = 15;

	/** C(count) S(name)...: �Ւf���X�g�����ǉ� */
	public static final int ADD_EXCLUDE2 = 17;

	/** S(name): �Ւf���X�g�ǉ� */
	public static final int ADD_EXCLUDE = 18;

	/** S(name): �Ւf���� */
	public static final int REM_EXCLUDE = 19;

	/** �X�L���A�C�R�� */
	public static final int ICONS1 = 20;

	/** �X�L���A�C�R�� */
	public static final int ICONS2 = 21;

	/** �I�[���n�̃X�L���A�C�R�� */
	public static final int ICON_AURA = 22;

	/** S(name): �^�E�����[�_�[��%s���I�΂�܂����B */
	public static final int MSG_TOWN_LEADER = 23;

	/**
	 * C(id): ���Ȃ��̃����N��%s�ɕύX����܂����B<br>
	 * id - 1:���K�� 2:��� 3:�K�[�f�B�A��
	 */
	public static final int MSG_RANK_CHANGED = 27;

	/** D(?) S(name) S(clanname): %s������%s�����X�^�o�h�R��ނ��܂����B */
	public static final int MSG_WIN_LASTAVARD = 30;

	/** -: \f1�C�����ǂ��Ȃ�܂����B */
	public static final int MSG_FEEL_GOOD = 31;

	/** �s���BC_30�p�P�b�g����� */
	public static final int SOMETHING1 = 33;

	/** H(time): �u���[�|�[�V�����̃A�C�R�����\�������B */
	public static final int ICON_BLUEPOTION = 34;

	/** H(time): �ϐg�̃A�C�R�����\�������B */
	public static final int ICON_POLYMORPH = 35;

	/** H(time): �`���b�g�֎~�̃A�C�R�����\�������B */
	public static final int ICON_CHATBAN = 36;

	/** �s���BC_7�p�P�b�g����ԁBC_7�̓y�b�g�̃��j���[���J�����Ƃ��ɂ���ԁB */
	public static final int SOMETHING2 = 37;

	/** ��������HTML���\������� */
	public static final int HTML_CLAN1 = 38;

	/** H(time): �C�~���̃A�C�R�����\������� */
	public static final int ICON_I2H = 40;

	/** �L�����N�^�[�̃Q�[���I�v�V�����A�V���[�g�J�b�g���Ȃǂ𑗂� */
	public static final int CHARACTER_CONFIG = 41;

	/** �L�����N�^�[�I����ʂɖ߂� */
	public static final int LOGOUT = 42;

	/** �퓬���ɍĎn�����邱�Ƃ͂ł��܂���B */
	public static final int MSG_CANT_LOGOUT = 43;

	/**
	 * C(count) D(time) S(name) S(info):<br>
	 * [CALL] �{�^���̂����E�B���h�E���\�������B�����BOT�Ȃǂ̕s���҃`�F�b�N��
	 * �g����@�\�炵���B���O���_�u���N���b�N�����C_RequestWho����сA�N���C�A���g��
	 * �t�H���_��bot_list.txt�����������B���O��I������+�L�[�������ƐV�����E�B���h�E���J���B
	 */
	public static final int CALL_SOMETHING = 45;

	/**
	 * C(id): �o�g�� �R���V�A���A�J�I�X��킪�[<br>
	 * id - 1:�J�n���܂� 2:��������܂��� 3:�I�����܂�
	 */
	public static final int MSG_COLOSSEUM = 49;

	/** ��������HTML */
	public static final int HTML_CLAN2 = 51;

	/** �����E�B���h�E���J�� */
	public static final int COOK_WINDOW = 52;

	/** C(type) H(time): �����A�C�R�����\������� */
	public static final int ICON_COOKING = 53;

	/** �������������O���t�B�b�N���\������� */
	public static final int FISHING = 55;

	public S_PacketBox(int subCode) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case MSG_WAR_INITIATIVE:
		case MSG_WAR_OCCUPY:
		case MSG_MARRIED:
		case MSG_FEEL_GOOD:
		case MSG_CANT_LOGOUT:
		case LOGOUT:
		case FISHING:
			break;
		case CALL_SOMETHING:
			callSomething();
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, int value) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case ICON_BLUEPOTION:
		case ICON_CHATBAN:
		case ICON_I2H:
		case ICON_POLYMORPH:
			writeH(value); // time
			break;
		case MSG_WAR_BEGIN:
		case MSG_WAR_END:
		case MSG_WAR_GOING:
			writeC(value); // castle id
			writeH(0); // ?
			break;
		case MSG_SMS_SENT:
		case WEIGHT:
		case FOOD:
			writeC(value);
			break;
		case MSG_ELF:
		case MSG_RANK_CHANGED:
		case MSG_COLOSSEUM:
			writeC(value); // msg id
			break;
		case MSG_LEVEL_OVER:
			writeC(0); // ?
			writeC(value); // 0-49�ȊO�͕\������Ȃ�
			break;
		case COOK_WINDOW:
			writeC(0xdb); // ?
			writeC(0x31);
			writeC(0xdf);
			writeC(0x02);
			writeC(0x01);
			writeC(value); // level
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, int type, int time) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case ICON_COOKING:
			if (type != 7) {
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x12);
				writeC(0x0c);
				writeC(0x09);
				writeC(0x00);
				writeC(0x00);
				writeC(type);
				writeC(0x24);
				writeH(time);
				writeH(0x00);
			} else {
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x12);
				writeC(0x0c);
				writeC(0x09);
				writeC(0xc8);
				writeC(0x00);
				writeC(type);
				writeC(0x26);
				writeH(time);
				writeC(0x3e);
				writeC(0x87);
			}
			break;
		case MSG_DUEL:
			writeD(type); // ����̃I�u�W�F�N�gID
			writeD(time); // �����̃I�u�W�F�N�gID
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, String name) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case ADD_EXCLUDE:
		case REM_EXCLUDE:
		case MSG_TOWN_LEADER:
			writeS(name);
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, int id, String name, String clanName) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case MSG_WIN_LASTAVARD:
			writeD(id); // �N����ID�������H
			writeS(name);
			writeS(clanName);
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, Object[] names) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case ADD_EXCLUDE2:
			writeC(names.length);
			for (Object name : names) {
				writeS(name.toString());
			}
			break;
		default:
			break;
		}
	}

	private void callSomething() {
		Iterator<L1PcInstance> itr = L1World.getInstance().getAllPlayers().iterator();

		writeC(L1World.getInstance().getAllPlayers().size());

		while (itr.hasNext()) {
			L1PcInstance pc = itr.next();
			Account acc = Account.load(pc.getAccountName());

			// ���ԏ�� �Ƃ肠�������O�C�����Ԃ����Ă݂�
			if (acc == null) {
				writeD(0);
			} else {
				Calendar cal = Calendar
						.getInstance(TimeZone.getTimeZone(Config.TIME_ZONE));
				long lastactive = acc.getLastActive().getTime();
				cal.setTimeInMillis(lastactive);
				cal.set(Calendar.YEAR, 1970);
				int time = (int) (cal.getTimeInMillis() / 1000);
				writeD(time); // JST 1970 1/1 09:00 ���
			}

			// �L�������
			writeS(pc.getName()); // ���p12���܂�
			writeS(pc.getClanname()); // []���ɕ\������镶����B���p12���܂�
		}
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
		return S_PACKETBOX;
	}
}
