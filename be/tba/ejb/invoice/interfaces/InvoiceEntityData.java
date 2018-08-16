/*
 * Generated by XDoclet - Do not edit!
 */
package be.tba.ejb.invoice.interfaces;

/**
 * Data object for InvoiceEntity.
 *
 * @xdoclet-generated at 1-01-15
 */
public class InvoiceEntityData extends be.tba.util.data.AbstractData implements java.io.Serializable
{
    /**
    *
    */
    private static final long serialVersionUID = 1L;
    private int id;
    private java.lang.String fileName = "";
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
    private java.lang.String customerRef = "";
    private java.lang.String payDate = "";
    // -1 means regular invoice
    // 0 means this is a credit invoice
    // db id means it is a regular invoice with a credit invoice counterpart indicated by this id.
    private int creditId = -1;
    public String fintroId = "";
    public String executionDate = "";
    public String valutaDate = "";
    public String fromBankNr = "";
    public String paymentDetails = "";


    public InvoiceEntityData()
    {
        creditId = -1;
    }

    public InvoiceEntityData(int id, java.lang.String fileName, java.lang.String accountFwdNr, double totalCost, int month, int year, int yearSeqNr, java.lang.String invoiceNr, boolean frozenFlag, boolean isPayed, long startTime, long stopTime, java.lang.String customerName, boolean isInvoiceMailed, java.lang.String customerRef, java.lang.String payDate, int creditId, String fintroId, String executionDate, String valutaDate, String fromBankNr, String paymentDetails)
    {
        setId(id);
        setFileName(fileName);
        setAccountFwdNr(accountFwdNr);
        setTotalCost(totalCost);
        setMonth(month);
        setYear(year);
        setYearSeqNr(yearSeqNr);
        setInvoiceNr(invoiceNr);
        setFrozenFlag(frozenFlag);
        setIsPayed(isPayed);
        setStartTime(startTime);
        setStopTime(stopTime);
        setCustomerName(customerName);
        setIsInvoiceMailed(isInvoiceMailed);
        setCustomerRef(customerRef);
        setPayDate(payDate);
        setCreditId(creditId);
        setFintroId(fintroId);
        setExecutionDate(executionDate);
        setValutaDate(valutaDate);
        setFromBankNr(fromBankNr);
        setPaymentDetails(paymentDetails);

    }

    public InvoiceEntityData(InvoiceEntityData otherData)
    {
        setId(otherData.getId());
        setFileName(otherData.getFileName());
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
        setCustomerRef(otherData.getCustomerRef());
        setPayDate(otherData.getPayDate());
        setCreditId(otherData.getCreditId());
        setFintroId(otherData.getFintroId());
        setExecutionDate(otherData.getExecutionDate());
        setValutaDate(otherData.getValutaDate());
        setFromBankNr(otherData.getFromBankNr());
        setPaymentDetails(otherData.getPaymentDetails());
    }

    public be.tba.ejb.invoice.interfaces.InvoiceEntityPK getPrimaryKey()
    {
        be.tba.ejb.invoice.interfaces.InvoiceEntityPK pk = new be.tba.ejb.invoice.interfaces.InvoiceEntityPK(this.getId());
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

    public java.lang.String getFileName()
    {
        return this.fileName;
    }

    public void setFileName(java.lang.String fileName)
    {
        this.fileName = fileName;
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

    public String toString()
    {
        StringBuffer str = new StringBuffer("{");

        str.append(toNameValueString().toString());
        str.append('}');

        return (str.toString());
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

     public String getExecutionDate()
    {
        return this.executionDate;
    }
    
     public void setExecutionDate(String executionDate)
     {
         this.executionDate = executionDate;
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

    
    public boolean equals(Object pOther)
    {
        if (pOther instanceof InvoiceEntityData)
        {
            InvoiceEntityData lTest = (InvoiceEntityData) pOther;
            boolean lEquals = true;

            lEquals = lEquals && this.id == lTest.id;
            if (this.fileName == null)
            {
                lEquals = lEquals && (lTest.fileName == null);
            }
            else
            {
                lEquals = lEquals && this.fileName.equals(lTest.fileName);
            }
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
            lEquals = lEquals && this.frozenFlag == lTest.frozenFlag;
            lEquals = lEquals && this.creditId == lTest.creditId;
            lEquals = lEquals && this.isPayed == lTest.isPayed;
            lEquals = lEquals && this.startTime == lTest.startTime;
            lEquals = lEquals && this.stopTime == lTest.stopTime;
            if (this.customerName == null)
            {
                lEquals = lEquals && (lTest.customerName == null);
            }
            else
            {
                lEquals = lEquals && this.customerName.equals(lTest.customerName);
            }
            lEquals = lEquals && this.isInvoiceMailed == lTest.isInvoiceMailed;
            if (this.customerRef == null)
            {
                lEquals = lEquals && (lTest.customerRef == null);
            }
            else
            {
                lEquals = lEquals && this.customerRef.equals(lTest.customerRef);
            }
            if (this.payDate == null)
            {
                lEquals = lEquals && (lTest.payDate == null);
            }
            else
            {
                lEquals = lEquals && this.payDate.equals(lTest.payDate);
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

        result = 37 * result + ((this.invoiceNr != null) ? this.invoiceNr.hashCode() : 0);

        result = 37 * result + (frozenFlag ? 0 : 1);

        result = 37 * result + (isPayed ? 0 : 1);

        result = 37 * result + (int) (startTime ^ (startTime >>> 32));

        result = 37 * result + (int) (stopTime ^ (stopTime >>> 32));

        result = 37 * result + ((this.customerName != null) ? this.customerName.hashCode() : 0);

        result = 37 * result + (isInvoiceMailed ? 0 : 1);

        result = 37 * result + ((this.customerRef != null) ? this.customerRef.hashCode() : 0);

        result = 37 * result + ((this.payDate != null) ? this.payDate.hashCode() : 0);
        result = 37 * result + creditId;
        return result;
    }

    public String toNameValueString()
    {
        StringBuffer str = new StringBuffer();

        str.append("fileName='" + ((this.fileName != null) ? this.fileName : "") + 
                "',accountFwdNr='" + ((this.accountFwdNr != null) ? this.accountFwdNr : "") + 
                "',totalCost=" + getTotalCost() + 
                ",month=" + getMonth() + 
                ",year=" + getYear() + 
                ",yearSeqNr=" + getYearSeqNr() + 
                ",invoiceNr='" + ((this.getInvoiceNr() != null) ? this.getInvoiceNr() : "") + 
                "',frozenFlag=" + getFrozenFlag() + 
                ",isPayed=" + getIsPayed() + 
                ",startTime=" + getStartTime() + 
                ",stopTime=" + getStopTime() + 
                ",customerName='" + ((this.getCustomerName() != null) ? this.getCustomerName() : "") + 
                "',isInvoiceMailed=" + getIsInvoiceMailed() + 
                ",customerRef='" + ((this.getCustomerRef() != null) ? this.getCustomerRef() : "") + 
                "',payDate='" + ((this.getPayDate() != null) ? this.getPayDate() : "") + 
                "',creditId=" + getCreditId() + 
                ",fintroId='" + ((this.getFintroId() != null) ? this.getFintroId() : "") + 
                "',executionDate='" + ((this.getExecutionDate() != null) ? this.getExecutionDate() : "") +
                "',valutaDate='" + ((this.getValutaDate() != null) ? this.getValutaDate() : "") +
                "',fromBankNr='" + ((this.getFromBankNr() != null) ? this.getFromBankNr() : "") +
                "',paymentDetails='" + ((this.getPaymentDetails() != null) ? this.getPaymentDetails() : "") + "'");
                return (str.toString());
    }

    public String toValueString()
    {
        StringBuffer str = new StringBuffer();

        // "(1, '409031', '04/10/05', 1128528272192, 1, 220, 0, 'Nabelactie voor client'. ',0 ,0 ,0 ,0 ,'')
        str.append("'0','" + ((this.fileName != null) ? this.fileName : "") + 
                "','" + ((this.accountFwdNr != null) ? this.accountFwdNr : "") + 
                "'," + getTotalCost() + 
                "," + getMonth() + 
                "," + getYear() + 
                "," + getYearSeqNr() + 
                ",'" + getInvoiceNr() + 
                "'," + getFrozenFlag() + 
                "," + getIsPayed() + 
                "," + getStartTime() + 
                "," + getStopTime() + 
                ",'" + getCustomerName() + 
                "'," + getIsInvoiceMailed() + 
                ",'" + getCustomerRef() + 
                "','" + getPayDate() + 
                "'," + getCreditId() +
                ",'" + getFintroId() + 
                "','" + getExecutionDate() +
                "','" + getValutaDate() +
                "','" + getFromBankNr() +
                "','" + getPaymentDetails() + 
                "'");
        return (str.toString());
    }

}
