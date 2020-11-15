/*
 * Generated by XDoclet - Do not edit!
 */
package be.tba.sqldata;

import be.tba.util.constants.Constants;
import be.tba.util.invoice.InvoiceHelper;

/**
 * Data object for AccountEntity.
 *
 * @xdoclet-generated at 1-01-15
 */
public class AccountEntityData extends be.tba.sqldata.AbstractData implements Comparable<AccountEntityData>
// implements java.io.Serializable
{
   /**
    *
    */
   private static final long serialVersionUID = 1L;

   private int id;
   private int wc_prime;
   private String wc_alfa;
//    private java.lang.String userId;
//    private java.lang.String password;
   private java.lang.String fwdNumber;
   private java.lang.String role;
   private java.lang.String fullName;
//    private java.lang.String custFilter;
//    private java.lang.String stateFilter;
//    private java.lang.String dirFilter;
//    private java.lang.String lastLogin;
//    private long lastLoginTS;
//    private long previousLoginTS;
//    private boolean isRegistered;
   private boolean isAutoRelease;
   private boolean isXmlMail;
   private java.lang.String email;
   private java.lang.String gsm;
   private short invoiceType;
   private long lastInvoiceTime;
   private long lastMailTime;
   private short mailHour1;
   private short mailMinutes1;
   private short mailHour2;
   private short mailMinutes2;
   private short mailHour3;
   private short mailMinutes3;
   private int facStdInCall;
   private int facStdOutCall;
   private int facOutLevel1;
   private int facOutLevel2;
   private int facOutLevel3;
   private boolean isPriceAgendaFixed;
   private int facAgendaCall;
   private int facFaxCall;
   private short agendaPriceUnit;
   private int facSms;
   private int facCallForward;
   private int taskHourRate;
   private java.lang.String companyName;
   private java.lang.String attToName;
   private java.lang.String street;
   private java.lang.String city;
   private java.lang.String btwNumber;
   private boolean noInvoice;
   private boolean hasSubCustomers;
   private java.lang.String superCustomer;
   private int superCustomerId;
   private boolean countAllLongCalls;
   private boolean countLongFwdCalls;
   private boolean noBtw;
   private boolean noEmptyMails;
   private boolean textMail;
   private double facLong;
   private double facLongFwd;
   private int facTblMinCalls_I;
   private double facTblStartCost_I;
   private double facTblExtraCost_I;
   private int facTblMinCalls_II;
   private double facTblStartCost_II;
   private double facTblExtraCost_II;
   private int facTblMinCalls_III;
   private double facTblStartCost_III;
   private double facTblExtraCost_III;
   private int facTblMinCalls_IV;
   private double facTblStartCost_IV;
   private double facTblExtraCost_IV;
   private boolean isMailInvoice;
   private java.lang.String invoiceEmail;
   private java.lang.String accountNr;
   private String countryCode;
   private boolean isArchived;
   private String callProcessInfo;
   private int redirectAccountId;

   public AccountEntityData()
   {
//       isRegistered = false; 
      isXmlMail = false;
      invoiceType = InvoiceHelper.kStandardInvoice;
      isMailInvoice = true;
      noEmptyMails = true;
      noInvoice = false;
      textMail = false;
      countryCode = Constants.COUNTRY_CODES[0][0];
      isArchived = false;
      taskHourRate = Constants.kHourTarifCost;
      redirectAccountId = 0;
      mailHour1 = 0;
      mailMinutes1 = 0;
      mailHour2 = 0;
      mailMinutes2 = 0;
      mailHour3 = 0;
      mailMinutes3 = 0;


      facTblMinCalls_I = Constants.kFacTblMinCalls_I;
      facTblStartCost_I = Constants.kFacTblStartCost_I;
      facTblExtraCost_I = Constants.kFacTblExtraCost_I;
      facTblMinCalls_II = Constants.kFacTblMinCalls_II;
      facTblStartCost_II = Constants.kFacTblStartCost_II;
      facTblExtraCost_II = Constants.kFacTblExtraCost_II;
      facTblMinCalls_III = Constants.kFacTblMinCalls_III;
      facTblStartCost_III = Constants.kFacTblStartCost_III;
      facTblExtraCost_III = Constants.kFacTblExtraCost_III;
      facTblMinCalls_IV = Constants.kFacTblMinCalls_IV;
      facTblStartCost_IV = Constants.kFacTblStartCost_IV;
      facTblExtraCost_IV = Constants.kFacTblExtraCost_IV;

      countAllLongCalls = true;
      countLongFwdCalls = true;
      facLong = Constants.kFacLong;
      facLongFwd = Constants.kFacLongFwd;
      facStdOutCall = Constants.kOutCost;
      facFaxCall = Constants.kFaxCost;
      facSms = Constants.kSmsCost;
      facCallForward = Constants.kForwardCost;
   }

   public AccountEntityData(AccountEntityData otherData)
   {
      set(otherData);
   }

   public void set(AccountEntityData otherData)
   {
      setId(otherData.getId());
      setWcPrime(otherData.getWcPrime());
      setWcAlfa(otherData.getWcAlfa());
//       setUserId(otherData.getUserId());
//       setPassword(otherData.getPassword());
      setFwdNumber(otherData.getFwdNumber());
      setRole(otherData.getRole());
      setFullName(otherData.getFullName());
//       setCustFilter(otherData.getCustFilter());
//       setStateFilter(otherData.getStateFilter());
//       setDirFilter(otherData.getDirFilter());
//       setLastLogin(otherData.getLastLogin());
//       setLastLoginTS(otherData.getLastLoginTS());
//       setPreviousLoginTS(otherData.getPreviousLoginTS());
//       setIsRegistered(otherData.getIsRegistered());
      setIsAutoRelease(otherData.getIsAutoRelease());
      setIsXmlMail(otherData.getIsXmlMail());
      setEmail(otherData.getEmail());
      setInvoiceEmail(otherData.getInvoiceEmail());
      setGsm(otherData.getGsm());
      setInvoiceType(otherData.getInvoiceType());
      setLastInvoiceTime(otherData.getLastInvoiceTime());
      setLastMailTime(otherData.getLastMailTime());
      setMailHour1(otherData.getMailHour1());
      setMailMinutes1(otherData.getMailMinutes1());
      setMailHour2(otherData.getMailHour2());
      setMailMinutes2(otherData.getMailMinutes2());
      setMailHour3(otherData.getMailHour3());
      setMailMinutes3(otherData.getMailMinutes3());
      setFacStdInCall(otherData.getFacStdInCall());
      setFacStdOutCall(otherData.getFacStdOutCall());
      setFacOutLevel1(otherData.getFacOutLevel1());
      setFacOutLevel2(otherData.getFacOutLevel2());
      setFacOutLevel3(otherData.getFacOutLevel3());
      setIsPriceAgendaFixed(otherData.getIsPriceAgendaFixed());
      setFacAgendaCall(otherData.getFacAgendaCall());
      setFacFaxCall(otherData.getFacFaxCall());
      setAgendaPriceUnit(otherData.getAgendaPriceUnit());
      setFacSms(otherData.getFacSms());
      setFacCallForward(otherData.getFacCallForward());
      setTaskHourRate(otherData.getTaskHourRate());
      setCompanyName(otherData.getCompanyName());
      setAttToName(otherData.getAttToName());
      setStreet(otherData.getStreet());
      setCity(otherData.getCity());
      setBtwNumber(otherData.getBtwNumber());
      setNoInvoice(otherData.getNoInvoice());
      setHasSubCustomers(otherData.getHasSubCustomers());
      setSuperCustomer(otherData.getSuperCustomer());
      setSuperCustomerId(otherData.getSuperCustomerId());
      setCountAllLongCalls(otherData.getCountAllLongCalls());
      setCountLongFwdCalls(otherData.getCountLongFwdCalls());
      setNoBtw(otherData.getNoBtw());
      setNoEmptyMails(otherData.getNoEmptyMails());
      setTextMail(otherData.getTextMail());
      setFacLong(otherData.getFacLong());
      setFacLongFwd(otherData.getFacLongFwd());
      setFacTblMinCalls_I(otherData.getFacTblMinCalls_I());
      setFacTblStartCost_I(otherData.getFacTblStartCost_I());
      setFacTblExtraCost_I(otherData.getFacTblExtraCost_I());
      setFacTblMinCalls_II(otherData.getFacTblMinCalls_II());
      setFacTblStartCost_II(otherData.getFacTblStartCost_II());
      setFacTblExtraCost_II(otherData.getFacTblExtraCost_II());
      setFacTblMinCalls_III(otherData.getFacTblMinCalls_III());
      setFacTblStartCost_III(otherData.getFacTblStartCost_III());
      setFacTblExtraCost_III(otherData.getFacTblExtraCost_III());
      setFacTblMinCalls_IV(otherData.getFacTblMinCalls_IV());
      setFacTblStartCost_IV(otherData.getFacTblStartCost_IV());
      setFacTblExtraCost_IV(otherData.getFacTblExtraCost_IV());
      setIsMailInvoice(otherData.getIsMailInvoice());
      setAccountNr(otherData.getAccountNr());
      setCountryCode(otherData.getCountryCode());
      setIsArchived(otherData.getIsArchived());
      setCallProcessInfo(otherData.getCallProcessInfo());
      setRedirectAccountId(otherData.getRedirectAccountId());
   }

   public int getId()
   {
      return this.id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public int getWcPrime()
   {
      return this.wc_prime;
   }

   public void setWcPrime(int wc_prime)
   {
      this.wc_prime = wc_prime;
   }

   public java.lang.String getWcAlfa()
   {
      return this.wc_alfa;
   }

   public void setWcAlfa(java.lang.String wc_alfa)
   {
      this.wc_alfa = wc_alfa;
   }

//    public java.lang.String getUserId()
//    {
//        return this.userId;
//    }
//
//    public void setUserId(java.lang.String userId)
//    {
//        this.userId = userId;
//    }
//
//    public java.lang.String getPassword()
//    {
//        return this.password;
//    }
//
//    public void setPassword(java.lang.String password)
//    {
//        this.password = password;
//    }
//
   public java.lang.String getFwdNumber()
   {
      return this.fwdNumber;
   }

   public void setFwdNumber(java.lang.String fwdNumber)
   {
      this.fwdNumber = fwdNumber;
   }

   public java.lang.String getRole()
   {
      return this.role;
   }

   public void setRole(java.lang.String role)
   {
      this.role = role;
   }

   public java.lang.String getFullName()
   {
      return this.fullName;
   }

   public void setFullName(java.lang.String fullName)
   {
      this.fullName = fullName;
   }

//    public java.lang.String getCustFilter()
//    {
//        return this.custFilter;
//    }
//
//    public void setCustFilter(java.lang.String custFilter)
//    {
//        this.custFilter = custFilter;
//    }
//
//    public java.lang.String getStateFilter()
//    {
//        return this.stateFilter;
//    }
//
//    public void setStateFilter(java.lang.String stateFilter)
//    {
//        this.stateFilter = stateFilter;
//    }
//
//    public java.lang.String getDirFilter()
//    {
//        return this.dirFilter;
//    }
//
//    public void setDirFilter(java.lang.String dirFilter)
//    {
//        this.dirFilter = dirFilter;
//    }
//
//    public java.lang.String getLastLogin()
//    {
//        return this.lastLogin;
//    }
//
//    public void setLastLogin(java.lang.String lastLogin)
//    {
//        this.lastLogin = lastLogin;
//    }
//
//    public long getLastLoginTS()
//    {
//        return this.lastLoginTS;
//    }
//
//    public void setLastLoginTS(long lastLoginTS)
//    {
//        this.lastLoginTS = lastLoginTS;
//    }
//
//    public long getPreviousLoginTS()
//    {
//        return this.previousLoginTS;
//    }
//
//    public void setPreviousLoginTS(long previousLoginTS)
//    {
//        this.previousLoginTS = previousLoginTS;
//    }
//
//    public boolean getIsRegistered()
//    {
//        return this.isRegistered;
//    }
//
//    public void setIsRegistered(boolean isRegistered)
//    {
//        this.isRegistered = isRegistered;
//    }

   public boolean getIsAutoRelease()
   {
      return this.isAutoRelease;
   }

   public void setIsAutoRelease(boolean isAutoRelease)
   {
      this.isAutoRelease = isAutoRelease;
   }

   public boolean getIsXmlMail()
   {
      return this.isXmlMail;
   }

   public void setIsXmlMail(boolean isXmlMail)
   {
      this.isXmlMail = isXmlMail;
   }

   public java.lang.String getEmail()
   {
      return this.email;
   }

   public void setEmail(java.lang.String email)
   {
      this.email = email;
   }

   public java.lang.String getInvoiceEmail()
   {
      return this.invoiceEmail;
   }

   public void setInvoiceEmail(java.lang.String invoiceEmail)
   {
      this.invoiceEmail = invoiceEmail;
   }

   public java.lang.String getGsm()
   {
      return this.gsm;
   }

   public void setGsm(java.lang.String gsm)
   {
      this.gsm = gsm;
   }

   public short getInvoiceType()
   {
      return this.invoiceType;
   }

   public void setInvoiceType(short invoiceType)
   {
      this.invoiceType = invoiceType;
   }

   public long getLastInvoiceTime()
   {
      return this.lastInvoiceTime;
   }

   public void setLastInvoiceTime(long lastInvoiceTime)
   {
      this.lastInvoiceTime = lastInvoiceTime;
   }

   public long getLastMailTime()
   {
      return this.lastMailTime;
   }

   public void setLastMailTime(long lastMailTime)
   {
      this.lastMailTime = lastMailTime;
   }

   public short getMailHour1()
   {
      return this.mailHour1;
   }

   public void setMailHour1(short mailHour1)
   {
      this.mailHour1 = mailHour1;
   }

   public short getMailMinutes1()
   {
      return this.mailMinutes1;
   }

   public void setMailMinutes1(short mailMinutes1)
   {
      this.mailMinutes1 = mailMinutes1;
   }

   public short getMailHour2()
   {
      return this.mailHour2;
   }

   public void setMailHour2(short mailHour2)
   {
      this.mailHour2 = mailHour2;
   }

   public short getMailMinutes2()
   {
      return this.mailMinutes2;
   }

   public void setMailMinutes2(short mailMinutes2)
   {
      this.mailMinutes2 = mailMinutes2;
   }

   public short getMailHour3()
   {
      return this.mailHour3;
   }

   public void setMailHour3(short mailHour3)
   {
      this.mailHour3 = mailHour3;
   }

   public short getMailMinutes3()
   {
      return this.mailMinutes3;
   }

   public void setMailMinutes3(short mailMinutes3)
   {
      this.mailMinutes3 = mailMinutes3;
   }

   public int getFacStdInCall()
   {
      return this.facStdInCall;
   }

   public void setFacStdInCall(int facStdInCall)
   {
      this.facStdInCall = facStdInCall;
   }

   public int getFacStdOutCall()
   {
      return this.facStdOutCall;
   }

   public void setFacStdOutCall(int facStdOutCall)
   {
      this.facStdOutCall = facStdOutCall;
   }

   public int getFacOutLevel1()
   {
      return this.facOutLevel1;
   }

   public void setFacOutLevel1(int facOutLevel1)
   {
      this.facOutLevel1 = facOutLevel1;
   }

   public int getFacOutLevel2()
   {
      return this.facOutLevel2;
   }

   public void setFacOutLevel2(int facOutLevel2)
   {
      this.facOutLevel2 = facOutLevel2;
   }

   public int getFacOutLevel3()
   {
      return this.facOutLevel3;
   }

   public void setFacOutLevel3(int facOutLevel3)
   {
      this.facOutLevel3 = facOutLevel3;
   }

   public boolean getIsPriceAgendaFixed()
   {
      return this.isPriceAgendaFixed;
   }

   public void setIsPriceAgendaFixed(boolean isPriceAgendaFixed)
   {
      this.isPriceAgendaFixed = isPriceAgendaFixed;
   }

   public int getFacAgendaCall()
   {
      return this.facAgendaCall;
   }

   public void setFacAgendaCall(int facAgendaCall)
   {
      this.facAgendaCall = facAgendaCall;
   }

   public int getFacFaxCall()
   {
      return this.facFaxCall;
   }

   public void setFacFaxCall(int facFaxCall)
   {
      this.facFaxCall = facFaxCall;
   }

   public short getAgendaPriceUnit()
   {
      return this.agendaPriceUnit;
   }

   public void setAgendaPriceUnit(short agendaPriceUnit)
   {
      this.agendaPriceUnit = agendaPriceUnit;
   }

   public int getFacSms()
   {
      return this.facSms;
   }

   public void setFacSms(int facSms)
   {
      this.facSms = facSms;
   }

   public int getFacCallForward()
   {
      return this.facCallForward;
   }

   public void setFacCallForward(int facCallForward)
   {
      this.facCallForward = facCallForward;
   }

   public int getTaskHourRate()
   {
      return this.taskHourRate;
   }

   public void setTaskHourRate(int taskHourRate)
   {
      this.taskHourRate = taskHourRate;
   }

   public java.lang.String getCompanyName()
   {
      return this.companyName;
   }

   public void setCompanyName(java.lang.String companyName)
   {
      this.companyName = companyName;
   }

   public java.lang.String getAttToName()
   {
      return this.attToName;
   }

   public void setAttToName(java.lang.String attToName)
   {
      this.attToName = attToName;
   }

   public java.lang.String getStreet()
   {
      return this.street;
   }

   public void setStreet(java.lang.String street)
   {
      this.street = street;
   }

   public java.lang.String getCity()
   {
      return this.city;
   }

   public void setCity(java.lang.String city)
   {
      this.city = city;
   }

   public java.lang.String getBtwNumber()
   {
      return this.btwNumber;
   }

   public void setBtwNumber(java.lang.String btwNumber)
   {
      this.btwNumber = btwNumber;
   }

   public boolean getNoInvoice()
   {
      return this.noInvoice;
   }

   public void setNoInvoice(boolean noInvoice)
   {
      this.noInvoice = noInvoice;
   }

   public boolean getHasSubCustomers()
   {
      return this.hasSubCustomers;
   }

   public void setHasSubCustomers(boolean hasSubCustomers)
   {
      this.hasSubCustomers = hasSubCustomers;
   }

   public java.lang.String getSuperCustomer()
   {
      return this.superCustomer;
   }

   public void setSuperCustomer(java.lang.String superCustomer)
   {
      this.superCustomer = superCustomer;
   }

   public int getSuperCustomerId()
   {
      return this.superCustomerId;
   }

   public void setSuperCustomerId(int superCustomerId)
   {
      this.superCustomerId = superCustomerId;
   }

   public java.lang.String getAccountNr()
   {
      return this.accountNr;
   }

   public void setAccountNr(java.lang.String accountNr)
   {
      this.accountNr = accountNr;
   }

   public boolean getCountAllLongCalls()
   {
      return this.countAllLongCalls;
   }

   public void setCountAllLongCalls(boolean countAllLongCalls)
   {
      this.countAllLongCalls = countAllLongCalls;
   }

   public boolean getCountLongFwdCalls()
   {
      return this.countLongFwdCalls;
   }

   public void setCountLongFwdCalls(boolean countLongFwdCalls)
   {
      this.countLongFwdCalls = countLongFwdCalls;
   }

   public boolean getNoBtw()
   {
      return this.noBtw;
   }

   public void setNoBtw(boolean noBtw)
   {
      this.noBtw = noBtw;
   }

   public boolean getNoEmptyMails()
   {
      return this.noEmptyMails;
   }

   public void setNoEmptyMails(boolean noEmptyMails)
   {
      this.noEmptyMails = noEmptyMails;
   }

   public boolean getTextMail()
   {
      return this.textMail;
   }

   public void setTextMail(boolean textMail)
   {
      this.textMail = textMail;
   }

   public double getFacLong()
   {
      return this.facLong;
   }

   public void setFacLong(double facLong)
   {
      this.facLong = facLong;
   }

   public double getFacLongFwd()
   {
      return this.facLongFwd;
   }

   public void setFacLongFwd(double facLongFwd)
   {
      this.facLongFwd = facLongFwd;
   }

   public int getFacTblMinCalls_I()
   {
      return this.facTblMinCalls_I;
   }

   public void setFacTblMinCalls_I(int facTblMinCalls_I)
   {
      this.facTblMinCalls_I = facTblMinCalls_I;
   }

   public double getFacTblStartCost_I()
   {
      return this.facTblStartCost_I;
   }

   public void setFacTblStartCost_I(double facTblStartCost_I)
   {
      this.facTblStartCost_I = facTblStartCost_I;
   }

   public double getFacTblExtraCost_I()
   {
      return this.facTblExtraCost_I;
   }

   public void setFacTblExtraCost_I(double facTblExtraCost_I)
   {
      this.facTblExtraCost_I = facTblExtraCost_I;
   }

   public int getFacTblMinCalls_II()
   {
      return this.facTblMinCalls_II;
   }

   public void setFacTblMinCalls_II(int facTblMinCalls_II)
   {
      this.facTblMinCalls_II = facTblMinCalls_II;
   }

   public double getFacTblStartCost_II()
   {
      return this.facTblStartCost_II;
   }

   public void setFacTblStartCost_II(double facTblStartCost_II)
   {
      this.facTblStartCost_II = facTblStartCost_II;
   }

   public double getFacTblExtraCost_II()
   {
      return this.facTblExtraCost_II;
   }

   public void setFacTblExtraCost_II(double facTblExtraCost_II)
   {
      this.facTblExtraCost_II = facTblExtraCost_II;
   }

   public int getFacTblMinCalls_III()
   {
      return this.facTblMinCalls_III;
   }

   public void setFacTblMinCalls_III(int facTblMinCalls_III)
   {
      this.facTblMinCalls_III = facTblMinCalls_III;
   }

   public double getFacTblStartCost_III()
   {
      return this.facTblStartCost_III;
   }

   public void setFacTblStartCost_III(double facTblStartCost_III)
   {
      this.facTblStartCost_III = facTblStartCost_III;
   }

   public double getFacTblExtraCost_III()
   {
      return this.facTblExtraCost_III;
   }

   public void setFacTblExtraCost_III(double facTblExtraCost_III)
   {
      this.facTblExtraCost_III = facTblExtraCost_III;
   }

   public int getRedirectAccountId()
   {
      return this.redirectAccountId;
   }

   public void setRedirectAccountId(int redirectAccountId)
   {
      this.redirectAccountId = redirectAccountId;
   }

   public double getFacTblStartCost_IV()
   {
      return this.facTblStartCost_IV;
   }

   public void setFacTblStartCost_IV(double facTblStartCost_IV)
   {
      this.facTblStartCost_IV = facTblStartCost_IV;
   }

   public double getFacTblExtraCost_IV()
   {
      return this.facTblExtraCost_IV;
   }

   public void setFacTblExtraCost_IV(double facTblExtraCost_IV)
   {
      this.facTblExtraCost_IV = facTblExtraCost_IV;
   }

   public boolean getIsMailInvoice()
   {
      return this.isMailInvoice;
   }

   public void setIsMailInvoice(boolean isMailInvoice)
   {
      this.isMailInvoice = isMailInvoice;
   }

   public java.lang.String getCountryCode()
   {
      return this.countryCode;
   }

   public void setCountryCode(java.lang.String countryCode)
   {
      this.countryCode = countryCode;
   }

   public boolean getIsArchived()
   {
      return this.isArchived;
   }

   public void setIsArchived(boolean isArchived)
   {
      this.isArchived = isArchived;
   }

   public String getCallProcessInfo()
   {
      return this.callProcessInfo;
   }

   public void setCallProcessInfo(String info)
   {
      this.callProcessInfo = info;
   }

   public int getFacTblMinCalls_IV()
   {
      return this.facTblMinCalls_IV;
   }

   public void setFacTblMinCalls_IV(int facTblMinCalls_IV)
   {
      this.facTblMinCalls_IV = facTblMinCalls_IV;
   }

   public String toNameValueString()
   {
      StringBuilder str = new StringBuilder();

      str.append("WC_Prime=");
      str.append(this.wc_prime);
      str.append(",WC_Alfa='");
      str.append(((this.wc_alfa != null) ? this.wc_alfa : ""));
//        str.append("',UserId='");
//        str.append(((this.userId != null) ? this.userId : ""));
//        str.append("',Password='");
//        str.append(((this.password != null) ? this.password : ""));
      str.append("',FwdNumber='");
      str.append(((this.fwdNumber != null) ? this.fwdNumber : ""));
      str.append("',Role='");
      str.append(((this.role != null) ? this.role : ""));
      str.append("',FullName='");
      str.append(((this.fullName != null) ? escapeQuotes(this.fullName) : ""));
//        str.append("',CustFilter='");
//        str.append(((this.custFilter != null) ? this.custFilter : ""));
//        str.append("',StateFilter='");
//        str.append(((this.stateFilter != null) ? this.stateFilter : ""));
//        str.append("',DirFilter='");
//        str.append(((this.dirFilter != null) ? this.dirFilter : ""));
//        str.append("',LastLogin='");
//        str.append(((this.lastLogin != null) ? this.lastLogin : ""));
//        str.append("',LastLoginTS=");
//        str.append(getLastLoginTS());
//        str.append(",PrevLoginTS=");
//        str.append(getPreviousLoginTS());
//        str.append(",IsRegistered=");
//        str.append(getIsRegistered());
      str.append("',IsAutoRelease=");
      str.append(getIsAutoRelease());
      str.append(",IsXmlMail=");
      str.append(getIsXmlMail());
      str.append(",Email='");
      str.append(((this.email != null) ? this.email : ""));
      str.append("',Gsm='");
      str.append(((this.gsm != null) ? escapeQuotes(this.gsm) : ""));
      str.append("',InvoiceType=");
      str.append(getInvoiceType());
      str.append(",LastInvoiceTime=");
      str.append(getLastInvoiceTime());
      str.append(",LastMailTime=");
      str.append(getLastMailTime());
      str.append(",MailHour1=");
      str.append(getMailHour1());
      str.append(",MailMinutes1=");
      str.append(getMailMinutes1());
      str.append(",MailHour2=");
      str.append(getMailHour2());
      str.append(",MailMinutes2=");
      str.append(getMailMinutes2());
      str.append(",MailHour3=");
      str.append(getMailHour3());
      str.append(",MailMinutes3=");
      str.append(getMailMinutes3());
      str.append(",FacStdInCall=");
      str.append(getFacStdInCall());
      str.append(",FacOutLevel1=");
      str.append(getFacOutLevel1());
      str.append(",FacOutLevel2=");
      str.append(getFacOutLevel2());
      str.append(",FacOutLevel3=");
      str.append(getFacOutLevel3());
      str.append(",IsPriceAgendaFixed=");
      str.append(getIsPriceAgendaFixed());
      str.append(",FacAgendaCall=");
      str.append(getFacAgendaCall());
      str.append(",AgendaPriceUnit=");
      str.append(getAgendaPriceUnit());
      str.append(",FacSms=");
      str.append(getFacSms());
      str.append(",FacCallForward=");
      str.append(getFacCallForward());
      str.append(",FacStdOutCall=");
      str.append(getFacStdOutCall());
      str.append(",TaskHourRate=");
      str.append(getTaskHourRate());
      str.append(",CompanyName='");
      str.append(((this.companyName != null) ? escapeQuotes(this.companyName) : ""));
      str.append("',AttToName='");
      str.append(((this.attToName != null) ? escapeQuotes(this.attToName) : ""));
      str.append("',Street='");
      str.append(((this.street != null) ? escapeQuotes(this.street) : ""));
      str.append("',City='");
      str.append(((this.city != null) ? escapeQuotes(this.city) : ""));
      str.append("',BtwNumber='");
      str.append(((this.btwNumber != null) ? this.btwNumber : ""));
      str.append("',NoInvoice=");
      str.append(getNoInvoice());
      str.append(",FacFaxCall=");
      str.append(getFacFaxCall());
      str.append(",HasSubCustomers=");
      str.append(getHasSubCustomers());
      str.append(",SuperCustomer='");
      str.append(((this.superCustomer != null) ? this.superCustomer : ""));
      str.append("',SuperCustomerID=");
      str.append(getSuperCustomerId());
      str.append(",CountAllLongCalls=");
      str.append(getCountAllLongCalls());
      str.append(",CountLongFwdCalls=");
      str.append(getCountLongFwdCalls());
      str.append(",NoBtw=");
      str.append(getNoBtw());
      str.append(",NoEmptyMails=");
      str.append(getNoEmptyMails());
      str.append(",TextMail=");
      str.append(getTextMail());
      str.append(",FacLong=");
      str.append(getFacLong());
      str.append(",FacLongFwd=");
      str.append(getFacLongFwd());
      str.append(",FacTblMinCalls_I=");
      str.append(getFacTblMinCalls_I());
      str.append(",FacTblMinCalls_II=");
      str.append(getFacTblMinCalls_II());
      str.append(",FacTblMinCalls_III=");
      str.append(getFacTblMinCalls_III());
      str.append(",FacTblMinCalls_IV=");
      str.append(getFacTblMinCalls_IV());
      str.append(",FacTblStartCost_I=");
      str.append(getFacTblStartCost_I());
      str.append(",FacTblStartCost_II=");
      str.append(getFacTblStartCost_II());
      str.append(",FacTblStartCost_III=");
      str.append(getFacTblStartCost_III());
      str.append(",FacTblStartCost_IV=");
      str.append(getFacTblStartCost_IV());
      str.append(",FacTblExtraCost_I=");
      str.append(getFacTblExtraCost_I());
      str.append(",FacTblExtraCost_II=");
      str.append(getFacTblExtraCost_II());
      str.append(",FacTblExtraCost_III=");
      str.append(getFacTblExtraCost_III());
      str.append(",FacTblExtraCost_IV=");
      str.append(getFacTblExtraCost_IV());
      str.append(",IsMailInvoice=");
      str.append(getIsMailInvoice());
      str.append(",InvoiceEmail='");
      str.append(((this.invoiceEmail != null) ? this.invoiceEmail : ""));
      str.append("',AccountNr='");
      str.append(((this.accountNr != null) ? escapeQuotes(this.accountNr) : ""));
      str.append("',CountryCode='");
      str.append(((this.countryCode != null) ? escapeQuotes(this.countryCode) : "BE"));
      str.append("',IsArchived=");
      str.append(getIsArchived());
      str.append(",CallProcessInfo='");
      str.append(((this.callProcessInfo != null) ? escapeQuotes(this.callProcessInfo) : ""));
      str.append("',RedirectAccountId=");
      str.append(this.redirectAccountId);
      return (str.toString());

   }

   public String toValueString()
   {
      StringBuilder str = new StringBuilder();

      // "(1, '409031', '04/10/05', 1128528272192, 1, 220, 0, 'Nabelactie voor
      // client'. ',0 ,0 ,0 ,0 ,'')
      str.append("'0',");
      str.append(this.wc_prime);
      str.append(",'");
      str.append(((this.wc_alfa != null) ? this.wc_alfa : ""));
      str.append("','");
//        str.append(((this.userId != null) ? this.userId : ""));
//        str.append("','");
//        str.append(((this.password != null) ? this.password : ""));
//        str.append("','");
      str.append(((this.fwdNumber != null) ? this.fwdNumber : ""));
      str.append("','");
      str.append(((this.role != null) ? this.role : ""));
      str.append("','");
      str.append(((this.fullName != null) ? this.fullName : ""));
//        str.append("','");
//        str.append(((this.custFilter != null) ? this.custFilter : ""));
//        str.append("','");
//        str.append(((this.stateFilter != null) ? this.stateFilter : ""));
//        str.append("','");
//        str.append(((this.dirFilter != null) ? this.dirFilter : ""));
//        str.append("','");
//        str.append(((this.lastLogin != null) ? this.lastLogin : ""));
//        str.append("',");
//        str.append(getLastLoginTS());
//        str.append(",");
//        str.append(getPreviousLoginTS());
//        str.append(",");
//        str.append(getIsRegistered());
      str.append("',");
      str.append(getIsAutoRelease());
      str.append(",");
      str.append(getIsXmlMail());
      str.append(",'");
      str.append(((this.email != null) ? this.email : ""));
      str.append("','");
      str.append(((this.gsm != null) ? this.gsm : ""));
      str.append("',");
      str.append(getInvoiceType());
      str.append(",");
      str.append(getLastInvoiceTime());
      str.append(",");
      str.append(getLastMailTime());
      str.append(",");
      str.append(getMailHour1());
      str.append(",");
      str.append(getMailMinutes1());
      str.append(",");
      str.append(getMailHour2());
      str.append(",");
      str.append(getMailMinutes2());
      str.append(",");
      str.append(getMailHour3());
      str.append(",");
      str.append(getMailMinutes3());
      str.append(",");
      str.append(getFacStdInCall());
      str.append(",");
      str.append(getFacOutLevel1());
      str.append(",");
      str.append(getFacOutLevel2());
      str.append(",");
      str.append(getFacOutLevel3());
      str.append(",");
      str.append(getIsPriceAgendaFixed());
      str.append(",");
      str.append(getFacAgendaCall());
      str.append(",");
      str.append(getAgendaPriceUnit());
      str.append(",");
      str.append(getFacSms());
      str.append(",");
      str.append(getFacCallForward());
      str.append(",");
      str.append(getFacStdOutCall());
      str.append(",");
      str.append(getTaskHourRate());
      str.append(",'");
      str.append(((this.companyName != null) ? this.companyName : ""));
      str.append("','");
      str.append(((this.attToName != null) ? this.attToName : ""));
      str.append("','");
      str.append(((this.street != null) ? this.street : ""));
      str.append("','");
      str.append(((this.city != null) ? this.city : ""));
      str.append("','");
      str.append(((this.btwNumber != null) ? this.btwNumber : ""));
      str.append("',");
      str.append(getNoInvoice());
      str.append(",");
      str.append(getFacFaxCall());
      str.append(",");
      str.append(getHasSubCustomers());
      str.append(",'");
      str.append(((this.superCustomer != null) ? this.superCustomer : ""));
      str.append("',");
      str.append(getSuperCustomerId());
      str.append(",");
      str.append(getCountAllLongCalls());
      str.append(",");
      str.append(getCountLongFwdCalls());
      str.append(",");
      str.append(getNoBtw());
      str.append(",");
      str.append(getNoEmptyMails());
      str.append(",");
      str.append(getTextMail());
      str.append(",");
      str.append(getFacLong());
      str.append(",");
      str.append(getFacLongFwd());
      str.append(",");
      str.append(getFacTblMinCalls_I());
      str.append(",");
      str.append(getFacTblMinCalls_II());
      str.append(",");
      str.append(getFacTblMinCalls_III());
      str.append(",");
      str.append(getFacTblMinCalls_IV());
      str.append(",");
      str.append(getFacTblStartCost_I());
      str.append(",");
      str.append(getFacTblStartCost_II());
      str.append(",");
      str.append(getFacTblStartCost_III());
      str.append(",");
      str.append(getFacTblStartCost_IV());
      str.append(",");
      str.append(getFacTblExtraCost_I());
      str.append(",");
      str.append(getFacTblExtraCost_II());
      str.append(",");
      str.append(getFacTblExtraCost_III());
      str.append(",");
      str.append(getFacTblExtraCost_IV());
      str.append(",");
      str.append(getIsMailInvoice());
      str.append(",'");
      str.append(((this.invoiceEmail != null) ? this.invoiceEmail : ""));
      str.append("','");
      str.append(((this.accountNr != null) ? escapeQuotes(this.accountNr) : ""));
      str.append("','");
      str.append(((this.countryCode != null) ? escapeQuotes(this.countryCode) : "BE"));
      str.append("',");
      str.append(getIsArchived());
      str.append(",'");
      str.append(((this.callProcessInfo != null) ? this.callProcessInfo : ""));
      str.append("',");
      str.append(this.redirectAccountId);
      return (str.toString());
   }

   public boolean equals(Object pOther)
   {
      if (pOther instanceof AccountEntityData)
      {
         AccountEntityData lTest = (AccountEntityData) pOther;
         return this.id == lTest.id;
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

//        result = 37 * result + ((this.userId != null) ? this.userId.hashCode() : 0);
//
//        result = 37 * result + ((this.password != null) ? this.password.hashCode() : 0);

      result = 37 * result + ((this.fwdNumber != null) ? this.fwdNumber.hashCode() : 0);
      return result;
   }

   @Override
   public int compareTo(AccountEntityData o)
   {
      // this functions defines the sorting of the Sorted Sets in the AccountCache
      // class.
      // first it checks on the fullName. If that one results in a 0 (there is already
      // an entry with that name)
      // then a compare is done on FwdNumber, which is always unique but not a good
      // field to sort on.

      int result = this.fullName.compareTo(o.fullName);
      if (result == 0)
      {
         result = this.fwdNumber.compareTo(o.fwdNumber);
      }
      return result;
   }

}
