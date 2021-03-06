package be.tba.sqldata;

/**
 * Data object for InvoiceEntity.
 *
 * @xdoclet-generated at 1-01-15
 */
public class InvoiceEntityData extends be.tba.sqldata.AbstractData implements java.io.Serializable, Comparable<InvoiceEntityData>
{
   /**
   *
   */
   private static final long serialVersionUID = 1L;
   private int id;
   private java.lang.String fileName = "";
   private int accountID = 0;
   private java.lang.String accountFwdNr = "";
   private double totalCost = 0.0;
   private int month = 0;
   private int year = 0;
   private int yearSeqNr = 0;
   private java.lang.String invoiceNr = "";
   private boolean frozenFlag = false;
   private boolean isPayed = false;
   private long startTime = 0;
   private long stopTime = 0;
   private java.lang.String customerName = "";
   private boolean isInvoiceMailed = false;
   private java.lang.String invoiceDate = "";
   private java.lang.String customerRef = "";
   private java.lang.String payDate = "";
   // -1 means regular invoice
   // 0 means this is a credit invoice
   // db id means it is a regular invoice with a credit invoice counterpart
   // indicated by this id.
   private int creditId = -1;
   public String fintroId = "";
   public String valutaDate = "";
   public String fromBankNr = "";
   public String paymentDetails = "";
   public String structuredId = ""; // gestructureerde mededeling (Modulo 97 protected)
   public String comment = "";
   public String description = "";

   public InvoiceEntityData()
   {
      creditId = -1;
   }

   public InvoiceEntityData(InvoiceEntityData otherData)
   {
      setId(otherData.getId());
      setFileName(otherData.getFileName());
      setAccountID(otherData.getAccountId());
      setAccountFwdNr(otherData.getAccountFwdNr());
      setTotalCost(otherData.getTotalCost());
      setMonth(otherData.getMonth());
      setYear(otherData.getYear());
      setYearSeqNr(otherData.getYearSeqNr());
      setInvoiceNr(otherData.getInvoiceNr());
      setFrozenFlag(otherData.getFrozenFlag());
      setIsPayed(otherData.getIsPayed());
      setStartTime(otherData.getStartTime());
      setStopTime(otherData.getStopTime());
      setCustomerName(otherData.getCustomerName());
      setIsInvoiceMailed(otherData.getIsInvoiceMailed());
      setInvoiceDate(otherData.getInvoiceDate());
      setCustomerRef(otherData.getCustomerRef());
      setPayDate(otherData.getPayDate());
      setCreditId(otherData.getCreditId());
      setFintroId(otherData.getFintroId());
      setValutaDate(otherData.getValutaDate());
      setFromBankNr(otherData.getFromBankNr());
      setPaymentDetails(otherData.getPaymentDetails());
      setStructuredId(otherData.getStructuredId());
      setComment(otherData.getComment());
      setDescription(otherData.getDescription());
   }

   public int getId()
   {
      return this.id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public java.lang.String getFileName()
   {
      return this.fileName;
   }

   public void setFileName(java.lang.String fileName)
   {
      this.fileName = fileName;
   }

   public int getAccountId()
   {
      return this.accountID;
   }

   public void setAccountID(int accountID)
   {
      this.accountID = accountID;
   }

   public java.lang.String getAccountFwdNr()
   {
      return this.accountFwdNr;
   }

   public void setAccountFwdNr(java.lang.String accountFwdNr)
   {
      this.accountFwdNr = accountFwdNr;
   }

   public double getTotalCost()
   {
      return this.totalCost;
   }

   public void setTotalCost(double totalCost)
   {
      this.totalCost = totalCost;
   }

   public int getMonth()
   {
      return this.month;
   }

   public void setMonth(int month)
   {
      this.month = month;
   }

   public int getYear()
   {
      return this.year;
   }

   public void setYear(int year)
   {
      this.year = year;
   }

   public int getYearSeqNr()
   {
      return this.yearSeqNr;
   }

   public void setYearSeqNr(int yearSeqNr)
   {
      this.yearSeqNr = yearSeqNr;
   }

   public java.lang.String getInvoiceNr()
   {
      return this.invoiceNr;
   }

   public void setInvoiceNr(java.lang.String invoiceNr)
   {
      this.invoiceNr = invoiceNr;
   }

   public boolean getFrozenFlag()
   {
      return this.frozenFlag;
   }

   public void setFrozenFlag(boolean frozenFlag)
   {
      this.frozenFlag = frozenFlag;
   }

   public boolean getIsPayed()
   {
      return this.isPayed;
   }

   public void setIsPayed(boolean isPayed)
   {
      this.isPayed = isPayed;
   }

   public long getStartTime()
   {
      return this.startTime;
   }

   public void setStartTime(long startTime)
   {
      this.startTime = startTime;
   }

   public long getStopTime()
   {
      return this.stopTime;
   }

   public void setStopTime(long stopTime)
   {
      this.stopTime = stopTime;
   }

   public java.lang.String getCustomerName()
   {
      return this.customerName;
   }

   public void setCustomerName(java.lang.String customerName)
   {
      this.customerName = customerName;
   }

   public boolean getIsInvoiceMailed()
   {
      return this.isInvoiceMailed;
   }

   public void setIsInvoiceMailed(boolean isInvoiceMailed)
   {
      this.isInvoiceMailed = isInvoiceMailed;
   }

   public java.lang.String getInvoiceDate()
   {
      return this.invoiceDate;
   }

   public void setInvoiceDate(java.lang.String invoiceDate)
   {
      this.invoiceDate = invoiceDate;
   }

   public java.lang.String getCustomerRef()
   {
      return this.customerRef;
   }

   public void setCustomerRef(java.lang.String customerRef)
   {
      this.customerRef = customerRef;
   }

   public java.lang.String getPayDate()
   {
      return this.payDate;
   }

   public void setPayDate(java.lang.String payDate)
   {
      this.payDate = payDate;
   }

   public int getCreditId()
   {
      return this.creditId;
   }

   public void setCreditId(int creditId)
   {
      this.creditId = creditId;
   }

   public String getFintroId()
   {
      return this.fintroId;
   }

   public void setFintroId(String fintroId)
   {
      this.fintroId = fintroId;
   }

   public String getValutaDate()
   {
      return this.valutaDate;
   }

   public void setValutaDate(String valutaDate)
   {
      this.valutaDate = valutaDate;
   }

   public String getFromBankNr()
   {
      return this.fromBankNr;
   }

   public void setFromBankNr(String fromBankNr)
   {
      this.fromBankNr = fromBankNr;
   }

   public String getPaymentDetails()
   {
      return this.paymentDetails;
   }

   public void setPaymentDetails(String paymentDetails)
   {
      this.paymentDetails = paymentDetails;
   }

   public String getStructuredId()
   {
      return this.structuredId;
   }

   public void setStructuredId(String structuredId)
   {
      this.structuredId = structuredId;
   }

   public String getComment()
   {
      return this.comment;
   }

   public void setComment(String comment)
   {
      this.comment = comment;
   }

   public String getDescription()
   {
      return this.description;
   }

   public void setDescription(String description)
   {
      this.description = description;
   }

   public boolean equals(Object pOther)
   {
      if (pOther instanceof InvoiceEntityData)
      {
         InvoiceEntityData lTest = (InvoiceEntityData) pOther;
         boolean lEquals = true;

         lEquals = lEquals && this.id == lTest.id;
         if (this.accountFwdNr == null)
         {
            lEquals = lEquals && (lTest.accountFwdNr == null);
         }
         else
         {
            lEquals = lEquals && this.accountFwdNr.equals(lTest.accountFwdNr);
         }
         lEquals = lEquals && this.totalCost == lTest.totalCost;
         lEquals = lEquals && this.month == lTest.month;
         lEquals = lEquals && this.year == lTest.year;
         lEquals = lEquals && this.yearSeqNr == lTest.yearSeqNr;
         if (this.invoiceNr == null)
         {
            lEquals = lEquals && (lTest.invoiceNr == null);
         }
         else
         {
            lEquals = lEquals && this.invoiceNr.equals(lTest.invoiceNr);
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

      result = 37 * result + ((this.fileName != null) ? this.fileName.hashCode() : 0);

      result = 37 * result + ((this.accountFwdNr != null) ? this.accountFwdNr.hashCode() : 0);

      {
         long l = Double.doubleToLongBits(totalCost);
         result = 37 * result + (int) (l ^ (l >>> 32));
      }

      result = 37 * result + (int) month;

      result = 37 * result + (int) year;

      result = 37 * result + (int) yearSeqNr;
      return result;
   }

   public String toNameValueString()
   {
      StringBuilder str = new StringBuilder();
      str.append("FileName='");
      str.append((this.fileName != null) ? this.fileName : "");
      str.append("',AccountID=");
      str.append(getAccountId());
      str.append(",AccountFwdNr='");
      str.append((this.accountFwdNr != null) ? this.accountFwdNr : "");
      str.append("',TotalCost=");
      str.append(getTotalCost());
      str.append(",Month=");
      str.append(getMonth());
      str.append(",Year=");
      str.append(getYear());
      str.append(",YearSeqNr=");
      str.append(getYearSeqNr());
      str.append(",InvoiceNr='");
      str.append((this.invoiceNr != null) ? this.invoiceNr : "");
      str.append("',FrozenFlag=");
      str.append(getFrozenFlag());
      str.append(",IsPayed=");
      str.append(getIsPayed());
      str.append(",StartTime=");
      str.append(getStartTime());
      str.append(",StopTime=");
      str.append(getStopTime());
      str.append(",CustomerName='");
      str.append((this.customerName != null) ? this.customerName : "");
      str.append("',IsInvoiceMailed=");
      str.append(getIsInvoiceMailed());
      str.append(",InvoiceDate='");
      str.append((this.invoiceDate != null) ? this.invoiceDate : "");
      str.append("',CustomerRef='");
      str.append((this.customerRef != null) ? this.customerRef : "");
      str.append("',PayDate='");
      str.append((this.payDate != null) ? this.payDate : "");
      str.append("',CreditId=");
      str.append(getCreditId());
      str.append(",FintroId='");
      str.append((this.fintroId != null) ? this.fintroId : "");
      str.append("',ValutaDate='");
      str.append((this.valutaDate != null) ? this.valutaDate : "");
      str.append("',FromBankNr='");
      str.append((this.fromBankNr != null) ? this.fromBankNr : "");
      str.append("',PaymentDetails='");
      str.append((this.paymentDetails != null) ? this.paymentDetails : "");
      str.append("',StructuredId='");
      str.append((this.structuredId != null) ? this.structuredId : "");
      str.append("',Comment='");
      str.append((this.comment != null) ? this.comment : "");
      str.append("',Description='");
      str.append((this.description != null) ? this.description : "");
      str.append("'");
      return (str.toString());
   }

   public String toValueString()
   {
      StringBuilder str = new StringBuilder();

      // "(1, '409031', '04/10/05', 1128528272192, 1, 220, 0, 'Nabelactie voor
      // client'. ',0 ,0 ,0 ,0 ,'')
      str.append("'0','");
      str.append((this.fileName != null) ? this.fileName : "");
      str.append("',");
      str.append(getAccountId());
      str.append(",'");
      str.append((this.accountFwdNr != null) ? this.accountFwdNr : "");
      str.append("',");
      str.append(getTotalCost());
      str.append(",");
      str.append(getMonth());
      str.append(",");
      str.append(getYear());
      str.append(",");
      str.append(getYearSeqNr());
      str.append(",'");
      str.append((this.invoiceNr != null) ? this.invoiceNr : "");
      str.append("',");
      str.append(getFrozenFlag());
      str.append(",");
      str.append(getIsPayed());
      str.append(",");
      str.append(getStartTime());
      str.append(",");
      str.append(getStopTime());
      str.append(",'");
      str.append((this.customerName != null) ? this.customerName : "");
      str.append("',");
      str.append(getIsInvoiceMailed());
      str.append(",'");
      str.append((this.invoiceDate != null) ? this.invoiceDate : "");
      str.append("','");
      str.append((this.customerRef != null) ? this.customerRef : "");
      str.append("','");
      str.append((this.payDate != null) ? this.payDate : "");
      str.append("',");
      str.append(getCreditId());
      str.append(",'");
      str.append((this.fintroId != null) ? this.fintroId : "");
      str.append("','");
      str.append((this.valutaDate != null) ? this.valutaDate : "");
      str.append("','");
      str.append((this.fromBankNr != null) ? this.fromBankNr : "");
      str.append("','");
      str.append((this.paymentDetails != null) ? this.paymentDetails : "");
      str.append("','");
      str.append((this.structuredId != null) ? this.structuredId : "");
      str.append("','");
      str.append((this.comment != null) ? this.comment : "");
      str.append("','");
      str.append((this.description != null) ? this.description : "");
      str.append("'");
      return (str.toString());
   }

   @Override
   public int compareTo(InvoiceEntityData o)
   {
      return this.fintroId.compareTo(o.fintroId);
   }

}
