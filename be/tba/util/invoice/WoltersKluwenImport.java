package be.tba.util.invoice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.invoice.interfaces.InvoiceEntityData;
import be.tba.util.constants.Constants;
import be.tba.util.session.AccountCache;
import be.tba.util.timer.CallCalendar;

public class WoltersKluwenImport
{
    private static class CreditNoteSpecials
    {
        protected String debCre;
        protected String debCreRev;
        protected String vat1Inv;
        protected String vat1Btw;
        
        private CreditNoteSpecials(String debCre, String debCreRev, String vat1Inv, String vat1Btw)
        {
            this.debCre = debCre;
            this.debCreRev = debCreRev;
            this.vat1Inv = vat1Inv;
            this.vat1Btw = vat1Btw;
        }
    }
    
    private static final String[] kKwartalen = 
        { "1", "1", "1", "2", "2", "2", "3", "3", "3", "4", "4", "4" };
    private static final CreditNoteSpecials[] kCreditNoteSpecials = 
        {
                // regular invoice
                new CreditNoteSpecials( "1", "-1", "03", "54" ),
                //Credit note
                new CreditNoteSpecials( "-1", "1", "49", "64" )
        };
    
    private WoltersKluwenImport()
    {
        
    }
    
    static public File generateVerkopenXml(Collection<InvoiceEntityData> invoiceList)
    {
        //force the use of the ',' decimal separator by using explicit Belgium localization
    	DecimalFormat costFormatter = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(new Locale("nl", "BE")));
    	CallCalendar calendar = new CallCalendar();
    	
        File xml = new File(Constants.FILEUPLOAD_DIR + File.separator + Calendar.getInstance().getTimeInMillis() + Constants.WC_VERKOPEN_XML);
        StringBuffer xmlBuf = new StringBuffer("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");        
        xmlBuf.append("\r\n<ImportExpMPlus xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\r\n<Sales>\r\n");
        for (Iterator<InvoiceEntityData> i = invoiceList.iterator(); i.hasNext();)
        {
            InvoiceEntityData vEntry = i.next();
            String exclStr = costFormatter.format(vEntry.getTotalCost());
            String vatStr = costFormatter.format(vEntry.getTotalCost()*0.21);
            String inclStr = costFormatter.format(new Double(exclStr.replace(',', '.')) + new Double(vatStr.replace(',', '.')));
            
//            String exclStr = formatValue(vEntry.getTotalCost(), costFormatter);
//            String vatStr = formatValue(vEntry.getTotalCost()*0.21, costFormatter);
//            String inclStr = formatValue(new Double(costFormatter.format(exclStr)) + new Double(costFormatter.format(vatStr)), costFormatter);
            
            int yearInd = vEntry.getInvoiceNr().indexOf('-') + 1;
            String year = "20" + vEntry.getInvoiceNr().substring(yearInd, yearInd + 2);
            int debCreIndex = vEntry.getCreditId() == 0 ? 1 : 0;
            
            calendar.getWrappedCalendar().setTimeInMillis(vEntry.getStopTime());
            String dateStr = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getWrappedCalendar().getTime());
            //String dateStr = vEntry.getInvoiceDate();

            xmlBuf.append("<Sale>\r\n");
            xmlBuf.append("<Customer_Prime>");
            if (AccountCache.getInstance().get(vEntry.getAccountFwdNr()) != null)
            {
                xmlBuf.append(AccountCache.getInstance().get(vEntry.getAccountFwdNr()).getWcPrime());
            }
            else
            {
                xmlBuf.append("0");
            }
            
            xmlBuf.append("</Customer_Prime>\r\n<Journal_Prime>1</Journal_Prime>\r\n<CurrencyCode>EUR</CurrencyCode>\r\n<DocType>30</DocType>\r\n<DocNumber>");
            xmlBuf.append(vEntry.getId());
            xmlBuf.append("</DocNumber>\r\n<Amount>");
            xmlBuf.append(inclStr);
            xmlBuf.append("</Amount>\r\n<YourRef>");
            xmlBuf.append(vEntry.getInvoiceNr());
            xmlBuf.append("</YourRef>\r\n<Year_Alfa>");
            xmlBuf.append(year);
            xmlBuf.append("</Year_Alfa>\r\n<VATMonth>");
            xmlBuf.append(year + "0" + kKwartalen[vEntry.getMonth()]);
            xmlBuf.append("</VATMonth>\r\n<AccountingPeriod>");
            xmlBuf.append(kKwartalen[vEntry.getMonth()]);
            xmlBuf.append("</AccountingPeriod>\r\n<VATAmount>");
            xmlBuf.append(vatStr);
            xmlBuf.append("</VATAmount>\r\n<DocDate>");
            xmlBuf.append(dateStr);
            xmlBuf.append("</DocDate>\r\n<DueDate>");
            xmlBuf.append(dateStr);
            xmlBuf.append("</DueDate>\r\n<DocState>0</DocState>\r\n<Rate>1</Rate>\r\n<DelivDate>");
            xmlBuf.append(dateStr);
            xmlBuf.append("</DelivDate>\r\n<Status>0</Status>\r\n<Details>\r\n");
            xmlBuf.append("<Detail><Account>400000</Account>\r\n<Amount>");
            xmlBuf.append(inclStr);
            xmlBuf.append("</Amount>\r\n<DebCre>" + kCreditNoteSpecials[debCreIndex].debCre + "</DebCre>\r\n<Ventil>0</Ventil>\r\n<Unit1>0</Unit1>\r\n<Unit2>0</Unit2>\r\n</Detail>\r\n");
            xmlBuf.append("<Detail>\r\n<Account>700000</Account>\r\n<Amount>");
            xmlBuf.append(exclStr);
            xmlBuf.append("</Amount>\r\n<DebCre>" + kCreditNoteSpecials[debCreIndex].debCreRev + "</DebCre>\r\n<Ventil>4</Ventil>\r\n<Unit1>0</Unit1>\r\n<Unit2>0</Unit2>\r\n<VAT1>" + kCreditNoteSpecials[debCreIndex].vat1Inv + "</VAT1>\r\n</Detail>\r\n");
            xmlBuf.append("<Detail>\r\n<Account>451000</Account>\r\n<Amount>");
            xmlBuf.append(vatStr);
            xmlBuf.append("</Amount>\r\n<DebCre>" + kCreditNoteSpecials[debCreIndex].debCreRev + "</DebCre>\r\n<Ventil>11</Ventil>\r\n<Unit1>0</Unit1>\r\n<Unit2>0</Unit2>\r\n<VAT1>" + kCreditNoteSpecials[debCreIndex].vat1Btw +"</VAT1>\r\n</Detail>");
            xmlBuf.append("\r\n</Details>\r\n</Sale>\r\n");
        }
        xmlBuf.append("</Sales>\r\n</ImportExpMPlus>");
        
        if (xml.exists())
        {
            xml.delete();
        }
        try
        {
            xml.createNewFile();
            FileOutputStream fileStream = new FileOutputStream(xml, true);
            fileStream.write(xmlBuf.toString().getBytes());
            fileStream.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return xml;
        
    }
    
    
    static public File generateKlantenXml(Collection<AccountEntityData> invoiceList)
    {
        File xml = new File(Constants.FILEUPLOAD_DIR + File.separator + Calendar.getInstance().getTimeInMillis() + Constants.WC_VERKOPEN_XML);
        StringBuffer xmlBuf = new StringBuffer("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");        
        xmlBuf.append("\r\n<ImportExpMPlus xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\r\n<Customers>\r\n");
        for (Iterator<AccountEntityData> i = invoiceList.iterator(); i.hasNext();)
        {
            AccountEntityData vEntry = i.next();
            
            xmlBuf.append("<Customer>\r\n");
            xmlBuf.append("<Prime>");
            if (vEntry.getWcPrime() > 0) xmlBuf.append(vEntry.getWcPrime());
            xmlBuf.append("</Prime>\r\n<Alfa>");
            xmlBuf.append(vEntry.getWcAlfa());
            xmlBuf.append("</Alfa>\r\n<Name>");
            xmlBuf.append(vEntry.getCompanyName());
            xmlBuf.append("</Name>\r\n<Street>");
            xmlBuf.append(vEntry.getStreet());
            xmlBuf.append("</Street>\r\n<City>");
            xmlBuf.append(vEntry.getCity());
            xmlBuf.append("</City>\r\n<VATNumber>");
            xmlBuf.append(vatNrFormatter(vEntry.getBtwNumber(), vEntry.getCountryCode()));
            xmlBuf.append("</VATNumber>\r\n<VATCode>");
            if (vEntry.getNoBtw())
            {
                xmlBuf.append("0</VATCode>\r\n");
            }
            else
            {
                xmlBuf.append("1</VATCode>\r\n");
            }
            xmlBuf.append("<Country>");
            xmlBuf.append(vEntry.getCountryCode());
            xmlBuf.append("</Country>\r\n");
            
            StringTokenizer strTok = new StringTokenizer(vEntry.getAccountNr(), ",");
            int y = 1;
            while (strTok.hasMoreTokens())
            {
                xmlBuf.append("<CountryBankNumber" + (y == 1 ? "" : y) + ">");
                xmlBuf.append(strTok.nextToken());
                xmlBuf.append("</CountryBankNumber" + (y == 1 ? "" : y) + ">\r\n");
                ++y;    
            }
            if (y == 1)
            {
                xmlBuf.append("<CountryBankNumber>" + vEntry.getCountryCode() + "</CountryBankNumber>\r\n");
            }
            xmlBuf.append("<CountryBankNumberDom>" + vEntry.getCountryCode() + "</CountryBankNumberDom>\r\n<Language>1</Language>\r\n<CurrencyCode>EUR</CurrencyCode>\r\n<VATStatus>1</VATStatus>\r\n<CountryVATNumber>" + vEntry.getCountryCode() + "</CountryVATNumber>");
            xmlBuf.append("\r\n<Rappel>1</Rappel>\r\n<Dom>0</Dom>\r\n<AccountSale>700000</AccountSale>\r\n<IntrastatTransport>0</IntrastatTransport>\r\n<IntrastatTransaction>0</IntrastatTransaction>");
            xmlBuf.append("\r\n<Due>0</Due>\r\n<DueDays>0</DueDays>\r\n<Ventil>4</Ventil>\r\n<IntrastatDelivery>0</IntrastatDelivery>\r\n<DISCOUNTPERC>0</DISCOUNTPERC>\r\n"); 
            xmlBuf.append("<DelivDate>0</DelivDate>\r\n<DatePayed>0</DatePayed>\r\n<ElecTb>0</ElecTb>\r\n<CODADiscountTerm>0</CODADiscountTerm>\r\n<Status>0</Status>");
            xmlBuf.append("\r\n</Customer>\r\n");
        }
        xmlBuf.append("</Customers>\r\n</ImportExpMPlus>");
        
        if (xml.exists())
        {
            xml.delete();
        }
        try
        {
            xml.createNewFile();
            FileOutputStream fileStream = new FileOutputStream(xml, true);
            String out = xmlBuf.toString();
            out = out.replace('&', ' ');
            fileStream.write(out.getBytes());
            fileStream.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return xml;
        
    }
    
    static private String bankNrFormatter(String rawStr, String country)
    {
        // belgium: 001-0871882-25
        
        
        return "";
    }
    
    static public String vatNrFormatter(String rawStr, String country)
    {
        // create bank account numbers of the following format:0887.975.711
        StringBuffer formattedStr = new StringBuffer("");
        
        
        if (rawStr != null && rawStr.length() > 8 && country != null && country.length() == 2)
        {
            char[] rawArr  = rawStr.toCharArray();
            char[] noCharsArr = new char[rawArr.length];
            int i = 0;
            int y = 0;
            
            switch (country)
            {
            case "BE":
            {
                for(char c : rawArr)
                {
                    int temp = (int) c;
                    if (temp >= 48 && temp <= 57)
                    {
                        noCharsArr[i-y] =  rawArr[i];
                    }
                    else
                    {
                        ++y;
                    }
                    ++i;
                }
                String noCharsStr = new String(noCharsArr);
                noCharsStr = noCharsStr.substring(0, i-y);
                if (noCharsStr.length() < 9 || noCharsStr.length() > 10)
                {
                    System.out.println("string length (" + noCharsStr.length()  + ") must be 9 or 10 length: " + noCharsStr);
                    return noCharsStr;
                }
                i = 0;
                if (noCharsStr.indexOf('0') == 0)
                {
                    ++i;
                }
                formattedStr.append('0');
                formattedStr.append(noCharsStr.substring(i, i + 3));
                formattedStr.append('.');
                formattedStr.append(noCharsStr.substring(i + 3, i + 6));
                formattedStr.append('.');
                formattedStr.append(noCharsStr.substring(i + 6, i + 9));
                break;
            }
            case "FR":
            case "DE":
            case "NL":
            case "LU":
                return rawStr;
               
            }
            //System.out.println(rawStr + " converted to " + formattedStr.toString());
        }
        else
        {
            //System.out.println(rawStr + " not the right size: " + rawStr.length());
            return rawStr;
        }
        return formattedStr.toString();
    }
    
    static private String formatValue(double value, DecimalFormat formatter)
    {
        String valueStr = formatter.format(value);
        return valueStr.replace('.', ',');        
    }
}
