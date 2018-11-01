package be.tba.util.invoice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.invoice.interfaces.InvoiceEntityData;
import be.tba.util.constants.Constants;
import be.tba.util.session.AccountCache;

public class WoltersKluwenImport
{
    private WoltersKluwenImport()
    {
        
    }
    
    static public File generateVerkopenXml(Collection<InvoiceEntityData> invoiceList)
    {
        DecimalFormat costFormatter = new DecimalFormat("#0.00");
        String dateStr = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

        File xml = new File(Constants.FILEUPLOAD_DIR + File.separator + Calendar.getInstance().getTimeInMillis() + Constants.WC_VERKOPEN_XML);
        StringBuffer xmlBuf = new StringBuffer("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");        
        xmlBuf.append("\r\n<ImportExpMPlus xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\r\n<Sales>\r\n");
        for (Iterator<InvoiceEntityData> i = invoiceList.iterator(); i.hasNext();)
        {
            InvoiceEntityData vEntry = i.next();
            double vat = vEntry.getTotalCost()*0.21;
            double inclVat = vEntry.getTotalCost() + vat;
            int yearInd = vEntry.getInvoiceNr().indexOf('-') + 1;
            
            xmlBuf.append("<Sale>\r\n");
            xmlBuf.append("<Customer_Prime>");
            xmlBuf.append(AccountCache.getInstance().get(vEntry.getAccountFwdNr()).getWcPrime());
            xmlBuf.append("</Customer_Prime>\r\n<CurrencyCode>EUR</CurrencyCode>\r\n<DocType>30</DocType>\r\n<DocNumber>");
            xmlBuf.append(vEntry.getInvoiceNr());
            xmlBuf.append("</DocNumber>\r\n<Amount>");
            xmlBuf.append(costFormatter.format(vEntry.getTotalCost()));
            xmlBuf.append("</Amount>\r\n<YourRef>");
            xmlBuf.append(vEntry.getInvoiceNr());
            xmlBuf.append("</YourRef>\r\n<Year_Alfa>20");
            xmlBuf.append(vEntry.getInvoiceNr().substring(yearInd, yearInd + 2));
            xmlBuf.append("</Year_Alfa>\r\n<VATMonth>20");
            xmlBuf.append(vEntry.getInvoiceNr().substring(yearInd, yearInd + 4));
            xmlBuf.append("</VATMonth>\r\n<VATAmount>");
            xmlBuf.append(costFormatter.format(vat));
            xmlBuf.append("</VATAmount>\r\n<DocDate>");
            xmlBuf.append(dateStr);
            xmlBuf.append("</DocDate>\r\n<DueDate>");
            xmlBuf.append(dateStr);
            xmlBuf.append("</DueDate>\r\n<DocState>0</DocState>\r\n<Rate>1</Rate>\r\n<DelivDate>");
            xmlBuf.append(dateStr);
            xmlBuf.append("</DelivDate>\r\n<Status>0</Status>\r\n<Details>\r\n");
            xmlBuf.append("<Detail><Account>400000</Account><Amount>");
            xmlBuf.append(costFormatter.format(inclVat));
            xmlBuf.append("</Amount><DebCre>-1</DebCre><Ventil>0</Ventil><Unit1>0</Unit1><Unit2>0</Unit2></Detail>");
            xmlBuf.append("<Detail><Account>700000</Account><Amount>");
            xmlBuf.append(costFormatter.format(vat));
            xmlBuf.append("</Amount><DebCre>1</DebCre><Ventil>4</Ventil><Unit1>0</Unit1><Unit2>0</Unit2><VAT1>49</VAT1></Detail>");
            xmlBuf.append("<Detail><Account>498700</Account><Amount>");
            xmlBuf.append(costFormatter.format(vEntry.getTotalCost()));
            xmlBuf.append("</Amount><DebCre>1</DebCre><Ventil>11</Ventil><Unit1>0</Unit1><Unit2>0</Unit2><VAT1>64</VAT1></Detail>");
            xmlBuf.append("\r\n</Details>\r\n</Sale>");
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
            xmlBuf.append(vEntry.getWcPrime());
            xmlBuf.append("</Prime>\r\n<Alfa>");
            xmlBuf.append(vEntry.getWcAlfa());
            xmlBuf.append("</Alfa>\r\n<Name>");
            xmlBuf.append(vEntry.getCompanyName());
            xmlBuf.append("</Name>\r\n<Street>");
            xmlBuf.append(vEntry.getStreet());
            xmlBuf.append("</Street>\r\n<City>");
            xmlBuf.append(vEntry.getCity());
            xmlBuf.append("</City>\r\n<VATNumber>");
            xmlBuf.append(vEntry.getBtwNumber());
            xmlBuf.append("</VATNumber>\r\n<VATCode>\r\n");
            if (vEntry.getNoBtw())
            {
                xmlBuf.append("0</VATCode>\r\n");
            }
            else
            {
                xmlBuf.append("1</VATCode>\r\n");
            }
            xmlBuf.append("<Country>BE</Country>\r\n");
            
            StringTokenizer strTok = new StringTokenizer(vEntry.getBtwNumber(), ",");
            int y = 1;
            while (strTok.hasMoreTokens())
            {
                xmlBuf.append("<CountryBankNumber" + (y == 1 ? "" : i) + ">");
                xmlBuf.append(strTok.nextToken());
                xmlBuf.append("</CountryBankNumber" + (y == 1 ? "" : i) + ">\r\n");
                ++y;    
            }
            if (y ==1)
            {
                xmlBuf.append("<CountryBankNumber>BE</CountryBankNumber>\r\n");
            }
            xmlBuf.append("<CountryBankNumberDom>BE</CountryBankNumberDom>\r\n<Language>1</Language>\r\n<CurrencyCode>EUR</CurrencyCode>\r\n<VATStatus>1</VATStatus>\r\n<CountryVATNumber>BE</CountryVATNumber>");
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
    
    
    
}
