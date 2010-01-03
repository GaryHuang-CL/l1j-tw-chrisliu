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
package l1j.server.server.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Calendar;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.templates.L1Npc;

public class L1BossSpawn extends L1Spawn {
	private static Logger _log = Logger.getLogger(L1BossSpawn.class.getName());

	private class SpawnTask implements Runnable {
		private int _spawnNumber;
		private int _objectId;

		private SpawnTask(int spawnNumber, int objectId) {
			_spawnNumber = spawnNumber;
			_objectId = objectId;
		}

		@Override
		public void run() {
			doSpawn(_spawnNumber, _objectId);
		}
	}

	public L1BossSpawn(L1Npc mobTemplate) {
		super(mobTemplate);
	}

	/**
	 * SpawnTask���N������B
	 * 
	 * @param spawnNumber
	 *            L1Spawn�ŊǗ�����Ă���ԍ��B�z�[���|�C���g��������Ή����w�肵�Ă��ǂ��B
	 */
	@Override
	public void executeSpawnTask(int spawnNumber, int objectId) {
		// count���f�N�������g���đS�����񂾂��`�F�b�N
		if (subAndGetCount() != 0) {
			return; // �S������ł��Ȃ�
		}
		// �O��o�����Ԃɑ΂��āA���̏o�����Ԃ��Z�o
		Calendar spawnTime;
		Calendar now = Calendar.getInstance(); // ������
		Calendar latestStart = _cycle.getLatestStartTime(now); // �������ɑ΂���ŋ߂̎����̊J�n����

		Calendar activeStart = _cycle.getSpawnStartTime(_activeSpawnTime); // �A�N�e�B�u�����������̊J�n����
		// �A�N�e�B�u�����������̊J�n���� >= �ŋ߂̎����J�n���Ԃ̏ꍇ�A���̏o��
		if (!activeStart.before(latestStart)) {
			spawnTime = calcNextSpawnTime(activeStart);
		} else {
			// �A�N�e�B�u�����������̊J�n���� < �ŋ߂̎����J�n���Ԃ̏ꍇ�́A�ŋ߂̎����ŏo��
			// �킩��Â炢���m���v�Z����ׂɁA�������calcNextSpawnTime��ʂ��Ă���B
			latestStart.add(Calendar.SECOND, -1);
			spawnTime = calcNextSpawnTime(_cycle
					.getLatestStartTime(latestStart));
		}
		spawnBoss(spawnTime, objectId);
	}

	private int _spawnCount;

	private synchronized int subAndGetCount() {
		return --_spawnCount;
	}

	private String _cycleType;

	public void setCycleType(String type) {
		_cycleType = type;
	}

	private int _percentage;

	public void setPercentage(int percentage) {
		_percentage = percentage;
	}

	private L1BossCycle _cycle;

	private Calendar _activeSpawnTime;

	private static Random _rnd = new Random();

	@Override
	public void init() {
		if (_percentage <= 0) {
			return;
		}
		_cycle = L1BossCycle.getBossCycle(_cycleType);
		if (_cycle == null) {
			throw new RuntimeException(_cycleType + " not found");
		}
		Calendar now = Calendar.getInstance();
		// �o������
		Calendar spawnTime;
		if (Config.INIT_BOSS_SPAWN && _percentage > _rnd.nextInt(100)) {
			spawnTime = _cycle.calcSpawnTime(now);

		} else {
			spawnTime = calcNextSpawnTime(now);
		}
		spawnBoss(spawnTime, 0);
	}

	// �m���v�Z���Ď��̏o�����Ԃ��Z�o
	private Calendar calcNextSpawnTime(Calendar cal) {
		do {
			cal = _cycle.nextSpawnTime(cal);
		} while (!(_percentage > _rnd.nextInt(100)));
		return cal;
	}

	// �w�肳�ꂽ���ԂŃ{�X�o�����X�P�W���[��
	private void spawnBoss(Calendar spawnTime, int objectId) {
		// ����̏o�����Ԃ�ۑ����Ă����B�ďo�����Ɏg�p�B
		_activeSpawnTime = spawnTime;
		long delay = spawnTime.getTimeInMillis() - System.currentTimeMillis();

		int cnt = _spawnCount;
		_spawnCount = getAmount();
		while (cnt < getAmount()) {
			cnt++;
			GeneralThreadPool.getInstance().schedule(
					new SpawnTask(0, objectId), delay);
		}
		_log.log(Level.FINE, toString());
	}

	/**
	 * ���݃A�N�e�B�u�ȃ{�X�ɑ΂�������Əo�����Ԃ�\���B
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[MOB]npcid:" + getNpcId());
		builder.append(" name:" + getName());
		builder.append("[Type]" + _cycle.getName());
		builder.append("[���݂̎���]");
		builder.append(_cycle.getSpawnStartTime(_activeSpawnTime).getTime());
		builder.append(" - ");
		builder.append(_cycle.getSpawnEndTime(_activeSpawnTime).getTime());
		builder.append("[�o������]");
		builder.append(_activeSpawnTime.getTime());
		return builder.toString();
	}
}
