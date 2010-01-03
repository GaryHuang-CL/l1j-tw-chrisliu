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
package l1j.server.server.model.gametime;

/**
 * <p>
 * �A�f�����Ԃ̕ω����󂯎�邽�߂̃��X�i�[�C���^�[�t�F�[�X�B
 * </p>
 * <p>
 * �A�f�����Ԃ̕ω����Ď����ׂ��N���X�́A���̃C���^�[�t�F�[�X�Ɋ܂܂�Ă��邷�ׂẴ��\�b�h���`���Ă��̃C���^�[�t�F�[�X���������邩�A
 * �֘A���郁�\�b�h�������I�[�o�[���C�h����abstract�N���XL1GameTimeAdapter���g������B
 * </p>
 * <p>
 * ���̂悤�ȃN���X����쐬���ꂽ���X�i�[�I�u�W�F�N�g�́AL1GameTimeClock��addListener���\�b�h���g�p����L1GameTimeClock�ɓo�^�����B
 * �A�f�����ԕω��̒ʒm�́A�������������ꂼ��ς�����Ƃ��ɍs����B
 * </p>
 * <p>
 * �����̃��\�b�h�́AL1GameTimeClock�̃X���b�h��œ��삷��B
 * �����̃��\�b�h�̏����Ɏ��Ԃ����������ꍇ�A���̃��X�i�[�ւ̒ʒm���x���\��������B
 * �����܂łɎ��Ԃ�v���鏈����A�X���b�h���u���b�N���郁�\�b�h�̌Ăяo�����܂܂�鏈�����s���ꍇ�́A�����ŐV���ɃX���b�h���쐬���ď������s���ׂ��ł���B
 * </p>
 * 
 */
public interface L1GameTimeListener {
	/**
	 * �A�f�����ԂŌ����ς�����Ƃ��ɌĂяo�����B
	 * 
	 * @param time
	 *            �ŐV�̃A�f������
	 */
	public void onMonthChanged(L1GameTime time);

	/**
	 * �A�f�����Ԃœ����ς�����Ƃ��ɌĂяo�����B
	 * 
	 * @param time
	 *            �ŐV�̃A�f������
	 */
	public void onDayChanged(L1GameTime time);

	/**
	 * �A�f�����ԂŎ��Ԃ��ς�����Ƃ��ɌĂяo�����B
	 * 
	 * @param time
	 *            �ŐV�̃A�f������
	 */
	public void onHourChanged(L1GameTime time);

	/**
	 * �A�f�����Ԃŕ����ς�����Ƃ��ɌĂяo�����B
	 * 
	 * @param time
	 *            �ŐV�̃A�f������
	 */
	public void onMinuteChanged(L1GameTime time);
}
