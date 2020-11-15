/*
 * Generated by XDoclet - Do not edit!
 */
package be.tba.sqldata;

import be.tba.sqldata.AbstractData;
import be.tba.util.invoice.InvoiceHelper;

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
   private int accountId;
   // customer number
   private java.lang.String fwdNr;
   private java.lang.String date;
   private java.lang.String time;
   // calling party
   private java.lang.String number;
   // caller name
   private java.lang.String name;
   // duration of call
   private java.lang.String cost;
   private long timeStamp;
   private boolean isIncomingCall;
   private boolean isDocumented;
   private boolean isAgendaCall;
   private boolean isSmsCall;
   private boolean isForwardCall;
   private boolean isImportantCall;
   private boolean isMailed;
   private short invoiceLevel;
   private java.lang.String shortDescription;
   private java.lang.String longDescription;
   private boolean isVirgin;
   private boolean isFaxCall;
   private boolean isChanged;
   private boolean isArchived;
   private boolean isCustAttentionNeeded;

   private java.lang.String doneBy;

   private long tsStart;
   private long tsAnswer;
   private long tsEnd;
   private int monthInt;
   private int dayInt;

   public CallRecordEntityData()
   {
      id = 0;
      accountId = 0;
      fwdNr = "";
      date = "";
      time = "";
      number = "";
      name = "";
      cost = "";
      timeStamp = 0;
      isIncomingCall = false;
      isDocumented = false;
      isAgendaCall = false;
      isSmsCall = false;
      isForwardCall = false;
      isImportantCall = false;
      isMailed = false;
      invoiceLevel = InvoiceHelper.kLevel1;
      shortDescription = "";
      longDescription = "";
      isVirgin = true;
      isFaxCall = false;
      isChanged = false;
      isArchived = false;
      isCustAttentionNeeded = false;
      doneBy = "";
      monthInt = 0;
      dayInt = 0;
   }

   public int getId()
   {
      return this.id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public int getAccountId()
   {
      return this.accountId;
   }

   public void setAccountId(int id)
   {

      this.accountId = id;
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

   public boolean getIsChangedByCust()
   {
      return this.isChanged;
   }

   public void setIsChangedByCust(boolean changed)
   {
      this.isChanged = changed;
   }

   public boolean getIsArchived()
   {
      return this.isArchived;
   }

   public void setIsArchived(boolean archived)
   {
      this.isArchived = archived;
   }

   public boolean getIsFaxCall()
   {
      return this.isFaxCall;
   }

   public void setIsFaxCall(boolean isFaxCall)
   {
      this.isFaxCall = isFaxCall;
   }

   public boolean getIsCustAttentionNeeded()
   {
      return this.isCustAttentionNeeded;
   }

   public void setIsCustAttentionNeeded(boolean isCustAttentionNeeded)
   {
      this.isCustAttentionNeeded = isCustAttentionNeeded;
   }

   public java.lang.String getDoneBy()
   {
      return this.doneBy;
   }

   public void setDoneBy(java.lang.String doneBy)
   {
      this.doneBy = doneBy;
   }

   public long getTsStart()
   {
      return this.tsStart;
   }

   public void setTsStart(long tsStart)
   {
      this.tsStart = tsStart;
   }

   public long getTsAnswer()
   {
      return this.tsAnswer;
   }

   public void setTsAnswer(long tsAnswer)
   {
      this.tsAnswer = tsAnswer;
   }

   public long getTsEnd()
   {
      return this.tsEnd;
   }

   public void setTsEnd(long tsEnd)
   {
      this.tsEnd = tsEnd;
   }

   public int getMonthInt()
   {
      return this.monthInt;
   }

   public void setMonthInt(int monthInt)
   {
      this.monthInt = monthInt;
   }

   public int getDayInt()
   {
      return this.dayInt;
   }

   public void setDayInt(int dayInt)
   {
      this.dayInt = dayInt;
   }

   public String toNameValueString()
   {
      StringBuilder str = new StringBuilder();
      str.append("AccountID=" + getAccountId());
      str.append(",FwdNr='" + ((this.getFwdNr() != null) ? this.getFwdNr() : ""));
      str.append("',Date='" + ((this.getDate() != null) ? this.getDate() : ""));
      str.append("',Time='" + ((this.getTime() != null) ? this.getTime() : ""));
      str.append("',Number='" + ((this.getNumber() != null) ? escapeQuotes(this.getNumber()) : ""));
      str.append("',Name='" + ((this.getName() != null) ? escapeQuotes(this.getName()) : ""));
      str.append("',Cost='" + ((this.getCost() != null) ? this.getCost() : ""));
      str.append("',TimeStamp=" + getTimeStamp());
      str.append(",IsIncomingCall=" + getIsIncomingCall());
      str.append(",IsDocumented=" + getIsDocumented());
      str.append(",IsAgendaCall=" + getIsAgendaCall());
      str.append(",IsSmsCall=" + getIsSmsCall());
      str.append(",IsForwardCall=" + getIsForwardCall());
      str.append(",IsImportantCall=" + getIsImportantCall());
      str.append(",IsMailed=" + getIsMailed());
      str.append(",InvoiceLevel=" + getInvoiceLevel());
      str.append(",ShortDescription='" + ((this.getShortDescription() != null) ? escapeQuotes(this.getShortDescription()) : ""));
      str.append("',LongDescription='" + ((this.getLongDescription() != null) ? escapeQuotes(this.getLongDescription()) : ""));
      str.append("',IsVirgin=" + getIsVirgin());
      str.append(",IsFaxCall=" + getIsFaxCall());
      str.append(",IsChanged=" + getIsChangedByCust());
      str.append(",IsArchived=" + getIsArchived());
      str.append(",IsCustAttentionNeeded=" + getIsCustAttentionNeeded());
      str.append(",DoneBy='" + ((this.getDoneBy() != null) ? this.getDoneBy() : ""));
      str.append("',TsStart=" + getTsStart());
      str.append(",TsAnswer=" + getTsAnswer());
      str.append(",TsEnd=" + getTsEnd());
      str.append(",MonthInt=" + getMonthInt());
      str.append(",DayInt=" + getDayInt());
      return (str.toString());
   }

   public String toValueString()
   {
      StringBuilder str = new StringBuilder();
      str.append("'0'," + getAccountId());
      str.append(",'" + ((this.getFwdNr() != null) ? this.getFwdNr() : ""));
      str.append("','" + ((this.getDate() != null) ? this.getDate() : ""));
      str.append("','" + ((this.getTime() != null) ? this.getTime() : ""));
      str.append("','" + ((this.getNumber() != null) ? escapeQuotes(this.getNumber()) : ""));
      str.append("','" + ((this.getName() != null) ? escapeQuotes(this.getName()) : ""));
      str.append("','" + ((this.getCost() != null) ? this.getCost() : ""));
      str.append("'," + getTimeStamp());
      str.append("," + getIsIncomingCall());
      str.append("," + getIsDocumented());
      str.append("," + getIsAgendaCall());
      str.append("," + getIsSmsCall());
      str.append("," + getIsForwardCall());
      str.append("," + getIsImportantCall());
      str.append("," + getIsMailed());
      str.append("," + getInvoiceLevel());
      str.append(",'" + ((this.getShortDescription() != null) ? escapeQuotes(this.getShortDescription()) : ""));
      str.append("','" + ((this.getLongDescription() != null) ? escapeQuotes(this.getLongDescription()) : ""));
      str.append("'," + getIsVirgin());
      str.append("," + getIsFaxCall());
      str.append("," + getIsChangedByCust());
      str.append("," + getIsArchived());
      str.append("," + getIsCustAttentionNeeded());
      str.append(",'" + ((this.getDoneBy() != null) ? this.getDoneBy() : ""));
      str.append("'," + getTsStart());
      str.append("," + getTsAnswer());
      str.append("," + getTsEnd());
      str.append("," + getMonthInt());
      str.append("," + getDayInt());
      return (str.toString());
   }

   public boolean equals(Object pOther)
   {
      if (pOther instanceof CallRecordEntityData)
      {
         CallRecordEntityData lTest = (CallRecordEntityData) pOther;
         boolean lEquals = true;

         lEquals = lEquals && this.id == lTest.id;
         lEquals = lEquals && this.accountId == lTest.accountId;
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
      result = 37 * result + (int) accountId;
      return result;
   }

   static public String toSqlDeleteString(int key)
   {
      return "DELETE FROM `callrecordentity` WHERE Id=" + key + "\r\n";
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
      return "INSERT INTO `callrecordentity` VALUES( " + rec.toValueString() + ")\r\n";
   }

}
