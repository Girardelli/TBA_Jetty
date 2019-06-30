package be.tba.util.timer;

import java.util.Date;
import java.util.TimerTask;

public interface TimerTaskIntf 
{
	public Date getStartTime();
	public long getPeriod();
	public TimerTask getTimerTask();
}
