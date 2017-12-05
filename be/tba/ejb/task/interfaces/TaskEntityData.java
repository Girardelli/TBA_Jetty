/*
 * Generated by XDoclet - Do not edit!
 */
package be.tba.ejb.task.interfaces;

/**
 * Data object for TaskEntity.
 *
 * @xdoclet-generated at 1-01-15
 */
public class TaskEntityData extends be.tba.util.data.AbstractData
// implements java.io.Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int id;
    private java.lang.String fwdNr;
    private java.lang.String date;
    private long timeStamp;
    private boolean isFixedPrice;
    private double fixedPrice;
    private int timeSpend;
    private java.lang.String description;
    private boolean isInvoiced;
    private boolean isRecuring;
    private long stopTime;
    private long startTime;
    private java.lang.String doneBy;

    public TaskEntityData()
    {
    }

    public TaskEntityData(int id, java.lang.String fwdNr, java.lang.String date, long timeStamp, boolean isFixedPrice, double fixedPrice, int timeSpend, java.lang.String description, boolean isInvoiced, boolean isRecuring, long stopTime, long startTime, java.lang.String doneBy)
    {
        setId(id);
        setFwdNr(fwdNr);
        setDate(date);
        setTimeStamp(timeStamp);
        setIsFixedPrice(isFixedPrice);
        setFixedPrice(fixedPrice);
        setTimeSpend(timeSpend);
        setDescription(description);
        setIsInvoiced(isInvoiced);
        setIsRecuring(isRecuring);
        setStopTime(stopTime);
        setStartTime(startTime);
        setDoneBy(doneBy);
    }

    public TaskEntityData(TaskEntityData otherData)
    {
        setId(otherData.getId());
        setFwdNr(otherData.getFwdNr());
        setDate(otherData.getDate());
        setTimeStamp(otherData.getTimeStamp());
        setIsFixedPrice(otherData.getIsFixedPrice());
        setFixedPrice(otherData.getFixedPrice());
        setTimeSpend(otherData.getTimeSpend());
        setDescription(otherData.getDescription());
        setIsInvoiced(otherData.getIsInvoiced());
        setIsRecuring(otherData.getIsRecuring());
        setStopTime(otherData.getStopTime());
        setStartTime(otherData.getStartTime());
        setDoneBy(otherData.getDoneBy());

    }

    public be.tba.ejb.task.interfaces.TaskEntityPK getPrimaryKey()
    {
        be.tba.ejb.task.interfaces.TaskEntityPK pk = new be.tba.ejb.task.interfaces.TaskEntityPK(this.getId());
        return pk;
    }

    public int getId()
    {
        return this.id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public java.lang.String getFwdNr()
    {
        return this.fwdNr;
    }

    public void setFwdNr(java.lang.String fwdNr)
    {
        this.fwdNr = fwdNr;
    }

    public java.lang.String getDate()
    {
        return this.date;
    }

    public void setDate(java.lang.String date)
    {
        this.date = date;
    }

    public long getTimeStamp()
    {
        return this.timeStamp;
    }

    public void setTimeStamp(long timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public boolean getIsFixedPrice()
    {
        return this.isFixedPrice;
    }

    public void setIsFixedPrice(boolean isFixedPrice)
    {
        this.isFixedPrice = isFixedPrice;
    }

    public double getFixedPrice()
    {
        return this.fixedPrice;
    }

    public void setFixedPrice(double fixedPrice)
    {
        this.fixedPrice = fixedPrice;
    }

    public int getTimeSpend()
    {
        return this.timeSpend;
    }

    public void setTimeSpend(int timeSpend)
    {
        this.timeSpend = timeSpend;
    }

    public java.lang.String getDescription()
    {
        return this.description;
    }

    public void setDescription(java.lang.String description)
    {
        this.description = description;
    }

    public boolean getIsInvoiced()
    {
        return this.isInvoiced;
    }

    public void setIsInvoiced(boolean isInvoiced)
    {
        this.isInvoiced = isInvoiced;
    }

    public boolean getIsRecuring()
    {
        return this.isRecuring;
    }

    public void setIsRecuring(boolean isRecuring)
    {
        this.isRecuring = isRecuring;
    }

    public long getStopTime()
    {
        return this.stopTime;
    }

    public void setStopTime(long stopTime)
    {
        this.stopTime = stopTime;
    }

    public long getStartTime()
    {
        return this.startTime;
    }

    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }

    public java.lang.String getDoneBy()
    {
        return this.doneBy;
    }

    public void setDoneBy(java.lang.String doneBy)
    {
        this.doneBy = doneBy;
    }

    public String toString()
    {
        StringBuffer str = new StringBuffer("{");

        str.append(toNameValueString().toString());
        str.append('}');

        return (str.toString());
    }

    public String toNameValueString()
    {
        StringBuffer str = new StringBuffer();

        str.append("FwdNr='" + ((this.fwdNr != null) ? this.fwdNr : "") + "',Date='" + ((this.date != null) ? escapeQuotes(this.date) : "") + "',TimeStamp=" + getTimeStamp() + ",IsFixedPrice=" + getIsFixedPrice() + ",FixedPrice=" + getFixedPrice() + ",TimeSpend=" + getTimeSpend() + ",Description='" + ((this.description != null) ? escapeQuotes(this.description) : "") + "',IsInvoiced=" + getIsInvoiced() + ",IsRecuring=" + getIsRecuring() + ",StartTime=" + getStartTime() + ",StopTime=" + getStopTime() + ",DoneBy='" + ((this.doneBy != null) ? this.doneBy : "") + "' ");
        return (str.toString());
    }

    public String toValueString()
    {
        StringBuffer str = new StringBuffer();

        // "(1, '409031', '04/10/05', 1128528272192, 1, 220, 0, 'Nabelactie voor
        // client'. ',0 ,0 ,0 ,0 ,'')
        str.append("'0','" + ((this.fwdNr != null) ? this.fwdNr : "") + "','" + ((this.date != null) ? escapeQuotes(this.date) : "") + "'," + getTimeStamp() + "," + getIsFixedPrice() + "," + getFixedPrice() + "," + getTimeSpend() + ",'" + ((this.description != null) ? escapeQuotes(this.description) : "") + "'," + getIsInvoiced() + "," + getIsRecuring() + "," + getStartTime() + "," + getStopTime() + ",'" + ((this.doneBy != null) ? this.doneBy : "") + "'");
        return (str.toString());
    }

    public boolean equals(Object pOther)
    {
        if (pOther instanceof TaskEntityData)
        {
            TaskEntityData lTest = (TaskEntityData) pOther;
            boolean lEquals = true;

            lEquals = lEquals && this.id == lTest.id;
            if (this.fwdNr == null)
            {
                lEquals = lEquals && (lTest.fwdNr == null);
            }
            else
            {
                lEquals = lEquals && this.fwdNr.equals(lTest.fwdNr);
            }
            if (this.date == null)
            {
                lEquals = lEquals && (lTest.date == null);
            }
            else
            {
                lEquals = lEquals && this.date.equals(lTest.date);
            }
            lEquals = lEquals && this.timeStamp == lTest.timeStamp;
            lEquals = lEquals && this.isFixedPrice == lTest.isFixedPrice;
            lEquals = lEquals && this.fixedPrice == lTest.fixedPrice;
            lEquals = lEquals && this.timeSpend == lTest.timeSpend;
            if (this.description == null)
            {
                lEquals = lEquals && (lTest.description == null);
            }
            else
            {
                lEquals = lEquals && this.description.equals(lTest.description);
            }
            lEquals = lEquals && this.isInvoiced == lTest.isInvoiced;
            lEquals = lEquals && this.isRecuring == lTest.isRecuring;
            lEquals = lEquals && this.stopTime == lTest.stopTime;
            lEquals = lEquals && this.startTime == lTest.startTime;
            if (this.doneBy == null)
            {
                lEquals = lEquals && (lTest.doneBy == null);
            }
            else
            {
                lEquals = lEquals && this.doneBy.equals(lTest.doneBy);
            }

            return lEquals;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        int result = 17;

        result = 37 * result + (int) id;

        result = 37 * result + ((this.fwdNr != null) ? this.fwdNr.hashCode() : 0);

        result = 37 * result + ((this.date != null) ? this.date.hashCode() : 0);

        result = 37 * result + (int) (timeStamp ^ (timeStamp >>> 32));

        result = 37 * result + (isFixedPrice ? 0 : 1);

        {
            long l = Double.doubleToLongBits(fixedPrice);
            result = 37 * result + (int) (l ^ (l >>> 32));
        }

        result = 37 * result + (int) timeSpend;

        result = 37 * result + ((this.description != null) ? this.description.hashCode() : 0);

        result = 37 * result + (isInvoiced ? 0 : 1);

        result = 37 * result + (isRecuring ? 0 : 1);

        result = 37 * result + (int) (stopTime ^ (stopTime >>> 32));

        result = 37 * result + (int) (startTime ^ (startTime >>> 32));

        result = 37 * result + ((this.doneBy != null) ? this.doneBy.hashCode() : 0);

        return result;
    }

}
