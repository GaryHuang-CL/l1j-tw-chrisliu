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

package l1j.server.server.model.map;

import l1j.server.server.types.Point;

/**
 * L1Map �}�b�v����ێ����A����ɑ΂���l�X�ȃC���^�[�t�F�[�X��񋟂���B
 */
public abstract class L1Map {
	private static L1NullMap _nullMap = new L1NullMap();

	protected L1Map() {
	}

	/**
	 * ���̃}�b�v�̃}�b�vID��Ԃ��B
	 * 
	 * @return �}�b�vID
	 */
	public abstract int getId();

	// TODO JavaDoc
	public abstract int getX();

	public abstract int getY();

	public abstract int getWidth();

	public abstract int getHeight();

	/**
	 * �w�肳�ꂽ���W�̒l��Ԃ��B
	 * 
	 * ��������Ă��܂���B���̃��\�b�h�́A�����R�[�h�Ƃ̌݊����ׂ̈ɒ񋟂���Ă��܂��B
	 * L1Map�̗��p�҂͒ʏ�A�}�b�v�ɂǂ̂悤�Ȓl���i�[����Ă��邩��m��K�v�͂���܂���B
	 * �܂��A�i�[����Ă���l�Ɉˑ�����悤�ȃR�[�h�������ׂ��ł͂���܂���B �f�o�b�O���̓���ȏꍇ�Ɍ���A���̃��\�b�h�𗘗p�ł��܂��B
	 * 
	 * @param x
	 *            ���W��X�l
	 * @param y
	 *            ���W��Y�l
	 * @return �w�肳�ꂽ���W�̒l
	 */
	public abstract int getTile(int x, int y);

	/**
	 * �w�肳�ꂽ���W�̒l��Ԃ��B
	 * 
	 * ��������Ă��܂���B���̃��\�b�h�́A�����R�[�h�Ƃ̌݊����ׂ̈ɒ񋟂���Ă��܂��B
	 * L1Map�̗��p�҂͒ʏ�A�}�b�v�ɂǂ̂悤�Ȓl���i�[����Ă��邩��m��K�v�͂���܂���B
	 * �܂��A�i�[����Ă���l�Ɉˑ�����悤�ȃR�[�h�������ׂ��ł͂���܂���B �f�o�b�O���̓���ȏꍇ�Ɍ���A���̃��\�b�h�𗘗p�ł��܂��B
	 * 
	 * @param x
	 *            ���W��X�l
	 * @param y
	 *            ���W��Y�l
	 * @return �w�肳�ꂽ���W�̒l
	 */
	public abstract int getOriginalTile(int x, int y);

	/**
	 * �w�肳�ꂽ���W���}�b�v�͈͓̔��ł��邩��Ԃ��B
	 * 
	 * @param pt
	 *            ���W��ێ�����Point�I�u�W�F�N�g
	 * @return �͈͓��ł����true
	 */
	public abstract boolean isInMap(Point pt);

	/**
	 * �w�肳�ꂽ���W���}�b�v�͈͓̔��ł��邩��Ԃ��B
	 * 
	 * @param x
	 *            ���W��X�l
	 * @param y
	 *            ���W��Y�l
	 * @return �͈͓��ł����true
	 */
	public abstract boolean isInMap(int x, int y);

	/**
	 * �w�肳�ꂽ���W���ʍs�\�ł��邩��Ԃ��B
	 * 
	 * @param pt
	 *            ���W��ێ�����Point�I�u�W�F�N�g
	 * @return �ʍs�\�ł����true
	 */
	public abstract boolean isPassable(Point pt);

	/**
	 * �w�肳�ꂽ���W���ʍs�\�ł��邩��Ԃ��B
	 * 
	 * @param x
	 *            ���W��X�l
	 * @param y
	 *            ���W��Y�l
	 * @return �ʍs�\�ł����true
	 */
	public abstract boolean isPassable(int x, int y);

	/**
	 * �w�肳�ꂽ���W��heading�������ʍs�\�ł��邩��Ԃ��B
	 * 
	 * @param pt
	 *            ���W��ێ�����Point�I�u�W�F�N�g
	 * @return �ʍs�\�ł����true
	 */
	public abstract boolean isPassable(Point pt, int heading);

	/**
	 * �w�肳�ꂽ���W��heading�������ʍs�\�ł��邩��Ԃ��B
	 * 
	 * @param x
	 *            ���W��X�l
	 * @param y
	 *            ���W��Y�l
	 * @return �ʍs�\�ł����true
	 */
	public abstract boolean isPassable(int x, int y, int heading);

	/**
	 * �w�肳�ꂽ���W�̒ʍs�\�A�s�\��ݒ肷��B
	 * 
	 * @param pt
	 *            ���W��ێ�����Point�I�u�W�F�N�g
	 * @param isPassable
	 *            �ʍs�\�ł����true
	 */
	public abstract void setPassable(Point pt, boolean isPassable);

	/**
	 * �w�肳�ꂽ���W�̒ʍs�\�A�s�\��ݒ肷��B
	 * 
	 * @param x
	 *            ���W��X�l
	 * @param y
	 *            ���W��Y�l
	 * @param isPassable
	 *            �ʍs�\�ł����true
	 */
	public abstract void setPassable(int x, int y, boolean isPassable);

	/**
	 * �w�肳�ꂽ���W���Z�[�t�e�B�[�]�[���ł��邩��Ԃ��B
	 * 
	 * @param pt
	 *            ���W��ێ�����Point�I�u�W�F�N�g
	 * @return �Z�[�t�e�B�[�]�[���ł����true
	 */
	public abstract boolean isSafetyZone(Point pt);

	/**
	 * �w�肳�ꂽ���W���Z�[�t�e�B�[�]�[���ł��邩��Ԃ��B
	 * 
	 * @param x
	 *            ���W��X�l
	 * @param y
	 *            ���W��Y�l
	 * @return �Z�[�t�e�B�[�]�[���ł����true
	 */
	public abstract boolean isSafetyZone(int x, int y);

	/**
	 * �w�肳�ꂽ���W���R���o�b�g�]�[���ł��邩��Ԃ��B
	 * 
	 * @param pt
	 *            ���W��ێ�����Point�I�u�W�F�N�g
	 * @return �R���o�b�g�]�[���ł����true
	 */
	public abstract boolean isCombatZone(Point pt);

	/**
	 * �w�肳�ꂽ���W���R���o�b�g�]�[���ł��邩��Ԃ��B
	 * 
	 * @param x
	 *            ���W��X�l
	 * @param y
	 *            ���W��Y�l
	 * @return �R���o�b�g�]�[���ł����true
	 */
	public abstract boolean isCombatZone(int x, int y);

	/**
	 * �w�肳�ꂽ���W���m�[�}���]�[���ł��邩��Ԃ��B
	 * 
	 * @param pt
	 *            ���W��ێ�����Point�I�u�W�F�N�g
	 * @return �m�[�}���]�[���ł����true
	 */
	public abstract boolean isNormalZone(Point pt);

	/**
	 * �w�肳�ꂽ���W���m�[�}���]�[���ł��邩��Ԃ��B
	 * 
	 * @param x
	 *            ���W��X�l
	 * @param y
	 *            ���W��Y�l
	 * @return �m�[�}���]�[���ł����true
	 */
	public abstract boolean isNormalZone(int x, int y);

	/**
	 * �w�肳�ꂽ���W����▂�@��ʂ�����Ԃ��B
	 * 
	 * @param pt
	 *            ���W��ێ�����Point�I�u�W�F�N�g
	 * @return ��▂�@��ʂ��ꍇ�Atrue
	 */
	public abstract boolean isArrowPassable(Point pt);

	/**
	 * �w�肳�ꂽ���W����▂�@��ʂ�����Ԃ��B
	 * 
	 * @param x
	 *            ���W��X�l
	 * @param y
	 *            ���W��Y�l
	 * @return ��▂�@��ʂ��ꍇ�Atrue
	 */
	public abstract boolean isArrowPassable(int x, int y);

	/**
	 * �w�肳�ꂽ���W��heading��������▂�@��ʂ�����Ԃ��B
	 * 
	 * @param pt
	 *            ���W��ێ�����Point�I�u�W�F�N�g
	 * @param heading
	 *            ����
	 * @return ��▂�@��ʂ��ꍇ�Atrue
	 */
	public abstract boolean isArrowPassable(Point pt, int heading);

	/**
	 * �w�肳�ꂽ���W��heading��������▂�@��ʂ�����Ԃ��B
	 * 
	 * @param x
	 *            ���W��X�l
	 * @param y
	 *            ���W��Y�l
	 * @param heading
	 *            ����
	 * @return ��▂�@��ʂ��ꍇ�Atrue
	 */
	public abstract boolean isArrowPassable(int x, int y, int heading);

	/**
	 * ���̃}�b�v���A�����}�b�v�ł��邩��Ԃ��B
	 * 
	 * @return �����ł���΁Atrue
	 */
	public abstract boolean isUnderwater();

	/**
	 * ���̃}�b�v���A�u�b�N�}�[�N�\�ł��邩��Ԃ��B
	 * 
	 * @return �u�b�N�}�[�N�\�ł���΁Atrue
	 */
	public abstract boolean isMarkable();

	/**
	 * ���̃}�b�v���A�����_���e���|�[�g�\�ł��邩��Ԃ��B
	 * 
	 * @return �����_���e���|�[�g�\�ł���΁Atrue
	 */
	public abstract boolean isTeleportable();

	/**
	 * ���̃}�b�v���AMAP�𒴂����e���|�[�g�\�ł��邩��Ԃ��B
	 * 
	 * @return �e���|�[�g�\�ł���΁Atrue
	 */
	public abstract boolean isEscapable();

	/**
	 * ���̃}�b�v���A�����\�ł��邩��Ԃ��B
	 * 
	 * @return �����\�ł���΁Atrue
	 */
	public abstract boolean isUseResurrection();

	/**
	 * ���̃}�b�v���A�p�C�������h�g�p�\�ł��邩��Ԃ��B
	 * 
	 * @return �p�C�������h�g�p�\�ł���΁Atrue
	 */
	public abstract boolean isUsePainwand();

	/**
	 * ���̃}�b�v���A�f�X�y�i���e�B�����邩��Ԃ��B
	 * 
	 * @return �f�X�y�i���e�B������΁Atrue
	 */
	public abstract boolean isEnabledDeathPenalty();

	/**
	 * ���̃}�b�v���A�y�b�g�E�T������A��čs���邩��Ԃ��B
	 * 
	 * @return �y�b�g�E�T������A��čs����Ȃ��true
	 */
	public abstract boolean isTakePets();

	/**
	 * ���̃}�b�v���A�y�b�g�E�T�������Ăяo���邩��Ԃ��B
	 * 
	 * @return �y�b�g�E�T�������Ăяo����Ȃ��true
	 */
	public abstract boolean isRecallPets();

	/**
	 * ���̃}�b�v���A�A�C�e�����g�p�ł��邩��Ԃ��B
	 * 
	 * @return �A�C�e�����g�p�ł���Ȃ��true
	 */
	public abstract boolean isUsableItem();

	/**
	 * ���̃}�b�v���A�X�L�����g�p�ł��邩��Ԃ��B
	 * 
	 * @return �X�L�����g�p�ł���Ȃ��true
	 */
	public abstract boolean isUsableSkill();

	/**
	 * �w�肳�ꂽ���W���ނ�]�[���ł��邩��Ԃ��B
	 * 
	 * @param x
	 *            ���W��X�l
	 * @param y
	 *            ���W��Y�l
	 * @return �ނ�]�[���ł����true
	 */
    public abstract boolean isFishingZone(int x, int y);

	/**
	 * �w�肳�ꂽ���W�Ƀh�A�����݂��邩��Ԃ��B
	 * 
	 * @param x
	 *            ���W��X�l
	 * @param y
	 *            ���W��Y�l
	 * @return �h�A�������true
	 */
    public abstract boolean isExistDoor(int x, int y);

	public static L1Map newNull() {
		return _nullMap;
	}

	/**
	 * �w�肳�ꂽpt�̃^�C���̕�����\����Ԃ��B
	 */
	public abstract String toString(Point pt);

	/**
	 * ���̃}�b�v��null�ł��邩��Ԃ��B
	 * 
	 * @return null�ł���΁Atrue
	 */
	public boolean isNull() {
		return false;
	}
}

/**
 * �������Ȃ�Map�B
 */
class L1NullMap extends L1Map {
	public L1NullMap() {
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public int getX() {
		return 0;
	}

	@Override
	public int getY() {
		return 0;
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getTile(int x, int y) {
		return 0;
	}

	@Override
	public int getOriginalTile(int x, int y) {
		return 0;
	}

	@Override
	public boolean isInMap(int x, int y) {
		return false;
	}

	@Override
	public boolean isInMap(Point pt) {
		return false;
	}

	@Override
	public boolean isPassable(int x, int y) {
		return false;
	}

	@Override
	public boolean isPassable(Point pt) {
		return false;
	}

	@Override
	public boolean isPassable(int x, int y, int heading) {
		return false;
	}

	@Override
	public boolean isPassable(Point pt, int heading) {
		return false;
	}

	@Override
	public void setPassable(int x, int y, boolean isPassable) {
	}

	@Override
	public void setPassable(Point pt, boolean isPassable) {
	}

	@Override
	public boolean isSafetyZone(int x, int y) {
		return false;
	}

	@Override
	public boolean isSafetyZone(Point pt) {
		return false;
	}

	@Override
	public boolean isCombatZone(int x, int y) {
		return false;
	}

	@Override
	public boolean isCombatZone(Point pt) {
		return false;
	}

	@Override
	public boolean isNormalZone(int x, int y) {
		return false;
	}

	@Override
	public boolean isNormalZone(Point pt) {
		return false;
	}

	@Override
	public boolean isArrowPassable(int x, int y) {
		return false;
	}

	@Override
	public boolean isArrowPassable(Point pt) {
		return false;
	}

	@Override
	public boolean isArrowPassable(int x, int y, int heading) {
		return false;
	}

	@Override
	public boolean isArrowPassable(Point pt, int heading) {
		return false;
	}

	@Override
	public boolean isUnderwater() {
		return false;
	}

	@Override
	public boolean isMarkable() {
		return false;
	}

	@Override
	public boolean isTeleportable() {
		return false;
	}

	@Override
	public boolean isEscapable() {
		return false;
	}

	@Override
	public boolean isUseResurrection() {
		return false;
	}

	@Override
	public boolean isUsePainwand() {
		return false;
	}

	@Override
	public boolean isEnabledDeathPenalty() {
		return false;
	}

	@Override
	public boolean isTakePets() {
		return false;
	}

	@Override
	public boolean isRecallPets() {
		return false;
	}

	@Override
	public boolean isUsableItem() {
		return false;
	}

	@Override
	public boolean isUsableSkill() {
		return false;
	}

	@Override
	public boolean isFishingZone(int x, int y) {
		return false;
	}

	@Override
	public boolean isExistDoor(int x, int y) {
		return false;
	}

	@Override
	public String toString(Point pt) {
		return "null";
	}

	@Override
	public boolean isNull() {
		return true;
	}
}
