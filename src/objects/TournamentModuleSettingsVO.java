package objects;

import java.util.Vector;

public class TournamentModuleSettingsVO {
	public int localSpeed = 0;
	public int frameSkip = 0;
	public int gameFrameLimit = 85714;

	public Vector<TimeoutLimitsVO> timeoutLimits = new Vector<TimeoutLimitsVO>();

	public boolean drawBotNames = true;
	public boolean drawTournamentInfo = true;
	public boolean drawUnitInfo = true;

	public TournamentModuleSettingsVO(TournamentModuleSettingsMessage tmm) {
		this.localSpeed = tmm.LocalSpeed;
		this.frameSkip = tmm.FrameSkip;
		this.gameFrameLimit = tmm.GameFrameLimit;
		for (int i = 0; i < tmm.TimeoutLimits.size(); i++) {
			this.timeoutLimits.add(new TimeoutLimitsVO(tmm.TimeoutLimits.get(i), tmm.TimeoutBounds.get(i)));
		}
		this.drawBotNames = Boolean.valueOf(tmm.DrawBotNames);
		this.drawTournamentInfo = Boolean.valueOf(tmm.DrawTournamentInfo);
		this.drawUnitInfo = Boolean.valueOf(tmm.DrawUnitInfo);
	}
}
