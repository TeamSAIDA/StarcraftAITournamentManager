package objects;

public class TimeoutLimitsVO {
	public int timeInMS = 0;
	public int frameCount = 0;

	public TimeoutLimitsVO(int timeInMS, int frameCount) {
		this.timeInMS = timeInMS;
		this.frameCount = frameCount;
	}
}
