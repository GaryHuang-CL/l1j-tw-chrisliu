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
package l1j.server.server.command.executor;

import l1j.server.server.model.Instance.L1PcInstance;

/**
 * �R�}���h���s�����C���^�[�t�F�[�X
 * 
 * �R�}���h�����N���X�́A���̃C���^�[�t�F�[�X���\�b�h�ȊO��<br>
 * public static L1CommandExecutor getInstance()<br>
 * ���������Ȃ���΂Ȃ�Ȃ��B
 * �ʏ�A���N���X���C���X�^���X�����ĕԂ����A�K�v�ɉ����ăL���b�V�����ꂽ�C���X�^���X��Ԃ�����A���̃N���X���C���X�^���X�����ĕԂ����Ƃ��ł���B
 */
public interface L1CommandExecutor {
	/**
	 * ���̃R�}���h�����s����B
	 * 
	 * @param pc
	 *            ���s��
	 * @param cmdName
	 *            ���s���ꂽ�R�}���h��
	 * @param arg
	 *            ����
	 */
	public void execute(L1PcInstance pc, String cmdName, String arg);
}
