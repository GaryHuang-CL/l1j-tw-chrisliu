/* This program is free software; you can redistribute it and/or modify
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
package l1j.server.server.types;

import java.util.logging.Logger;

public class Point {

	private static Logger _log = Logger.getLogger(Point.class.getName());

	protected int _x = 0;
	protected int _y = 0;

	public Point() {
	}

	public Point(int x, int y) {
		_x = x;
		_y = y;
	}

	public Point(Point pt) {
		_x = pt._x;
		_y = pt._y;
	}

	public int getX() {
		return _x;
	}

	public void setX(int x) {
		_x = x;
	}

	public int getY() {
		return _y;
	}

	public void setY(int y) {
		_y = y;
	}

	public void set(Point pt) {
		_x = pt._x;
		_y = pt._y;
	}

	public void set(int x, int y) {
		_x = x;
		_y = y;
	}

	private static final int HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
	private static final int HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	/**
	 * �w�肳�ꂽ�����ɂ��̍��W���ЂƂi�߂�B
	 * 
	 * @param heading
	 *            ����(0~7)
	 */
	public void forward(int heading) {
		_x += HEADING_TABLE_X[heading];
		_y += HEADING_TABLE_Y[heading];
	}

	/**
	 * �w�肳�ꂽ�����Ƌt�����ɂ��̍��W���ЂƂi�߂�B
	 * 
	 * @param heading
	 *            ����(0~7)
	 */
	public void backward(int heading) {
		_x -= HEADING_TABLE_X[heading];
		_y -= HEADING_TABLE_Y[heading];
	}

	/**
	 * �w�肳�ꂽ���W�ւ̒���������Ԃ��B
	 * 
	 * @param pt
	 *            ���W��ێ�����Point�I�u�W�F�N�g
	 * @return ���W�܂ł̒�������
	 */
	public double getLineDistance(Point pt) {
		long diffX = pt.getX() - this.getX();
		long diffY = pt.getY() - this.getY();
		return Math.sqrt((diffX * diffX) + (diffY * diffY));
	}

	/**
	 * �w�肳�ꂽ���W�܂ł̒����^�C������Ԃ��B
	 * 
	 * @param pt
	 *            ���W��ێ�����Point�I�u�W�F�N�g
	 * @return �w�肳�ꂽ���W�܂ł̒����^�C�����B
	 */
	public int getTileLineDistance(Point pt) {
		return Math.max(Math.abs(pt.getX() - getX()), Math.abs(pt.getY()
				- getY()));
	}

	/**
	 * �w�肳�ꂽ���W�܂ł̃^�C������Ԃ��B
	 * 
	 * @param pt
	 *            ���W��ێ�����Point�I�u�W�F�N�g
	 * @return �w�肳�ꂽ���W�܂ł̃^�C�����B
	 */
	public int getTileDistance(Point pt) {
		return Math.abs(pt.getX() - getX()) + Math.abs(pt.getY() - getY());
	}

	/**
	 * �w�肳�ꂽ���W����ʓ��Ɍ����邩��Ԃ� �v���C���[�̍��W��(0,0)�Ƃ���Ό�����͈͂̍��W��
	 * ����(2,-15)�E��(15,-2)����(-15,2)�E��(-2,15)�ƂȂ�B �`���b�g���ɉB��Č����Ȃ���������ʓ��Ɋ܂܂��B
	 * 
	 * @param pt
	 *            ���W��ێ�����Point�I�u�W�F�N�g
	 * @return �w�肳�ꂽ���W����ʓ��Ɍ�����ꍇ��true�B�����łȂ��ꍇ��false�B
	 */
	public boolean isInScreen(Point pt) {
		int dist = this.getTileDistance(pt);

		if (dist > 17) {
			return false;
		} else if (dist <= 13) {
			return true;
		} else {
			// ���E�̉�ʊO���������O
			// �v���C���[�̍��W��(15, 15)�Ƃ����ꍇ��(0, 0)�ɂ�������W����̋����Ŕ��f
			// Point pointZero = new Point(this.getX() - 15, this.getY() - 15);
			// int dist2 = pointZero.getTileDistance(pt);
			int dist2 = Math.abs(pt.getX() - (this.getX() - 15))
					+ Math.abs(pt.getY() - (this.getY() - 15));
			if (17 <= dist2 && dist2 <= 43) {
				return true;
			}
			return false;
		}
	}

	/**
	 * �w�肳�ꂽ���W�Ɠ������W����Ԃ��B
	 * 
	 * @param pt
	 *            ���W��ێ�����Point�I�u�W�F�N�g
	 * @return �w�肳�ꂽ���W�Ɠ������W���B
	 */
	public boolean isSamePoint(Point pt) {
		return (pt.getX() == getX() && pt.getY() == getY());
	}

	@Override
	public int hashCode() {
		return 7 * getX() + getY();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Point)) {
			return false;
		}
		Point pt = (Point) obj;
		return (this.getX() == pt.getX()) && (this.getY() == pt.getY());
	}

	@Override
	public String toString() {
		return String.format("(%d, %d)", _x, _y);
	}
}
