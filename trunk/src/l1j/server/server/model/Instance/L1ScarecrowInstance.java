package l1j.server.server.model.Instance;

import java.util.ArrayList;
import java.util.logging.Logger;

import l1j.server.server.model.L1Attack;
import l1j.server.server.serverpackets.S_ChangeHeading;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.CalcExp;

public class L1ScarecrowInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(L1ScarecrowInstance.class
			.getName());

	public L1ScarecrowInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance player) {
		L1Attack attack = new L1Attack(player, this);
		if (attack.calcHit()) {
			if (player.getLevel() < 5) { // �k�u������������ꍇ�͂�����ύX
				ArrayList<L1PcInstance> targetList = new ArrayList<L1PcInstance>();

				targetList.add(player);
				ArrayList<Integer> hateList = new ArrayList<Integer>();
				hateList.add(1);
				CalcExp.calcExp(player, getId(),
						targetList, hateList, getExp());
			}
			if (getHeading() < 7) { // ���̌������擾
				setHeading(getHeading() + 1); // ���̌�����ݒ�
			} else {
				setHeading(0); // ���̌�����7 �ȏ�ɂȂ�ƍ��̌�����0�ɖ߂�
			}
			broadcastPacket(new S_ChangeHeading(this)); // �����̕ύX
		}
		attack.action();
	}

	@Override
	public void onTalkAction(L1PcInstance l1pcinstance) {

	}

	public void onFinalAction() {

	}

	public void doFinalAction() {}
}
