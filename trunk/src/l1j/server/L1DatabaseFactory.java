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
package l1j.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.utils.LeakCheckedConnection;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * DB�ւ̃A�N�Z�X���邽�߂̊e��C���^�[�t�F�[�X��񋟂���.
 */
public class L1DatabaseFactory {
	/** �C���X�^���X. */
	private static L1DatabaseFactory _instance;

	/** DB�ڑ������܂Ƃ߂����́H. */
	private ComboPooledDataSource _source;

	/** ���b�Z�[�W���O�p. */
	private static Logger _log = Logger.getLogger(L1DatabaseFactory.class
			.getName());

	/* DB�ւ̃A�N�Z�X�ɕK�v�Ȋe��� */
	/** DB�ڑ��h���C�o�[. */
	private static String _driver;

	/** DB�T�[�o��URL. */
	private static String _url;

	/** DB�T�[�o�ɐڑ����郆�[�U��. */
	private static String _user;

	/** DB�T�[�o�ɐڑ�����p�X���[�h. */
	private static String _password;

	/**
	 * DB�ւ̃A�N�Z�X�ɕK�v�Ȋe���̕ۑ�.
	 * 
	 * @param driver
	 *            DB�ڑ��h���C�o�[
	 * @param url
	 *            DB�T�[�o��URL
	 * @param user
	 *            DB�T�[�o�ɐڑ����郆�[�U��
	 * @param password
	 *            DB�T�[�o�ɐڑ�����p�X���[�h
	 */
	public static void setDatabaseSettings(final String driver,
			final String url, final String user, final String password) {
		_driver = driver;
		_url = url;
		_user = user;
		_password = password;
	}

	/**
	 * DB�ڑ��̏��̐ݒ�ƃe�X�g�ڑ�������.
	 * 
	 * @throws SQLException
	 */
	public L1DatabaseFactory() throws SQLException {
		try {
			// DatabaseFactory��L2J����ꕔ�������Ĕq��
			_source = new ComboPooledDataSource();
			_source.setDriverClass(_driver);
			_source.setJdbcUrl(_url);
			_source.setUser(_user);
			_source.setPassword(_password);

			/* Test the connection */
			_source.getConnection().close();
		} catch (SQLException x) {
			_log.fine("Database Connection FAILED");
			// rethrow the exception
			throw x;
		} catch (Exception e) {
			_log.fine("Database Connection FAILED");
			throw new SQLException("could not init DB connection:" + e);
		}
	}

	/**
	 * �T�[�o�V���b�g�_�E������DB�R�l�N�V������ؒf����.
	 */
	public void shutdown() {
		try {
			_source.close();
		} catch (Exception e) {
			_log.log(Level.INFO, "", e);
		}
		try {
			_source = null;
		} catch (Exception e) {
			_log.log(Level.INFO, "", e);
		}
	}

	/**
	 * �C���X�^���X��Ԃ��inull�Ȃ�쐬����).
	 * 
	 * @return L1DatabaseFactory
	 * @throws SQLException
	 */
	public static L1DatabaseFactory getInstance() throws SQLException {
		if (_instance == null) {
			_instance = new L1DatabaseFactory();
		}
		return _instance;
	}

	/**
	 * DB�ڑ������A�R�l�N�V�����I�u�W�F�N�g��Ԃ�.
	 * 
	 * @return Connection �R�l�N�V�����I�u�W�F�N�g
	 * @throws SQLException
	 */
	public Connection getConnection() {
		Connection con = null;

		while (con == null) {
			try {
				con = _source.getConnection();
			} catch (SQLException e) {
				_log
						.warning("L1DatabaseFactory: getConnection() failed, trying again "
								+ e);
			}
		}
		return Config.DETECT_DB_RESOURCE_LEAKS ? LeakCheckedConnection
				.create(con) : con;
	}
}
