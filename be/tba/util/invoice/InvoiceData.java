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
    public String StructuredId = "";
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
}
