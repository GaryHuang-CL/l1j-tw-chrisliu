package l1j;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ChrisLiu
 * 
 */
public final class Config {

	private Config() {
	}

	/**
	 * 是否開啟偵錯模式
	 */
	public static final boolean DEBUG = true;
	
	// -----------------------------------------------------------------------------
	// 設定檔路徑 /** Configuration files */
	// -----------------------------------------------------------------------------
	/**
	 * 伺服器設定檔
	 */
	public static final String SERVER_CONFIG_FILE = "./config/server.properties";
	/**
	 * 資料庫設定檔
	 */
	public static final String DATABASE_CONFIG_FILE = "./config/database.properties";
	/**
	 * 一般設定檔
	 */
	public static final String GENERAL_SETTINGS_CONFIG_FILE = "./config/general.properties";
	/**
	 * 進階設定檔
	 */
	public static final String ALT_SETTINGS_CONFIG_FILE = "./config/altsetting.properties";
	/**
	 * 角色設定檔
	 */
	public static final String CHAR_SETTINGS_CONFIG_FILE = "./config/charsetting.properties";
	/**
	 * 倍率設定檔
	 */
	public static final String RATE_CONFIG_FILE = "./config/rate.properties";
	/**
	 * 線程設定檔
	 */
	public static final String THREAD_CONFIG_FILE = "./config/thread.properties";
	/**
	 * 版本設定檔
	 */
	public static final String VERSION_CONFIG_FILE = "./config/version.properties";
	/**
	 * log 設定檔的路徑
	 */
	public static final String LOG_CONFIG_FILE = "./config/log.properties";

	// -----------------------------------------------------------------------------
	// 其他路徑 /** Other files */
	// -----------------------------------------------------------------------------
	
	/**
	 * 地圖的資料夾路徑
	 */
	public static final String DIR_MAP = "./map/";
	/**
	 * 公告的的資料夾路徑
	 */
	public static final String DIR_ANNOUNCEMENT = "./data/announcements.txt";
	
	public static void Load() {

		InputStream _is = null;
		Properties _properties = null;
		
		// 讀取遊戲伺服器相關的設定
		_log.info("讀取遊戲伺服器的設定…");
		try {
			_is = new FileInputStream(new File(SERVER_CONFIG_FILE));
			_properties = new Properties();
			_properties.load(_is);
			
			GAME_SERVER_HOST_NAME = _properties.getProperty("GameServerHostname", "*");
			GAME_SERVER_MAX_ONLINE_USER = Integer.parseInt(_properties.getProperty("MaxOnlineUsers", "30"));
		} catch (Exception e) {
		}
		
		// 讀取資料庫伺服器相關的設定
		_log.info("讀取資料庫伺服器的設定…");
		try {
			_is = new FileInputStream(new File(DATABASE_CONFIG_FILE));
			_properties = new Properties();
			_properties.load(_is);

			DB_DRIVER = _properties.getProperty("Driver",
					"com.mysql.jdbc.Driver");
			DB_URL = _properties
					.getProperty("URL",
							"jdbc:mysql://localhost/l1jdb?useUnicode=true&characterEncoding=utf8");
			DB_USER = _properties.getProperty("User", "root");
			DB_PASSWORD = _properties.getProperty("Password", "");
			DB_MAX_CONNECTIONS = Integer.parseInt(_properties.getProperty("MaxConnections", "2"));
			DB_MAX_IDLE_TIME = Integer.parseInt(_properties.getProperty("MaxIdleTime", "2"));
		} catch (Exception e) {
			_log.log(Level.SEVERE, "讀取 " + Config.DATABASE_CONFIG_FILE + " 時發生錯誤", e);
		}
		
		// 讀取遊戲倍率相關的設定
		_log.info("讀取遊戲倍率的設定…");
		try {
			_is = new FileInputStream(new File(RATE_CONFIG_FILE));
			_properties = new Properties();
			_properties.load(_is);
			
			RATE_EXP = Double.parseDouble(_properties.getProperty("RateExp", "1.0"));
			RATE_LAWFUL = Double.parseDouble(_properties.getProperty("RateLawful", "1.0"));
			RATE_KARMA = Double.parseDouble(_properties.getProperty("RateKarma", "1.0"));
			RATE_DROP_ITEM = Double.parseDouble(_properties.getProperty("RateDropAdena", "1.0"));
			RATE_DROP_ADENA = Double.parseDouble(_properties.getProperty("RateDropItem", "1.0"));
		} catch (Exception e) {
		}
		
		// 讀取遊戲進階相關的設定
		_log.info("讀取遊戲進階的設定…");
		try {
		} catch (Exception e) {
		}
	}

	// -----------------------------------------------------------------------------
	// 伺服器設定 /** Server Settings */
	// -----------------------------------------------------------------------------
	/**
	 * 遊戲伺服器相關
	 */
	public static String GAME_SERVER_HOST_NAME;
	/**
	 * 伺服器開啟的 Port
	 */
	public static int GAME_SERVER_PORT;
	public static int GAME_SERVER_MAX_ONLINE_USER;

	/**
	 * 資料庫伺服器相關
	 */
	public static String DB_DRIVER;
	public static String DB_URL;
	public static String DB_USER;
	public static String DB_PASSWORD;
	public static int DB_MAX_CONNECTIONS;
	public static int DB_MAX_IDLE_TIME;
	
	/**
	 * 遊戲倍率相關
	 */
	public static double RATE_EXP;
	public static double RATE_LAWFUL;
	public static double RATE_KARMA;
	public static double RATE_DROP_ITEM;
	public static double RATE_DROP_ADENA;
	/**
	 * 遊戲進階相關
	 */
	public static int ALT_GLOBAL_CHAT_LEVEL;
	
	
	
	private static final Logger _log = Logger.getLogger(Config.class.getName());
}
