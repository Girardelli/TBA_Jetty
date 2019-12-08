package be.tba.util.invoice;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;

//import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import be.tba.ejb.task.interfaces.TaskEntityData;
import be.tba.util.constants.Constants;
import java.io.File;

public class TbaPdfInvoice
{
    private PDDocument mDocument;
    private PDPage mPage1;
    private PDPage mPage2;
    private File mFileName;
    private CustomerData mCustomerData = null;
    private InvoiceData mInvoiceData = null;
    private CallCounts mCallCounts = null;
    private Collection<TaskEntityData> mTaskList = null;
    private Collection<SubcustomerCost> mSubcustomers = null;

    DecimalFormat mCostFormatter = new DecimalFormat("#0.00");

    private final int kSpacing = 17;
    private final int kDescrSpacing = 12;

    /**
     * This will create a blank PDF and write the contents to a file.
     *
     * @param file
     *            The name of the file to write to.
     *
     * @throws IOException
     *             If there is an error writing the data.
     * @throws COSVisitorException
     *             If there is an error while generating the document.
     */
    public TbaPdfInvoice(File file, File template)
    {
        try
        {
            mFileName = file;
            mDocument = PDDocument.load(template);
            // Every document requires at least one page, so we will add one
            // blank page.

            mPage1 = (PDPage) mDocument.getDocumentCatalog().getPages().get(0);
            mPage2 = (PDPage) mDocument.getDocumentCatalog().getPages().get(1);

        }
        catch (Exception ex)
        {
            System.out.println("TbaPdfInvoice failed");
            System.err.println(ex.getStackTrace().toString());
        }
    }

    /**
     * This will create a blank document.
     *
     * @param args
     *            The command line arguments.
     *
     * @throws IOException
     *             If there is an error writing the document data.
     * @throws COSVisitorException
     *             If there is an error generating the data.
     */
    public void createInvoice()
    {
        if (mCustomerData == null || mInvoiceData == null || mCallCounts == null)
        {
            throw new IllegalArgumentException("data objects are not set.");
        }
        // init();

        try
        {
            fillAddress();
            fillCustomerRef();
            fillDescription(false);
            fillSummaryTable();
            fillTaskList();
            fillSubcustomers();
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

    }

    public void createManualInvoice()
    {
       if (mCustomerData == null || mInvoiceData == null)
       {
           throw new IllegalArgumentException("data objects are not set.");
       }
       try
       {
           fillAddress();
           fillDescription(false);
       }
       catch (Exception e)
       {
           System.err.println(e.getMessage());
           e.printStackTrace();
       }
    }
    
    public void createCreditNote()
    {
        if (mCustomerData == null)
        {
            throw new IllegalArgumentException("data objects are not set.");
        }
        try
        {
            fillAddress();
            fillCustomerRef();
            fillDescription(true);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCustomerData(CustomerData customerData)
    {
        this.mCustomerData = customerData;
    }

    public void setInvoiceData(InvoiceData invoiceData)
    {
        this.mInvoiceData = invoiceData;
    }

    public void setCallCounts(CallCounts callCounts)
    {
        this.mCallCounts = callCounts;
    }

    public void setTaskData(Collection<TaskEntityData> tasks)
    {
        mTaskList = tasks;
    }

    public void setSubCustomers(Collection<SubcustomerCost> subCustomerList)
    {
        mSubcustomers = subCustomerList;
    }

    public void setCreditNoteData(int id, double totalCost, double btw, String customerRef, String number, String structuredId)
    {
        mInvoiceData = new InvoiceData();

        mInvoiceData.Id = id;
        mInvoiceData.TotalCost = totalCost;
        mInvoiceData.Btw = btw;
        mInvoiceData.CustomerRef = customerRef;
        mInvoiceData.InvoiceNr = number;
        mInvoiceData.StructuredId = structuredId;
    }

    public void closeAndSave()
    {
        if (mDocument != null)
        {
            try
            {
                mDocument.save(mFileName.getAbsolutePath());
                mDocument.close();
                System.out.println("doc printed at : " + mFileName);
            }
            catch (Exception ex)
            {
                System.err.println(ex.getMessage());
                System.err.println(ex.getStackTrace().toString());
            }
            finally
            {

            }
        }

    }

    private void fillAddress() throws IOException
    {
        int y = 680;

        writeText(mPage1, mCustomerData.getName(), PDType1Font.TIMES_BOLD, 11, 310, y);
        y -= kSpacing;
        if (mCustomerData.getTAV() != null && !mCustomerData.getTAV().equals(""))
        {
            writeText(mPage1, "T.a.v. " + mCustomerData.getTAV(), PDType1Font.TIMES_ROMAN, 11, 310, y);
            y -= kSpacing;
        }
        writeText(mPage1, mCustomerData.getAddress1(), PDType1Font.TIMES_ROMAN, 11, 310, y);
        y -= kSpacing;
        writeText(mPage1, mCustomerData.getAddress2(), PDType1Font.TIMES_ROMAN, 11, 310, y);
        y -= kSpacing;
        if (mCustomerData.getBtwNr() != null && !mCustomerData.getBtwNr().equals(""))
        {
            writeText(mPage1, mCustomerData.getBtwNr(), PDType1Font.TIMES_ROMAN, 11, 310, y);
        }
        else
        {
            ;// System.err.println("No BTW number for " + mCustomerData.getName());
        }
    }

    private void fillCustomerRef() throws IOException
    {
        if (mInvoiceData != null && mInvoiceData.CustomerRef != null && mInvoiceData.CustomerRef.length() > 0)
        {
            int y = 590;

            // System.err.println("fillCustomerRef : " + mInvoiceData.CustomerRef);
            writeText(mPage1, "Klantreferentie: ", PDType1Font.TIMES_BOLD, 11, 90, y);
            y -= kSpacing;
            String lineArr[] = mInvoiceData.CustomerRef.split("\n");
            for (int i = 0; i < lineArr.length; ++i)
            {
                writeText(mPage1, lineArr[i], PDType1Font.TIMES_ROMAN, 11, 90, y);
                y -= kSpacing;
                // System.err.println("fillCustomerRef writes " + lineArr[i]);
            }
        }
        else
        {
            // System.err.println("No fillCustomerRef()");
        }
    }

    private void fillDescription(boolean isCreditNote) throws IOException
    {
        writeText(mPage1, mInvoiceData.StructuredId, PDType1Font.TIMES_ROMAN, 12, 205, 527);
        writeText(mPage1, String.format("%08d", mCustomerData.getId()), PDType1Font.TIMES_ROMAN, 11, 185, 497);
        writeText(mPage1, mInvoiceData.InvoiceNr, PDType1Font.TIMES_ROMAN, 11, 185, 484);
        writeText(mPage1, mInvoiceData.Date, PDType1Font.TIMES_ROMAN, 11, 185, 470);

        if (!mInvoiceData.Description.isEmpty())
        {
           final int kWidth = 60;
           int start = 0;
           int end  = kWidth;
           int len = mInvoiceData.Description.length();
           int lineCnt = 0;

           while (lineCnt < 4)
           {
              int extra = mInvoiceData.Description.indexOf(' ', end);
              if (extra > end)
              {
                 end = extra;
              }
              end = Integer.min(end, len);
              writeText(mPage1, mInvoiceData.Description.substring(start, end), PDType1Font.TIMES_ITALIC, 11, 100, 420 - (kDescrSpacing*lineCnt));
              if (start + end > len)
              {
                 break;
              }
              start = end;
              end = end + kWidth;
              ++lineCnt;
           }
        }
        else if (isCreditNote)
        {
            writeText(mPage1, "Credit nota", PDType1Font.TIMES_ITALIC, 11, 100, 420);
        }
        else
        {
            writeText(mPage1, "Diensten die werden uitgevoerd tijdens de maand " + Constants.MONTHS[mInvoiceData.Month], PDType1Font.TIMES_ITALIC, 11, 100, 420);
            writeText(mPage1, "(Detail van de kostenstaat vindt u in bijlage).", PDType1Font.TIMES_ITALIC, 11, 100, 420 - kSpacing);
        }
        writeText(mPage1, mCostFormatter.format(mInvoiceData.TotalCost), PDType1Font.TIMES_BOLD, 11, 480, 420);
        writeText(mPage1, mCostFormatter.format(mInvoiceData.Btw), PDType1Font.TIMES_BOLD, 11, 480, 358);
        writeText(mPage1, mCostFormatter.format(mInvoiceData.Btw + mInvoiceData.TotalCost), PDType1Font.TIMES_BOLD, 11, 480, 328);
    }

    private void fillSummaryTable() throws IOException
    {
        final int kCollom1 = 255;
        final int kCollom2 = 305;
        final int kCollom3 = 515;
        int y = 689;  //681;

        writeText(mPage2, Integer.toString(mCallCounts.InCalls), PDType1Font.HELVETICA, 11, kCollom1, y);
        writeText(mPage2, "Tariefgroep " + mInvoiceData.TarifGroup, PDType1Font.HELVETICA, 11, kCollom2, y);
        writeText(mPage2, mCostFormatter.format(mInvoiceData.InCost), PDType1Font.HELVETICA, 11, kCollom3, y);
        y -= kSpacing;
        writeText(mPage2, Integer.toString(mCallCounts.OutCalls), PDType1Font.HELVETICA, 11, kCollom1, y);
        writeText(mPage2, mCostFormatter.format(mInvoiceData.OutUnitCost) + " Euro per uitgaande oproep", PDType1Font.HELVETICA, 11, kCollom2, y);
        writeText(mPage2, mCostFormatter.format(mInvoiceData.OutCost), PDType1Font.HELVETICA, 11, kCollom3, y);
        y -= kSpacing;
        writeText(mPage2, Integer.toString(mCallCounts.InCalls + mCallCounts.OutCalls), PDType1Font.HELVETICA, 11, kCollom1, y);
        writeText(mPage2, mCostFormatter.format(mInvoiceData.InCost + mInvoiceData.OutCost), PDType1Font.HELVETICA, 11, kCollom3, y);
        y -= kSpacing;
        writeText(mPage2, Integer.toString(mCallCounts.FwdCalls), PDType1Font.HELVETICA, 11, kCollom1, y);
        writeText(mPage2, mCostFormatter.format(mInvoiceData.FwdUnitCost) + " Euro per doorgeschakelde oproep", PDType1Font.HELVETICA, 11, kCollom2, y);
        writeText(mPage2, mCostFormatter.format(mInvoiceData.FwdCost), PDType1Font.HELVETICA, 11, kCollom3, y);
        y -= (kSpacing);
        writeText(mPage2, Integer.toString(mCallCounts.SmsCalls), PDType1Font.HELVETICA, 11, kCollom1, y);
        writeText(mPage2, mCostFormatter.format(mInvoiceData.SmsUnitCost) + " Euro per SMS", PDType1Font.HELVETICA, 11, kCollom2, y);
        writeText(mPage2, mCostFormatter.format(mInvoiceData.SmsCost), PDType1Font.HELVETICA, 11, kCollom3, y);
        y -= kSpacing;
        writeText(mPage2, Integer.toString(mCallCounts.FaxCalls), PDType1Font.HELVETICA, 11, kCollom1, y);
        writeText(mPage2, mCostFormatter.format(mInvoiceData.FaxUnitCost) + " Euro per fax", PDType1Font.HELVETICA, 11, kCollom2, y);
        writeText(mPage2, mCostFormatter.format(mInvoiceData.FaxCost), PDType1Font.HELVETICA, 11, kCollom3, y);
        y -= (kSpacing);
        writeText(mPage2, Integer.toString(mCallCounts.LongCalls), PDType1Font.HELVETICA, 11, kCollom1, y);
        if (mCallCounts.LongCalls > 0)
        {
            writeText(mPage2, mInvoiceData.FacLongUnit + " Euro per sec (" + String.valueOf(mCallCounts.LongCallSec) + " sec.)", PDType1Font.HELVETICA, 11, kCollom2, y);
        }
        writeText(mPage2, mCostFormatter.format(mInvoiceData.LongCost), PDType1Font.HELVETICA, 11, kCollom3, y);
        y -= kSpacing;
        writeText(mPage2, Integer.toString(mCallCounts.LongFwdCalls), PDType1Font.HELVETICA, 11, kCollom1, y);
        if (mCallCounts.LongFwdCalls > 0)
        {
            writeText(mPage2, mInvoiceData.FacLongFwdUnit + " Euro per sec (" + String.valueOf(mCallCounts.LongFwdCallSec) + " sec.)", PDType1Font.HELVETICA, 11, kCollom2, y);
        }
        writeText(mPage2, mCostFormatter.format(mInvoiceData.LongFwdCost), PDType1Font.HELVETICA, 11, kCollom3, y);
        y -= (kSpacing);
        writeText(mPage2, Integer.toString(mCallCounts.AgendaCalls), PDType1Font.HELVETICA, 11, kCollom1, y);
        writeText(mPage2, mInvoiceData.AgendaCostString, PDType1Font.HELVETICA, 11, kCollom2, y);
        writeText(mPage2, mCostFormatter.format(mInvoiceData.AgendaCost), PDType1Font.HELVETICA, 11, kCollom3, y);
        y -= kSpacing;
        // tasks
        writeText(mPage2, Integer.toString(mInvoiceData.NrOfTasks), PDType1Font.HELVETICA, 11, kCollom1, y);
        writeText(mPage2, "(taak afhankelijk - zie lijst)", PDType1Font.HELVETICA, 11, kCollom2, y);
        writeText(mPage2, mCostFormatter.format(mInvoiceData.TaskCost), PDType1Font.HELVETICA, 11, kCollom3, y);
        y -= (kSpacing);
        writeText(mPage2, mCostFormatter.format(mInvoiceData.TotalCost) + " Euro", PDType1Font.HELVETICA_BOLD, 11, kCollom3, y);
    }

    private void fillTaskList() throws IOException
    {
        if (mInvoiceData.NrOfTasks == 0 || mTaskList == null || mTaskList.size() == 0)
            return;

        final int kCollom1 = 60;
        final int kCollom2 = 115;
        final int kCollom3 = 450;
        final int kCollomEnd = 550;
        final int kTop = 460;

        float y = kTop;
        int top = kTop;
        PDPage page = mPage2;

        // writeText(mPage2, "Overzicht taken:",
        // PDType1Font.HELVETICA_BOLD_OBLIQUE, 11, 60, 500);

        drawLine(page, kCollom1 - 10, y - 5 + kSpacing, kCollomEnd, y - 5 + kSpacing);
        writeText(page, "Datum", PDType1Font.HELVETICA_BOLD, 11, kCollom1, y);
        writeText(page, "Omschrijving", PDType1Font.HELVETICA_BOLD, 11, kCollom2, y);
        writeText(page, "Kost (Euro)", PDType1Font.HELVETICA_BOLD, 11, kCollom3, y);
        drawLine(page, kCollom1 - 10, y - 5, kCollomEnd, y - 5);
        y -= kSpacing;

        for (Iterator<TaskEntityData> i = mTaskList.iterator(); i.hasNext();)
        {
            TaskEntityData vEntry = ((TaskEntityData) i.next());

            String vKost;
            double vTaskCost;

            if (vEntry.getIsFixedPrice())
            {
                vTaskCost = vEntry.getFixedPrice();
                // vKost = new String(vEntry.getFixedPrice() + ".00 Euro");
            }
            else
            {
                vTaskCost = ((double) vEntry.getTimeSpend() / 60.00) * ((double) mCustomerData.getTaskHourRate() / 100.00);
            }
            vKost = new String(mCostFormatter.format(vTaskCost));

            writeText(page, vEntry.getDate(), PDType1Font.HELVETICA, 11, kCollom1, y);
            writeText(page, vKost, PDType1Font.HELVETICA, 11, kCollom3, y);
            y = writeTextWrap(page, vEntry.getDescription(), PDType1Font.HELVETICA, 11, kCollom2, y, 60);

            drawLine(page, kCollom1 - 10, y - 5, kCollomEnd, y - 5);
            y -= kSpacing;

            if (y < 90)
            {
                drawLine(page, kCollom1 - 10, top - 5 + kSpacing, kCollom1 - 10, y - 5 + kSpacing);
                drawLine(page, kCollom2 - 10, top - 5 + kSpacing, kCollom2 - 10, y - 5 + kSpacing);
                drawLine(page, kCollom3 - 10, top - 5 + kSpacing, kCollom3 - 10, y - 5 + kSpacing);
                drawLine(page, kCollomEnd, top - 5 + kSpacing, kCollomEnd, y - 5 + kSpacing);
                page = new PDPage();
                mDocument.addPage(page);
                top = 680;
                y = top;

                drawLine(page, kCollom1 - 10, y - 5 + kSpacing, kCollomEnd, y - 5 + kSpacing);
                writeText(page, "Datum", PDType1Font.HELVETICA_BOLD, 11, kCollom1, y);
                writeText(page, "Kost (Euro)", PDType1Font.HELVETICA_BOLD, 11, kCollom3, y);
                y = writeTextWrap(page, "Omschrijving", PDType1Font.HELVETICA_BOLD, 11, kCollom2, y, 60);
                drawLine(page, kCollom1 - 10, y - 5, kCollomEnd, y - 5);
                y -= kSpacing;
            }
        }
        drawLine(page, kCollom1 - 10, top - 5 + kSpacing, kCollom1 - 10, y - 5 + kSpacing);
        drawLine(page, kCollom2 - 10, top - 5 + kSpacing, kCollom2 - 10, y - 5 + kSpacing);
        drawLine(page, kCollom3 - 10, top - 5 + kSpacing, kCollom3 - 10, y - 5 + kSpacing);
        drawLine(page, kCollomEnd, top - 5 + kSpacing, kCollomEnd, y - 5 + kSpacing);
    }

    void fillSubcustomers() throws IOException
    {
        if (mSubcustomers == null || mSubcustomers.size() == 0)
            return;

        final int kCollom1 = 60;
        final int kCollom2 = 350;
        final int kCollom3 = 400;
        final int kCollom4 = 450;
        final int kCollom5 = 500;
        final int kCollomEnd = 550;
        final int kTop = 680;

        PDPage page = new PDPage();
        mDocument.addPage(page);

        float y = kTop;
        int top = kTop;

        // writeText(mPage2, "Overzicht taken:",
        // PDType1Font.HELVETICA_BOLD_OBLIQUE, 11, 60, 500);

        drawLine(page, kCollom1 - 10, y - 5 + kSpacing, kCollomEnd, y - 5 + kSpacing);
        writeText(page, "Medewerker", PDType1Font.HELVETICA_BOLD, 11, kCollom1, y);
        writeText(page, "Aantal", PDType1Font.HELVETICA_BOLD, 11, kCollom2, y);
        writeText(page, "Kost", PDType1Font.HELVETICA_BOLD, 11, kCollom3, y);
        writeText(page, "Taken", PDType1Font.HELVETICA_BOLD, 11, kCollom4, y);
        writeText(page, "Tot.", PDType1Font.HELVETICA_BOLD, 11, kCollom5, y);
        drawLine(page, kCollom1 - 10, y - 5, kCollomEnd, y - 5);
        y -= kSpacing;

        for (Iterator<SubcustomerCost> i = mSubcustomers.iterator(); i.hasNext();)
        {
            SubcustomerCost vEntry = ((SubcustomerCost) i.next());

            writeText(page, Integer.toString(vEntry.getCalls()), PDType1Font.HELVETICA, 11, kCollom2, y);
            writeText(page, mCostFormatter.format(vEntry.getCallCost()), PDType1Font.HELVETICA, 11, kCollom3, y);
            writeText(page, mCostFormatter.format(vEntry.getTaskCost()), PDType1Font.HELVETICA, 11, kCollom4, y);
            writeText(page, mCostFormatter.format(vEntry.getCallCost() + vEntry.getTaskCost()), PDType1Font.HELVETICA, 11, kCollom5, y);
            y = writeTextWrap(page, vEntry.getName(), PDType1Font.HELVETICA, 11, kCollom1, y, 60);
            drawLine(page, kCollom1 - 10, y - 5, kCollomEnd, y - 5);
            y -= kSpacing;

            if (y < 90)
            {
                drawLine(page, kCollom1 - 10, top - 5 + kSpacing, kCollom1 - 10, y - 5 + kSpacing);
                drawLine(page, kCollom2 - 10, top - 5 + kSpacing, kCollom2 - 10, y - 5 + kSpacing);
                drawLine(page, kCollom3 - 10, top - 5 + kSpacing, kCollom3 - 10, y - 5 + kSpacing);
                drawLine(page, kCollom4 - 10, top - 5 + kSpacing, kCollom4 - 10, y - 5 + kSpacing);
                drawLine(page, kCollom5 - 10, top - 5 + kSpacing, kCollom5 - 10, y - 5 + kSpacing);
                drawLine(page, kCollomEnd, top - 5 + kSpacing, kCollomEnd, y - 5 + kSpacing);
                page = new PDPage();
                mDocument.addPage(page);
                top = 680;
                y = top;

                drawLine(page, kCollom1 - 10, y - 5 + kSpacing, kCollomEnd, y - 5 + kSpacing);
                writeText(page, "Medewerker", PDType1Font.HELVETICA_BOLD, 11, kCollom1, y);
                writeText(page, "Aantal", PDType1Font.HELVETICA_BOLD, 11, kCollom2, y);
                writeText(page, "Kost", PDType1Font.HELVETICA_BOLD, 11, kCollom3, y);
                writeText(page, "Taken", PDType1Font.HELVETICA_BOLD, 11, kCollom4, y);
                writeText(page, "Tot.", PDType1Font.HELVETICA_BOLD, 11, kCollom5, y);
                drawLine(page, kCollom1 - 10, y - 5, kCollomEnd, y - 5);
                y -= kSpacing;
            }

        }
        drawLine(page, kCollom1 - 10, top - 5 + kSpacing, kCollom1 - 10, y - 5 + kSpacing);
        drawLine(page, kCollom2 - 10, top - 5 + kSpacing, kCollom2 - 10, y - 5 + kSpacing);
        drawLine(page, kCollom3 - 10, top - 5 + kSpacing, kCollom3 - 10, y - 5 + kSpacing);
        drawLine(page, kCollom4 - 10, top - 5 + kSpacing, kCollom4 - 10, y - 5 + kSpacing);
        drawLine(page, kCollom5 - 10, top - 5 + kSpacing, kCollom5 - 10, y - 5 + kSpacing);
        drawLine(page, kCollomEnd, top - 5 + kSpacing, kCollomEnd, y - 5 + kSpacing);
    }

    private float writeTextWrap(PDPage page, String text, PDFont font, int fontSize, float x, float y, int maxLineLen) throws IOException
    {
        String line = text;
        int index = 0;
        do
        {
            if (maxLineLen + index > text.length())
            {
                line = text.substring(index, text.length());
                index += maxLineLen;
            }
            else
            {
                line = text.substring(index, Math.min(maxLineLen + index, text.length()));
                int pos = line.lastIndexOf(' ');
                line = text.substring(index, pos + index);
                index += pos + 1;
            }
            writeText(page, line, font, fontSize, x, y);
            y -= kSpacing;
        } while (text.length() > index);
        y += kSpacing;
        return y;
    }

    private void writeText(PDPage page, String text, PDFont font, int fontSize, float x, float y) throws IOException
    {
        if (text == null)
        {
            System.out.println("TbaPdfInvoice.writeText: '" + text + "' is null!");
            return;
        }
        text = text.replaceAll("\t", " ");
        text = text.replaceAll("\r", "");
        text = text.replaceAll("\n", " ");
        // PDPageContentStream contentStream = new PDPageContentStream(mDocument, page,
        // true, false);
        PDPageContentStream contentStream = new PDPageContentStream(mDocument, page, PDPageContentStream.AppendMode.APPEND, false);
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        // contentStream.setNonStrokingColor(Color.blue);
        // contentStream.moveTextPositionByAmount(x, y);
        contentStream.newLineAtOffset(x, y);
        // contentStream.drawString(text);
        contentStream.showText(text);
        contentStream.endText();
        contentStream.close();
    }

    private void drawLine(PDPage page, float x1, float y1, float x2, float y2) throws IOException
    {
        PDPageContentStream contentStream = new PDPageContentStream(mDocument, page, PDPageContentStream.AppendMode.APPEND, false);
        contentStream.moveTo(x1, y1);
        contentStream.lineTo(x2, y2);
        contentStream.stroke();
        contentStream.close();
    }

}
