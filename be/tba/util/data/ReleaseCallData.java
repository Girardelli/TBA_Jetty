/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.data;

import java.io.Serializable;

/**
 * Base Data Container for all other Value Objects
 * 
 * @author Yves Willems
 * @version $Revision: 1.0 $
 */
final public class ReleaseCallData implements Serializable
{

    /**
    * 
    */
    private static final long serialVersionUID = 10005L;

    private int mKey;

    private String mFwdNr;

    private long mTimeStamp;

    private boolean mIsReleased;

    private boolean mIsNotLogged;

    public ReleaseCallData(int key, String fwdNr, long timestamp, boolean isReleased, boolean isNotLogged)
    {
        this.mKey = key;
        this.mFwdNr = fwdNr;
        this.mTimeStamp = timestamp;
        this.mIsReleased = isReleased;
        this.mIsNotLogged = isNotLogged;
    }

    public int getKey()
    {
        return this.mKey;
    }

    public void setKey(int key)
    {
        this.mKey = key;
    }

    public String getFwdNr()
    {
        return this.mFwdNr;
    }

    public void setFwdNr(String fwdNr)
    {
        this.mFwdNr = fwdNr;
    }

    public long getTimeStamp()
    {
        return this.mTimeStamp;
    }

    public void setTimeStamp(long timeStamp)
    {
        this.mTimeStamp = timeStamp;
    }

    public boolean getIsReleased()
    {
        return this.mIsReleased;
    }

    public void setIsReleased(boolean isReleased)
    {
        this.mIsReleased = isReleased;
    }

    public boolean getIsNotLogged()
    {
        return this.mIsNotLogged;
    }

    public void setIsNotLogged(boolean isNotLogged)
    {
        this.mIsNotLogged = isNotLogged;
    }
}
