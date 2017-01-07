package be.tba.util.invoice;

import java.text.DecimalFormat;
import java.util.Calendar;

import be.tba.util.constants.Constants;

public class InvoiceData
{
   public double InCost = 0.0;
   public double OutCost = 0.0;
   public double SmsCost = 0.0;
   public double FwdCost = 0.0;
   public double FaxCost = 0.0;
   public double CallsCost = 0.0;
   public double AgendaCost = 0.0;
   public double Level1Cost = 0.0;
   public double Level2Cost = 0.0;
   public double Level3Cost = 0.0;
   public double InUnitCost = 0.0;
   public double OutUnitCost = 0.0;
   public double SmsUnitCost = 0.0;
   public double FwdUnitCost = 0.0;
   public double FaxUnitCost = 0.0;
   public double CallsUnitCost = 0.0;
   public double AgendaUnitCost = 0.0;
   public double Level1UnitCost = 0.0;
   public double Level2UnitCost = 0.0;
   public double Level3UnitCost = 0.0;
   public double LongCost = 0.0;
   public double LongFwdCost = 0.0;
   public short Type = 0;
   public String AgendaCostString = "";
   public int NrOfTasks = 0;
   public double TaskCost = 0.0;
   public double TotalCost = 0.0;
   public double Btw = 0.0;
   public DecimalFormat CostFormatter = new DecimalFormat("#0.00");
   public int Month = 1;
   public int Year = 0;
   public String InvoiceNr = "";
   public String Date = "";
   public String TarifGroup = "";
   public String FacLongUnit = "";
   public String FacLongFwdUnit = "";
   public String CustomerRef = "";
   public String PayDate = "";
   public int Id = 0;

   public InvoiceData()
   {
      Calendar vToday = Calendar.getInstance();
      Date = new String(vToday.get(Calendar.DAY_OF_MONTH) + " " + Constants.MONTHS[vToday.get(Calendar.MONTH)] + " " + vToday.get(Calendar.YEAR));
   }

   /*
    * 
    * public String getTarifGroup() { return mTarifGroup; }
    * 
    * public void setTarifGroup(String tarifGroup) { this.mTarifGroup =
    * tarifGroup; }
    * 
    * public InvoiceData() {
    * 
    * }
    * 
    * public double getInCost() { return mInCost; }
    * 
    * public void setInCost(double inCost) { mInCost = inCost; }
    * 
    * public double getOutCost() { return mOutCost; }
    * 
    * public void setOutCost(double outCost) { mOutCost = outCost; }
    * 
    * public double getSmsCost() { return mSmsCost; }
    * 
    * public void setSmsCost(double smsCost) { mSmsCost = smsCost; }
    * 
    * public double getFwdCost() { return mFwdCost; }
    * 
    * public void setFwdCost(double fwdCost) { mFwdCost = fwdCost; }
    * 
    * public double getFaxCost() { return mFaxCost; }
    * 
    * public void setFaxCost(double faxCost) { mFaxCost = faxCost; }
    * 
    * public double getCallsCost() { return mCallsCost; }
    * 
    * public void setCallsCost(double callsCost) { mCallsCost = callsCost; }
    * 
    * public double getAgendaCost() { return mAgendaCost; }
    * 
    * public void setAgendaCost(double agendaCost) { mAgendaCost = agendaCost; }
    * 
    * public double getLevel1Cost() { return mLevel1Cost; }
    * 
    * public void setLevel1Cost(double level1Cost) { mLevel1Cost = level1Cost; }
    * 
    * public double getLevel2Cost() { return mLevel2Cost; }
    * 
    * public void setLevel2Cost(double level2Cost) { mLevel2Cost = level2Cost; }
    * 
    * public double getLevel3Cost() { return mLevel3Cost; }
    * 
    * public void setLevel3Cost(double level3Cost) { mLevel3Cost = level3Cost; }
    * 
    * public double getInUnitCost() { return mInUnitCost; }
    * 
    * public void setInUnitCost(double inUnitCost) { mInUnitCost = inUnitCost; }
    * 
    * public double getOutUnitCost() { return mOutUnitCost; }
    * 
    * public void setOutUnitCost(double outUnitCost) { mOutUnitCost =
    * outUnitCost; }
    * 
    * public double getSmsUnitCost() { return mSmsUnitCost; }
    * 
    * public void setSmsUnitCost(double smsUnitCost) { mSmsUnitCost =
    * smsUnitCost; }
    * 
    * public double getFwdUnitCost() { return mFwdUnitCost; }
    * 
    * public void setFwdUnitCost(double fwdUnitCost) { mFwdUnitCost =
    * fwdUnitCost; }
    * 
    * public double getFaxUnitCost() { return mFaxUnitCost; }
    * 
    * public void setFaxUnitCost(double faxUnitCost) { mFaxUnitCost =
    * faxUnitCost; }
    * 
    * public double getCallsUnitCost() { return mCallsUnitCost; }
    * 
    * public void setCallsUnitCost(double callsUnitCost) { mCallsUnitCost =
    * callsUnitCost; }
    * 
    * public double getAgendaUnitCost() { return mAgendaUnitCost; }
    * 
    * public void setAgendaUnitCost(double agendaUnitCost) { mAgendaUnitCost =
    * agendaUnitCost; }
    * 
    * public double getLevel1UnitCost() { return mLevel1UnitCost; }
    * 
    * public void setLevel1UnitCost(double level1UnitCost) { mLevel1UnitCost =
    * level1UnitCost; }
    * 
    * public double getLevel2UnitCost() { return mLevel2UnitCost; }
    * 
    * public void setLevel2UnitCost(double level2UnitCost) { mLevel2UnitCost =
    * level2UnitCost; }
    * 
    * public double getLevel3UnitCost() { return mLevel3UnitCost; }
    * 
    * public void setLevel3UnitCost(double level3UnitCost) { mLevel3UnitCost =
    * level3UnitCost; }
    * 
    * public short getType() { return mType; }
    * 
    * public void setType(short type) { mType = type; }
    * 
    * public String getAgendaCostString() { return mAgendaCostString; }
    * 
    * public void setAgendaCostString(String agendaCostString) {
    * mAgendaCostString = agendaCostString; }
    * 
    * public int getNrOfTasks() { return mNrOfTasks; }
    * 
    * public void setNrOfTasks(int nrOfTasks) { mNrOfTasks = nrOfTasks; }
    * 
    * public double getTaskCost() { return mTaskCost; }
    * 
    * public void setTaskCost(double taskCost) { mTaskCost = taskCost; }
    * 
    * public double getTotalCost() { return mTotalCost; }
    * 
    * public double getBtw() { return mBtw; }
    * 
    * public void setTotalCost(double totalCost) { mTotalCost = totalCost; }
    * 
    * public DecimalFormat getCostFormatter() { return mCostFormatter; }
    * 
    * public void setCostFormatter(DecimalFormat costFormatter) { mCostFormatter
    * = costFormatter; }
    * 
    * public int getMonth() { return mMonth; }
    * 
    * public void setMonth(int month) { mMonth = month; }
    * 
    * public int getYear() { return mYear; }
    * 
    * public void setYear(int year) { mYear = year; }
    * 
    * public String getInvoiceNr() { return mInvoiceNr; }
    * 
    * public void setInvoiceNr(String invoiceNr) { this.mInvoiceNr = invoiceNr;
    * }
    * 
    * public String getDate() { return mDate; }
    * 
    * public void setDate(String date) { this.mDate = date; }
    * 
    * public String getFacLongUnit() { return mFacLongUnit; }
    * 
    * public void setFacLongUnit(String facLongUnit) { this.mFacLongUnit =
    * facLongUnit; }
    * 
    * public String getFacLongFwdUnit() { return mFacLongFwdUnit; }
    * 
    * public void setFacLongFwdUnit(String facLongFwdUnit) {
    * this.mFacLongFwdUnit = facLongFwdUnit; }
    * 
    * public int getLongSec() { return mLongSec; }
    * 
    * public void setmLongSec(int longSec) { this.mLongSec = longSec; }
    * 
    * public int getLongFwdSec() { return mLongFwdSec; }
    * 
    * public void setLongFwdSec(int longFwdSec) { this.mLongFwdSec = longFwdSec;
    * }
    */

}
