package be.tba.ejb.task.session;

import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.invoice.interfaces.InvoiceEntityData;
import be.tba.ejb.invoice.session.InvoiceSqlAdapter;
import be.tba.ejb.task.interfaces.TaskEntityData;
import be.tba.servlets.session.WebSession;
import be.tba.util.data.AbstractSqlAdapter;
import be.tba.util.session.AccountCache;
import be.tba.util.timer.CallCalendar;

import java.sql.SQLException;
import java.sql.ResultSet;

/**
 * Session Bean Template
 *
 *
 */
public class TaskSqlAdapter extends AbstractSqlAdapter<TaskEntityData>
{

    public TaskSqlAdapter()
    {
        super("TaskEntity");
    }

    // -------------------------------------------------------------------------
    // Static
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Members
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    public Collection<TaskEntityData> getTasks(WebSession webSession, String fwdNr)
    {
        return executeSqlQuery(webSession, "SELECT * FROM TaskEntity WHERE FwdNr='" + fwdNr + "' AND IsRecuring=FALSE ORDER BY TimeStamp DESC");
    }

    public Collection<TaskEntityData> getTasksForMonthforFwdNr(WebSession webSession, String fwdNr, int month, int year)
    {
        try
        {
            CallCalendar vCalendar = new CallCalendar();
            long vStart = vCalendar.getStartOfMonth(month, year);
            long vEnd = vCalendar.getEndOfMonth(month, year);

            return queryAllTasksForFwdNr(webSession, fwdNr, vStart, vEnd);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<TaskEntityData>();
    }

    public Collection<TaskEntityData> getTasksForMonth(WebSession webSession, int month, int year)
    {
        try
        {
            CallCalendar vCalendar = new CallCalendar();
            long vStart = vCalendar.getStartOfMonth(month, year);
            long vEnd = vCalendar.getEndOfMonth(month, year);

            return queryAllTasks(webSession, vStart, vEnd);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<TaskEntityData>();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<TaskEntityData> getDoneByTasks(WebSession webSession, String empl, int month, int year)
    {
        try
        {
            CallCalendar vCalendar = new CallCalendar();
            long vStart = vCalendar.getStartOfMonth(month, year);
            long vEnd = vCalendar.getEndOfMonth(month, year);
            return executeSqlQuery(webSession, "SELECT * FROM TaskEntity WHERE DoneBy='" + empl + "' AND TimeStamp>" + vStart + " AND TimeStamp<=" + vEnd + " AND IsRecuring=FALSE ORDER BY TimeStamp DESC");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<TaskEntityData>();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Collection<TaskEntityData> getTasksFromTillTimestamp(WebSession webSession, int accountId, long start, long stop)
    {
        try
        {
            Collection<TaskEntityData> vTaskList = new Vector<TaskEntityData>();
            collectInvoiceTasks(webSession, AccountCache.getInstance().get(accountId), vTaskList, start, stop);
            return vTaskList;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<TaskEntityData>();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Hashtable<Integer, Collection<TaskEntityData>> getTasksFromTillTimestampHashtable(WebSession webSession, int accountId, long start, long stop)
    {
        try
        {
            Hashtable<Integer, Collection<TaskEntityData>> vTaskList = new Hashtable<Integer, Collection<TaskEntityData>>();
            collectInvoiceTasksHashTable(webSession, AccountCache.getInstance().get(accountId), vTaskList, start, stop);

            //System.out.println("getTasksFromTillTimestamp for " + fwdNr + ": " + vTaskList.size() + " entries.");
            return vTaskList;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Hashtable<Integer, Collection<TaskEntityData>>();
    }

    public Collection<TaskEntityData> getTasksForInvoice(WebSession webSession, int invoiceId)
    {
    	return executeSqlQuery(webSession, "SELECT * FROM TaskEntity WHERE InvoiceId=" + invoiceId + " ORDER BY TimeStamp DESC"); 
    }

    public void setInvoiceId(WebSession webSession, int key, int invoiceId)
    {
    	executeSqlQuery(webSession, "UPDATE TaskEntity set InvoiceId=" + invoiceId  + " where id=" + key); 
    }
    
    public void fixDbIds(WebSession webSession, int key, int invoiceId, int accountId)
    {
    	executeSqlQuery(webSession, "UPDATE TaskEntity set InvoiceId=" + invoiceId + ", AccountID=" + accountId + " where id=" + key); 
    }
    
    
    
    /**
     * @ejb:interface-method view-type="remote"
     */
    public void removeTask(WebSession webSession, int key)
    {
    	TaskEntityData entry = getRow(webSession, key);
    	if (entry != null && entry.getInvoiceId() == 0)
    	{
    		executeSqlQuery(webSession, "DELETE FROM TaskEntity WHERE Id=" + key);
    	}
    	else
    	{
    		System.out.println("task cannot be removed because it belongs to an invoice");
    	}
    }

    public TaskEntityData getTask(WebSession webSession, int key)
    {
        Collection<TaskEntityData> taskCollection = executeSqlQuery(webSession, "SELECT * FROM TaskEntity WHERE Id=" + key);
        if (taskCollection.size() == 1)
        {
            return taskCollection.iterator().next();
        }
        return null;
    }
    
    public void removeTasks(WebSession webSession, int accountID)
    {
        executeSqlQuery(webSession, "DELETE FROM TaskEntity WHERE FwdNr='" + accountID + "'");
    }



    /**
     * Describes the instance and its content for debugging purpose
     *
     * @return Debugging information about the instance and its content
     */
    public String toString()
    {
        return "TaskQuerySessionBean [ " + " ]";
    }

    private void collectInvoiceTasks(WebSession webSession, AccountEntityData customer, Collection<TaskEntityData> taskList, long start, long stop)
    {
        if (customer == null)
        {
        	System.out.println("collectInvoiceTasks() with customer=null");
    		return;
        }
    	Collection<TaskEntityData> vCollection =  queryAllTasksForFwdNr(webSession, customer.getFwdNumber(), start, stop);
        
        if (vCollection != null)
        {
            taskList.addAll(vCollection);
        }
        if (customer.getHasSubCustomers())
        {
            Collection<AccountEntityData> vSubCustomerList = AccountCache.getInstance().getSubCustomersList(customer.getId());
            //System.out.println(customer.getFullName() + "has " + vSubCustomerList.size() + " sub customers");
            for (Iterator<AccountEntityData> i = vSubCustomerList.iterator(); i.hasNext();)
            {
                AccountEntityData vEntry = i.next();

                if (vEntry.getNoInvoice())
                {
                    Collection<TaskEntityData> vSubCollection =  queryAllTasksForFwdNr(webSession, vEntry.getFwdNumber(), start, stop);
                    
                    if (vSubCollection != null)
                    {
                        taskList.addAll(vSubCollection);
                    }
                }
            }
        }
    }

    private void collectInvoiceTasksHashTable(WebSession webSession, AccountEntityData customer, Hashtable<Integer, Collection<TaskEntityData>> taskList, long start, long stop)
    {
        Collection<TaskEntityData> vCollection = queryAllTasksForFwdNr(webSession, customer.getFwdNumber(), start, stop);
        if (vCollection != null)
        {
            taskList.put(customer.getId(), vCollection);
        }
        if (customer.getHasSubCustomers())
        {
            Collection<AccountEntityData> vSubCustomerList = AccountCache.getInstance().getSubCustomersList(customer.getId());
            //System.out.println(customer.getFullName() + "has " + vSubCustomerList.size() + " sub customers");
            for (Iterator<AccountEntityData> i = vSubCustomerList.iterator(); i.hasNext();)
            {
                AccountEntityData vEntry = i.next();

                if (vEntry.getNoInvoice())
                {
                    vCollection = queryAllTasksForFwdNr(webSession, vEntry.getFwdNumber(), start, stop);
                    if (vCollection != null)
                    {
                        taskList.put(vEntry.getId(), vCollection);
                    }
                }
            }
        }
    }

    private Collection<TaskEntityData> queryAllTasksForFwdNr(WebSession webSession, String fwdNr, long start, long stop)
    {
        Collection<TaskEntityData> vCollection = executeSqlQuery(webSession, "SELECT * FROM TaskEntity WHERE FwdNr='" + fwdNr + "' AND TimeStamp>" + start + " AND TimeStamp<=" + stop + " AND IsRecuring=FALSE ORDER BY TimeStamp DESC");
        Collection<TaskEntityData> vRecuringCollection = executeSqlQuery(webSession, "SELECT * FROM TaskEntity WHERE FwdNr='" + fwdNr + "' AND StartTime<" + stop + " AND IsRecuring=TRUE ORDER BY TimeStamp DESC");
        if (vCollection != null)
        {
            vCollection.addAll(vRecuringCollection);
        }
        else
        {
            vCollection = vRecuringCollection;
        }
        return vCollection;
    }
    
    private Collection<TaskEntityData> queryNotInvoicedTasksForFwdNr(WebSession webSession, int accountId)
    {
    	Collection<TaskEntityData> vRecuringCollection = new Vector<TaskEntityData>();
    	Collection<TaskEntityData> vCollection = executeSqlQuery(webSession, "SELECT * FROM TaskEntity WHERE AccountID='" + accountId + "' AND IsInvoiced=false AND IsRecuring=FALSE ORDER BY TimeStamp DESC");
        
        InvoiceSqlAdapter vInvoiceSession = new InvoiceSqlAdapter();
        Calendar vStartCalendar = Calendar.getInstance();
        int year = vStartCalendar.get(Calendar.YEAR);
        int month = vStartCalendar.get(Calendar.MONTH);
        
        boolean isRecuringAlreadyInvoiced = false;
        
        // whether this recuring task has already been invoiced this month
        Collection<InvoiceEntityData> invoices = vInvoiceSession.getInvoiceList(webSession, accountId, month, year);
        if (!invoices.isEmpty())
        {
        	for (Iterator<InvoiceEntityData> iter = invoices.iterator(); iter.hasNext();)
            {
        		InvoiceEntityData vEntry = iter.next();
        		if (vEntry.getFrozenFlag())
        		{
        			isRecuringAlreadyInvoiced = true;
        			break;
        		}
            }
        }
        	
        if (!isRecuringAlreadyInvoiced)	
        {
        	long now = Calendar.getInstance().getTimeInMillis();
            vRecuringCollection = executeSqlQuery(webSession, "SELECT * FROM TaskEntity WHERE AccountID='" + accountId + "' AND StartTime<" + now + "' AND StopTime>" + now+ " AND IsRecuring=TRUE ORDER BY TimeStamp DESC");
        }
        if (vCollection != null)
        {
            vCollection.addAll(vRecuringCollection);
        }
        else
        {
            vCollection = vRecuringCollection;
        }
        return vCollection;
    }
    
    private Collection<TaskEntityData> queryAllTasks(WebSession webSession, long start, long stop)
    {
        Collection<TaskEntityData> vCollection = executeSqlQuery(webSession, "SELECT * FROM TaskEntity WHERE TimeStamp>" + start + " AND TimeStamp<=" + stop + " AND IsRecuring=FALSE ORDER BY TimeStamp DESC");
        Collection<TaskEntityData> vRecuringCollection = executeSqlQuery(webSession, "SELECT * FROM TaskEntity WHERE StartTime<" + stop + stop + " AND IsRecuring=TRUE ORDER BY TimeStamp DESC");
        if (vCollection != null)
        {
            vCollection.addAll(vRecuringCollection);
        }
        else
        {
            vCollection = vRecuringCollection;
        }
        return vCollection;
    }
    
    protected Vector<TaskEntityData> translateRsToValueObjects(ResultSet rs) throws SQLException
    {
        Vector<TaskEntityData> vVector = new Vector<TaskEntityData>();
        while (rs.next())
        {
            TaskEntityData entry = new TaskEntityData();
            entry.setId(rs.getInt(1));
            entry.setFwdNr(null2EmpthyString(rs.getString(2)));
            entry.setDate(null2EmpthyString(rs.getString(3)));
            entry.setTimeStamp(rs.getLong(4));
            entry.setIsFixedPrice(rs.getBoolean(5));
            entry.setFixedPrice(rs.getDouble(6));
            entry.setTimeSpend(rs.getInt(7));
            entry.setDescription(null2EmpthyString(rs.getString(8)));
            entry.setIsRecuring(rs.getBoolean(9));
            entry.setStartTime(rs.getLong(10));
            entry.setStopTime(rs.getLong(11));
            entry.setDoneBy(null2EmpthyString(rs.getString(12)));
            entry.setInvoiceId(rs.getInt(13));
            entry.setAccountId(rs.getInt(14));
            vVector.add(entry);
        }
        return vVector;
    }

}
