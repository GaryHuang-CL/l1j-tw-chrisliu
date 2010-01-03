package l1j.server.server.model;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import l1j.server.server.datatables.BossSpawnTable;
import l1j.server.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1BossCycle {
	@XmlAttribute(name = "Name")
	private String _name;
	@XmlElement(name = "Base")
	private Base _base;

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Base {
		@XmlAttribute(name = "Date")
		private String _date;
		@XmlAttribute(name = "Time")
		private String _time;

		public String getDate() {
			return _date;
		}

		public void setDate(String date) {
			this._date = date;
		}

		public String getTime() {
			return _time;
		}

		public void setTime(String time) {
			this._time = time;
		}
	}

	@XmlElement(name = "Cycle")
	private Cycle _cycle;

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Cycle {
		@XmlAttribute(name = "Period")
		private String _period;
		@XmlAttribute(name = "Start")
		private String _start;
		@XmlAttribute(name = "End")
		private String _end;

		public String getPeriod() {
			return _period;
		}

		public void setPeriod(String period) {
			this._period = period;
		}

		public String getStart() {
			return _start;
		}

		public void setStart(String start) {
			_start = start;
		}

		public String getEnd() {
			return _end;
		}

		public void setEnd(String end) {
			_end = end;
		}
	}

	private static final Random _rnd = new Random();
	private Calendar _baseDate;
	private int _period; // �����Z
	private int _periodDay;
	private int _periodHour;
	private int _periodMinute;

	private int _startTime; // �����Z
	private int _endTime; // �����Z
	private static SimpleDateFormat _sdfYmd = new SimpleDateFormat("yyyy/MM/dd");
	private static SimpleDateFormat _sdfTime = new SimpleDateFormat("HH:mm");
	private static SimpleDateFormat _sdf = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm");
	private static Date _initDate = new Date();
	private static String _initTime = "0:00";
	private static final Calendar START_UP = Calendar.getInstance();

	public void init() throws Exception {
		// ������̐ݒ�
		Base base = getBase();
		// ����Ȃ���΁A���ݓ��t��0:00�
		if (base == null) {
			setBase(new Base());
			getBase().setDate(_sdfYmd.format(_initDate));
			getBase().setTime(_initTime);
			base = getBase();
		} else {
			try {
				_sdfYmd.parse(base.getDate());
			} catch (Exception e) {
				base.setDate(_sdfYmd.format(_initDate));
			}
			try {
				_sdfTime.parse(base.getTime());
			} catch (Exception e) {
				base.setTime(_initTime);
			}
		}
		// �����������
		Calendar baseCal = Calendar.getInstance();
		baseCal.setTime(_sdf.parse(base.getDate() + " " + base.getTime()));

		// �o�������̏�����,�`�F�b�N
		Cycle spawn = getCycle();
		if (spawn == null || spawn.getPeriod() == null) {
			throw new Exception("Cycle��Period�͕K�{");
		}

		String period = spawn.getPeriod();
		_periodDay = getTimeParse(period, "d");
		_periodHour = getTimeParse(period, "h");
		_periodMinute = getTimeParse(period, "m");

		String start = spawn.getStart();
		int sDay = getTimeParse(start, "d");
		int sHour = getTimeParse(start, "h");
		int sMinute = getTimeParse(start, "m");
		String end = spawn.getEnd();
		int eDay = getTimeParse(end, "d");
		int eHour = getTimeParse(end, "h");
		int eMinute = getTimeParse(end, "m");

		// �����Z
		_period = (_periodDay * 24 * 60) + (_periodHour * 60) + _periodMinute;
		_startTime = (sDay * 24 * 60) + (sHour * 60) + sMinute;
		_endTime = (eDay * 24 * 60) + (eHour * 60) + eMinute;
		if (_period <= 0) {
			throw new Exception("must be Period > 0");
		}
		// start�␳
		if (_startTime < 0 || _period < _startTime) { // �␳
			_startTime = 0;
		}
		// end�␳
		if (_endTime < 0 || _period < _endTime || end == null) { // �␳
			_endTime = _period;
		}
		if (_startTime > _endTime) {
			_startTime = _endTime;
		}
		// start,end�̑��֕␳(�Œ�ł�1���̊Ԃ�������)
		// start==end�Ƃ����w��ł��A�o�����Ԃ����̎����ɔ��Ȃ��悤�ɂ��邽��
		if (_startTime == _endTime) {
			if (_endTime == _period) {
				_startTime--;
			} else {
				_endTime++;
			}
		}

		// �ŋ߂̎����܂ŕ␳(�Čv�Z����Ƃ��Ɍ����ɎZ�o����̂ŁA�����ł͋߂��܂œK���ɕ␳���邾��)
		while (!(baseCal.after(START_UP))) {
			baseCal.add(Calendar.DAY_OF_MONTH, _periodDay);
			baseCal.add(Calendar.HOUR_OF_DAY, _periodHour);
			baseCal.add(Calendar.MINUTE, _periodMinute);
		}
		_baseDate = baseCal;
	}

	/*
	 * �w��������܂ގ���(�̊J�n����)��Ԃ�
	 * ex.������2���Ԃ̏ꍇ
	 *  target base �߂�l
	 *   4:59  7:00 3:00
	 *   5:00  7:00 5:00
	 *   5:01  7:00 5:00
	 *   6:00  7:00 5:00
	 *   6:59  7:00 5:00
	 *   7:00  7:00 7:00
	 *   7:01  7:00 7:00
	 *   9:00  7:00 9:00
	 *   9:01  7:00 9:00
	 */
	private Calendar getBaseCycleOnTarget(Calendar target) {
		// ������擾
		Calendar base = (Calendar) _baseDate.clone();
		if (target.after(base)) {
			// target <= base�ƂȂ�܂ŌJ��Ԃ�
			while (target.after(base)) {
				base.add(Calendar.DAY_OF_MONTH, _periodDay);
				base.add(Calendar.HOUR_OF_DAY, _periodHour);
				base.add(Calendar.MINUTE, _periodMinute);
			}
		}
		if (target.before(base)) {
			while (target.before(base)) {
				base.add(Calendar.DAY_OF_MONTH, -_periodDay);
				base.add(Calendar.HOUR_OF_DAY, -_periodHour);
				base.add(Calendar.MINUTE, -_periodMinute);
			}
		}
		// �I�����Ԃ��Z�o���Ă݂āA�ߋ��̎����Ȃ�{�X���Ԃ��߂��Ă��遨���̎�����Ԃ��B
		Calendar end = (Calendar) base.clone();
		end.add(Calendar.MINUTE, _endTime);
		if (end.before(target)) {
			base.add(Calendar.DAY_OF_MONTH, _periodDay);
			base.add(Calendar.HOUR_OF_DAY, _periodHour);
			base.add(Calendar.MINUTE, _periodMinute);
		}
		return base;
	}

	/**
	 * �w��������܂ގ����ɑ΂��āA�o���^�C�~���O���Z�o����B
	 * @return �o�����鎞��
	 */
	public Calendar calcSpawnTime(Calendar now) {
		// ������擾
		Calendar base = getBaseCycleOnTarget(now);
		// �o�����Ԃ̌v�Z
		base.add(Calendar.MINUTE, _startTime);
		// �o�����Ԃ̌��� start�`end���̊ԂŃ����_���̕b
		int diff = (_endTime - _startTime) * 60;
		int random = diff > 0 ? _rnd.nextInt(diff) : 0;
		base.add(Calendar.SECOND, random);
		return base;
	}

	/**
	 * �w��������܂ގ����ɑ΂��āA�o���J�n���Ԃ��Z�o����B
	 * @return �����̏o���J�n����
	 */
	public Calendar getSpawnStartTime(Calendar now) {
		// ������擾
		Calendar startDate = getBaseCycleOnTarget(now);
		// �o�����Ԃ̌v�Z
		startDate.add(Calendar.MINUTE, _startTime);
		return startDate;
	}

	/**
	 * �w��������܂ގ����ɑ΂��āA�o���I�����Ԃ��Z�o����B
	 * @return �����̏o���I������
	 */
	public Calendar getSpawnEndTime(Calendar now) {
		// ������擾
		Calendar endDate = getBaseCycleOnTarget(now);
		// �o�����Ԃ̌v�Z
		endDate.add(Calendar.MINUTE, _endTime);
		return endDate;
	}

	/**
	 * �w��������܂ގ����ɑ΂��āA���̎����̏o���^�C�~���O���Z�o����B
	 * @return ���̎����̏o�����鎞��
	 */
	public Calendar nextSpawnTime(Calendar now) {
		// ������擾
		Calendar next = (Calendar) now.clone();
		next.add(Calendar.DAY_OF_MONTH, _periodDay);
		next.add(Calendar.HOUR_OF_DAY, _periodHour);
		next.add(Calendar.MINUTE, _periodMinute);
		return calcSpawnTime(next);
	}

	/**
	 * �w������ɑ΂��āA�ŋ߂̏o���J�n���Ԃ�ԋp����B
	 * @return �ŋ߂̏o���J�n����
	 */
	public Calendar getLatestStartTime(Calendar now) {
		// ������擾
		Calendar latestStart = getSpawnStartTime(now);
		if (!now.before(latestStart)) { // now >= latestStart
		} else {
			// now < latestStart�Ȃ�1�O���ŋ߂̎���
			latestStart.add(Calendar.DAY_OF_MONTH, -_periodDay);
			latestStart.add(Calendar.HOUR_OF_DAY, -_periodHour);
			latestStart.add(Calendar.MINUTE, -_periodMinute);
		}

		return latestStart;
	}

	private static int getTimeParse(String target, String search) {
		if (target == null) {
			return 0;
		}
		int n = 0;
		Matcher matcher = Pattern.compile("\\d+" + search).matcher(target);
		if (matcher.find()) {
			String match = matcher.group();
			n = Integer.parseInt(match.replace(search, ""));
		}
		return n;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "BossCycleList")
	static class L1BossCycleList {
		@XmlElement(name = "BossCycle")
		private List<L1BossCycle> bossCycles;

		public List<L1BossCycle> getBossCycles() {
			return bossCycles;
		}

		public void setBossCycles(List<L1BossCycle> bossCycles) {
			this.bossCycles = bossCycles;
		}
	}

	public static void load() {
		PerformanceTimer timer = new PerformanceTimer();
		System.out.print("loading boss cycle...");
		try {
			// BookOrder �N���X���o�C���f�B���O����R���e�L�X�g�𐶐�
			JAXBContext context = JAXBContext
					.newInstance(L1BossCycle.L1BossCycleList.class);

			// XML -> POJO �ϊ����s���A���}�[�V�����𐶐�
			Unmarshaller um = context.createUnmarshaller();

			// XML -> POJO �ϊ�
			File file = new File("./data/xml/Cycle/BossCycle.xml");
			L1BossCycleList bossList = (L1BossCycleList) um.unmarshal(file);

			for (L1BossCycle cycle : bossList.getBossCycles()) {
				cycle.init();
				_cycleMap.put(cycle.getName(), cycle);
			}

			// user�f�[�^������Ώ㏑��
			File userFile = new File("./data/xml/Cycle/users/BossCycle.xml");
			if (userFile.exists()) {
				bossList = (L1BossCycleList) um.unmarshal(userFile);

				for (L1BossCycle cycle : bossList.getBossCycles()) {
					cycle.init();
					_cycleMap.put(cycle.getName(), cycle);
				}
			}
			// spawnlist_boss����ǂݍ���Ŕz�u
			BossSpawnTable.fillSpawnTable();
		} catch (Exception e) {
			_log.log(Level.SEVERE, "BossCycle��ǂݍ��߂܂���ł���", e);
			System.exit(0);
		}
		System.out.println("OK! " + timer.get() + "ms");
	}

	/**
	 * �������Ǝw������ɑ΂���o�����ԁA�o�����Ԃ��R���\�[���o��
	 * @param now �������o�͂������
	 */
	public void showData(Calendar now) {
		System.out.println("[Type]" + getName());
		System.out.print("  [�o������]");
		System.out.print(_sdf.format(getSpawnStartTime(now).getTime()) + " - ");
		System.out.println(_sdf.format(getSpawnEndTime(now).getTime()));
	}

	private static HashMap<String, L1BossCycle> _cycleMap = new HashMap<String, L1BossCycle>();

	public static L1BossCycle getBossCycle(String type) {
		return _cycleMap.get(type);
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

	public Base getBase() {
		return _base;
	}

	public void setBase(Base base) {
		this._base = base;
	}

	public Cycle getCycle() {
		return _cycle;
	}

	public void setCycle(Cycle cycle) {
		this._cycle = cycle;
	}

	private static Logger _log = Logger.getLogger(L1BossCycle.class.getName());
}
