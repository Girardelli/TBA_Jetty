/*
 * Generated by XDoclet - Do not edit!
 */
package be.tba.ejb.pbx.interfaces;

import be.tba.util.data.AbstractData;

/**
 * Data object for CallRecordEntity.
 *
 * @xdoclet-generated at 1-01-15
 */
public class CallRecordEntityData extends AbstractData
{
    /**
    *
    */
    private static final long serialVersionUID = 1L;
    private int id;
    private java.lang.String fwdNr;
    private java.lang.String date;
    private java.lang.String time;
    private java.lang.String number;
    private java.lang.String name;
    private java.lang.String cost;
    private long timeStamp;
    private boolean isIncomingCall;
    private boolean isDocumented;
    private boolean isReleased;
    private boolean isNotLogged;
    private boolean isAgendaCall;
    private boolean isSmsCall;
    private boolean isForwardCall;
    private boolean isImportantCall;
    private boolean is3W_call;
    private boolean isMailed;
    private short invoiceLevel;
    private java.lang.String w3_CustomerId;
    private java.lang.String shortDescription;
    private java.lang.String longDescription;
    private boolean isVirgin;
    private boolean isFaxCall;
    private boolean isChanged;
    private java.lang.String doneBy;

    public CallRecordEntityData()
    {
    }

    public CallRecordEntityData(int id, java.lang.String fwdNr, java.lang.String date, java.lang.String time, java.lang.String number, java.lang.String name, java.lang.String cost, long timeStamp, boolean isIncomingCall, boolean isDocumented, boolean isReleased, boolean isNotLogged, boolean isAgendaCall, boolean isSmsCall, boolean isForwardCall, boolean isImportantCall, boolean is3W_call, boolean isMailed, short invoiceLevel, java.lang.String w3_CustomerId, java.lang.String shortDescription, java.lang.String longDescription, boolean isVirgin, boolean isFaxCall, java.lang.String doneBy)
    {
        setId(id);
        setFwdNr(fwdNr);
        setDate(date);
        setTime(time);
        setNumber(number);
        setName(name);
        setCost(cost);
        setTimeStamp(timeStamp);
        setIsIncomingCall(isIncomingCall);
        setIsDocumented(isDocumented);
        setIsReleased(isReleased);
        setIsNotLogged(isNotLogged);
        setIsAgendaCall(isAgendaCall);
        setIsSmsCall(isSmsCall);
        setIsForwardCall(isForwardCall);
        setIsImportantCall(isImportantCall);
        setIs3W_call(is3W_call);
        setIsMailed(isMailed);
        setInvoiceLevel(invoiceLevel);
        setW3_CustomerId(w3_CustomerId);
        setShortDescription(shortDescription);
        setLongDescription(longDescription);
        setIsVirgin(isVirgin);
        setIsFaxCall(isFaxCall);
        setDoneBy(doneBy);
    }

    public CallRecordEntityData(CallRecordEntityData otherData)
    {
        setId(otherData.getId());
        setFwdNr(otherData.getFwdNr());
        setDate(otherData.getDate());
        setTime(otherData.getTime());
        setNumber(otherData.getNumber());
        setName(otherData.getName());
        setCost(otherData.getCost());
        setTimeStamp(otherData.getTimeStamp());
        setIsIncomingCall(otherData.getIsIncomingCall());
        setIsDocumented(otherData.getIsDocumented());
        setIsReleased(otherData.getIsReleased());
        setIsNotLogged(otherData.getIsNotLogged());
        setIsAgendaCall(otherData.getIsAgendaCall());
        setIsSmsCall(otherData.getIsSmsCall());
        setIsForwardCall(otherData.getIsForwardCall());
        setIsImportantCall(otherData.getIsImportantCall());
        setIs3W_call(otherData.getIs3W_call());
        setIsMailed(otherData.getIsMailed());
        setInvoiceLevel(otherData.getInvoiceLevel());
        setW3_CustomerId(otherData.getW3_CustomerId());
        setShortDescription(otherData.getShortDescription());
        setLongDescription(otherData.getLongDescription());
        setIsVirgin(otherData.getIsVirgin());
        setIsFaxCall(otherData.getIsFaxCall());
        setDoneBy(otherData.getDoneBy());

    }

    public be.tba.ejb.pbx.interfaces.CallRecordEntityPK getPrimaryKey()
    {
        be.tba.ejb.pbx.interfaces.CallRecordEntityPK pk = new be.tba.ejb.pbx.interfaces.CallRecordEntityPK(this.getId());
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

    public java.lang.String getTime()
    {
        return this.time;
    }

    public void setTime(java.lang.String time)
    {
        this.time = time;
    }

    public java.lang.String getNumber()
    {
        return this.number;
    }

    public void setNumber(java.lang.String number)
    {
        this.number = number;
    }

    public java.lang.String getName()
    {
        return this.name;
    }

    public void setName(java.lang.String name)
    {
        this.name = name;
    }

    public java.lang.String getCost()
    {
        return this.cost;
    }

    public void setCost(java.lang.String cost)
    {
        this.cost = cost;
    }

    public long getTimeStamp()
    {
        return this.timeStamp;
    }

    public void setTimeStamp(long timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public boolean getIsIncomingCall()
    {
        return this.isIncomingCall;
    }

    public void setIsIncomingCall(boolean isIncomingCall)
    {
        this.isIncomingCall = isIncomingCall;
    }

    public boolean getIsDocumented()
    {
        return this.isDocumented;
    }

    public void setIsDocumented(boolean isDocumented)
    {
        this.isDocumented = isDocumented;
    }

    public boolean getIsReleased()
    {
        return this.isReleased;
    }

    public void setIsReleased(boolean isReleased)
    {
        this.isReleased = isReleased;
    }

    public boolean getIsNotLogged()
    {
        return this.isNotLogged;
    }

    public void setIsNotLogged(boolean isNotLogged)
    {
        this.isNotLogged = isNotLogged;
    }

    public boolean getIsAgendaCall()
    {
        return this.isAgendaCall;
    }

    public void setIsAgendaCall(boolean isAgendaCall)
    {
        this.isAgendaCall = isAgendaCall;
    }

    public boolean getIsSmsCall()
    {
        return this.isSmsCall;
    }

    public void setIsSmsCall(boolean isSmsCall)
    {
        this.isSmsCall = isSmsCall;
    }

    public boolean getIsForwardCall()
    {
        return this.isForwardCall;
    }

    public void setIsForwardCall(boolean isForwardCall)
    {
        this.isForwardCall = isForwardCall;
    }

    public boolean getIsImportantCall()
    {
        return this.isImportantCall;
    }

    public void setIsImportantCall(boolean isImportantCall)
    {
        this.isImportantCall = isImportantCall;
    }

    public boolean getIs3W_call()
    {
        return this.is3W_call;
    }

    public void setIs3W_call(boolean is3W_call)
    {
        this.is3W_call = is3W_call;
    }

    public boolean getIsMailed()
    {
        return this.isMailed;
    }

    public void setIsMailed(boolean isMailed)
    {
        this.isMailed = isMailed;
    }

    public short getInvoiceLevel()
    {
        return this.invoiceLevel;
    }

    public void setInvoiceLevel(short invoiceLevel)
    {
        this.invoiceLevel = invoiceLevel;
    }

    public java.lang.String getW3_CustomerId()
    {
        return this.w3_CustomerId;
    }

    public void setW3_CustomerId(java.lang.String w3_CustomerId)
    {
        this.w3_CustomerId = w3_CustomerId;
    }

    public java.lang.String getShortDescription()
    {
        return this.shortDescription;
    }

    public void setShortDescription(java.lang.String shortDescription)
    {
//        if (!shortDescription.endsWith("\r\n\r\n"))
//        {
//        	this.shortDescription = shortDescription + "\r\n\r\n";
//        }
//        else
//        {
        	this.shortDescription = shortDescription;
//        }
    }

    public java.lang.String getLongDescription()
    {
        return this.longDescription;
    }

    public void setLongDescription(java.lang.String longDescription)
    {
        this.longDescription = longDescription;
    }

    public boolean getIsVirgin()
    {
        return this.isVirgin;
    }

    public void setIsVirgin(boolean isVirgin)
    {
        this.isVirgin = isVirgin;
    }

    public boolean getIsChanged()
    {
        return this.isChanged;
    }

    public void setIsChanged(boolean changed)
    {
        this.isChanged = changed;
    }

    public boolean getIsFaxCall()
    {
        return this.isFaxCall;
    }

    public void setIsFaxCall(boolean isFaxCall)
    {
        this.isFaxCall = isFaxCall;
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
        str.append("FwdNr='" + ((this.getFwdNr() != null) ? this.getFwdNr() : "") + "',Date='" + ((this.getDate() != null) ? this.getDate() : "") + "',Time='" + ((this.getTime() != null) ? this.getTime() : "") + "',Number='" + ((this.getNumber() != null) ? escapeQuotes(this.getNumber()) : "") + "',Name='" + ((this.getName() != null) ? escapeQuotes(this.getName()) : "") + "',Cost='" + ((this.getCost() != null) ? this.getCost() : "") + "',TimeStamp=" + getTimeStamp() + ",IsIncomingCall=" + getIsIncomingCall() + ",IsDocumented=" + getIsDocumented() + ",IsReleased=" + getIsReleased() + ",IsNotLogged=" + getIsNotLogged() + ",IsAgendaCall=" + getIsAgendaCall() + ",IsSmsCall=" + getIsSmsCall() + ",IsForwardCall=" + getIsForwardCall() + ",IsImportantCall=" + getIsImportantCall() + ",Is3W_call=" + getIs3W_call() + ",IsMailed=" + getIsMailed() + ",InvoiceLevel=" + getInvoiceLevel() + ",W3_CustomerId='" + getW3_CustomerId() + "',ShortDescription='" + ((this.getShortDescription() != null) ? escapeQuotes(this.getShortDescription()) : "") + "',LongDescription='" + ((this.getLongDescription() != null) ? escapeQuotes(this.getLongDescription()) : "") + "',IsVirgin=" + getIsVirgin() + ",IsFaxCall=" + getIsFaxCall() + ",IsChanged=" + getIsChanged() + ",DoneBy='" + ((this.getDoneBy() != null) ? this.getDoneBy() : "") + "' ");

        return (str.toString());
    }

    public String toValueString()
    {
        StringBuffer str = new StringBuffer();
        str.append("'0','" + getFwdNr() + "','" + getDate() + "','" + getTime() + "','" + escapeQuotes(getNumber()) + "','" + escapeQuotes(getName()) + "','" + getCost() + "'," + getTimeStamp() + "," + getIsIncomingCall() + "," + getIsDocumented() + "," + getIsReleased() + "," + getIsNotLogged() + "," + getIsAgendaCall() + "," + getIsSmsCall() + "," + getIsForwardCall() + "," + getIsImportantCall() + "," + getIs3W_call() + "," + getIsMailed() + "," + getInvoiceLevel() + ",'" + getW3_CustomerId() + "','" + escapeQuotes(getShortDescription()) + "','" + escapeQuotes(getLongDescription()) + "'," + getIsVirgin() + "," + getIsFaxCall() + "," + getIsChanged() + ",'" + getDoneBy() + "'");
        return (str.toString());
    }

    public boolean equals(Object pOther)
    {
        if (pOther instanceof CallRecordEntityData)
        {
            CallRecordEntityData lTest = (CallRecordEntityData) pOther;
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
            if (this.time == null)
            {
                lEquals = lEquals && (lTest.time == null);
            }
            else
            {
                lEquals = lEquals && this.time.equals(lTest.time);
            }
            if (this.number == null)
            {
                lEquals = lEquals && (lTest.number == null);
            }
            else
            {
                lEquals = lEquals && this.number.equals(lTest.number);
            }
            if (this.name == null)
            {
                lEquals = lEquals && (lTest.name == null);
            }
            else
            {
                lEquals = lEquals && this.name.equals(lTest.name);
            }
            if (this.cost == null)
            {
                lEquals = lEquals && (lTest.cost == null);
            }
            else
            {
                lEquals = lEquals && this.cost.equals(lTest.cost);
            }
            lEquals = lEquals && this.timeStamp == lTest.timeStamp;
            lEquals = lEquals && this.isIncomingCall == lTest.isIncomingCall;
            lEquals = lEquals && this.isDocumented == lTest.isDocumented;
            lEquals = lEquals && this.isReleased == lTest.isReleased;
            lEquals = lEquals && this.isNotLogged == lTest.isNotLogged;
            lEquals = lEquals && this.isAgendaCall == lTest.isAgendaCall;
            lEquals = lEquals && this.isSmsCall == lTest.isSmsCall;
            lEquals = lEquals && this.isForwardCall == lTest.isForwardCall;
            lEquals = lEquals && this.isImportantCall == lTest.isImportantCall;
            lEquals = lEquals && this.is3W_call == lTest.is3W_call;
            lEquals = lEquals && this.isMailed == lTest.isMailed;
            lEquals = lEquals && this.invoiceLevel == lTest.invoiceLevel;
            if (this.w3_CustomerId == null)
            {
                lEquals = lEquals && (lTest.w3_CustomerId == null);
            }
            else
            {
                lEquals = lEquals && this.w3_CustomerId.equals(lTest.w3_CustomerId);
            }
            if (this.shortDescription == null)
            {
                lEquals = lEquals && (lTest.shortDescription == null);
            }
            else
            {
                lEquals = lEquals && this.shortDescription.equals(lTest.shortDescription);
            }
            if (this.longDescription == null)
            {
                lEquals = lEquals && (lTest.longDescription == null);
            }
            else
            {
                lEquals = lEquals && this.longDescription.equals(lTest.longDescription);
            }
            lEquals = lEquals && this.isVirgin == lTest.isVirgin;
            lEquals = lEquals && this.isFaxCall == lTest.isFaxCall;
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

        result = 37 * result + ((this.time != null) ? this.time.hashCode() : 0);

        result = 37 * result + ((this.number != null) ? this.number.hashCode() : 0);

        result = 37 * result + ((this.name != null) ? this.name.hashCode() : 0);

        result = 37 * result + ((this.cost != null) ? this.cost.hashCode() : 0);

        result = 37 * result + (int) (timeStamp ^ (timeStamp >>> 32));

        result = 37 * result + (isIncomingCall ? 0 : 1);

        result = 37 * result + (isDocumented ? 0 : 1);

        result = 37 * result + (isReleased ? 0 : 1);

        result = 37 * result + (isNotLogged ? 0 : 1);

        result = 37 * result + (isAgendaCall ? 0 : 1);

        result = 37 * result + (isSmsCall ? 0 : 1);

        result = 37 * result + (isForwardCall ? 0 : 1);

        result = 37 * result + (isImportantCall ? 0 : 1);

        result = 37 * result + (is3W_call ? 0 : 1);

        result = 37 * result + (isMailed ? 0 : 1);

        result = 37 * result + (int) invoiceLevel;

        result = 37 * result + ((this.w3_CustomerId != null) ? this.w3_CustomerId.hashCode() : 0);

        result = 37 * result + ((this.shortDescription != null) ? this.shortDescription.hashCode() : 0);

        result = 37 * result + ((this.longDescription != null) ? this.longDescription.hashCode() : 0);

        result = 37 * result + (isVirgin ? 0 : 1);

        result = 37 * result + (isFaxCall ? 0 : 1);

        result = 37 * result + ((this.doneBy != null) ? this.doneBy.hashCode() : 0);

        return result;
    }

    static public String toSqlDeleteString(int key)
    {
        return "DELETE FROM `callrecordentity` WHERE Id=" + key;
    }

    static public String toSqlUpdateString(CallRecordEntityData rec)
    {
        String temp = rec.toNameValueString();
        if (temp.indexOf('\'') >= 0)
        {
            temp = temp.replace("'", "''");
        }
        return "UPDATE `callrecordentity` SET " + temp + "\r\n";
    }

    /**
     * Describes the instance and its content for debugging purpose
     *
     * @return Debugging information about the instance and its content
     */
    static public String toSqlInsertString(CallRecordEntityData rec)
    {
        return "INSERT INTO `callrecordentity` VALUES( " + rec.toValueString() + ")";
    }

}
