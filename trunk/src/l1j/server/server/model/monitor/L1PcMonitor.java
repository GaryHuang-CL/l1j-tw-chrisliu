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
package l1j.server.server.model.monitor;

import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;

/**
 * L1PcInstance�̒�������A�Ď����������s���ׂ̋��ʓI�ȏ����������������ۃN���X
 * 
 * �e�^�X�N������{@link #run()}�ł͂Ȃ�{@link #execTask(L1PcInstance)}�ɂĎ�������B
 * PC�����O�A�E�g����Ȃǂ��ăT�[�o��ɑ��݂��Ȃ��Ȃ����ꍇ�Arun()���\�b�h�ł͑����Ƀ��^�[������B
 * ���̏ꍇ�A�^�X�N��������s�X�P�W���[�����O����Ă�����A���O�A�E�g�������ŃX�P�W���[�����O���~����K�v������B
 * ��~���Ȃ���΃^�X�N�͎~�܂炸�A�i���ɒ�����s����邱�ƂɂȂ�B
 * ������s�łȂ��P���A�N�V�����̏ꍇ�͂��̂悤�Ȑ���͕s�v�B
 * 
 * L1PcInstance�̎Q�Ƃ𒼐ڎ����Ƃ͖]�܂����Ȃ��B
 * 
 * @author frefre
 *
 */
public abstract class L1PcMonitor implements Runnable {

	/** ���j�^�[�Ώ�L1PcInstance�̃I�u�W�F�N�gID */
	protected int _id;

	/**
	 * �w�肳�ꂽ�p�����[�^��L1PcInstance�ɑ΂��郂�j�^�[���쐬����B
	 * @param oId {@link L1PcInstance#getId()}�Ŏ擾�ł���I�u�W�F�N�gID
	 */
	public L1PcMonitor(int oId) {
		_id = oId;
	}

	@Override
	public final void run() {
		L1PcInstance pc = (L1PcInstance) L1World.getInstance().findObject(_id);
		if (pc == null || pc.getNetConnection() == null) {
			return;
		}
		execTask(pc);
	}

	/**
	 * �^�X�N���s���̏���
	 * @param pc ���j�^�[�Ώۂ�PC
	 */
	public abstract void execTask(L1PcInstance pc);
}
