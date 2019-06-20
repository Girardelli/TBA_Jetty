package be.tba.ejb.task.session;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import be.tba.ejb.account.interfaces.AccountEntityData;
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
    public Collection<TaskEntityData> getTasksFromTillTimestamp(WebSession webSession, String fwdNr, long start, long stop)
    {
        try
        {
            Collection<TaskEntityData> vTaskList = new Vector<TaskEntityData>();
            collectInvoiceTasks(webSession, AccountCache.getInstance().get(fwdNr), vTaskList, start, stop);
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
    public Hashtable<String, Collection<TaskEntityData>> getTasksFromTillTimestampHashtable(WebSession webSession, String fwdNr, long start, long stop)
    {
        try
        {
            Hashtable<String, Collection<TaskEntityData>> vTaskList = new Hashtable<String, Collection<TaskEntityData>>();
            collectInvoiceTasksHashTable(webSession, AccountCache.getInstance().get(fwdNr), vTaskList, start, stop);

            //System.out.println("getTasksFromTillTimestamp for " + fwdNr + ": " + vTaskList.size() + " entries.");
            return vTaskList;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Hashtable<String, Collection<TaskEntityData>>();
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public void removeTask(WebSession webSession, int key)
    {
        executeSqlQuery(webSession, "DELETE FROM TaskEntity WHERE Id=" + key);
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
        Collection<TaskEntityData> vCollection =  queryAllTasksForFwdNr(webSession, customer.getFwdNumber(), start, stop);
        
        if (vCollection != null)
        {
            taskList.addAll(vCollection);
        }
        if (customer.getHasSubCustomers())
        {
            Collection<AccountEntityData> vSubCustomerList = AccountCache.getInstance().getSubCustomersList(customer.getFwdNumber());
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

    private void collectInvoiceTasksHashTable(WebSession webSession, AccountEntityData customer, Hashtable<String, Collection<TaskEntityData>> taskList, long start, long stop)
    {
        Collection<TaskEntityData> vCollection = queryAllTasksForFwdNr(webSession, customer.getFwdNumber(), start, stop);
        if (vCollection != null)
        {
            taskList.put(customer.getFwdNumber(), vCollection);
        }
        if (customer.getHasSubCustomers())
        {
            Collection<AccountEntityData> vSubCustomerList = AccountCache.getInstance().getSubCustomersList(customer.getFwdNumber());
            //System.out.println(customer.getFullName() + "has " + vSubCustomerList.size() + " sub customers");
            for (Iterator<AccountEntityData> i = vSubCustomerList.iterator(); i.hasNext();)
            {
                AccountEntityData vEntry = i.next();

                if (vEntry.getNoInvoice())
                {
                    vCollection = queryAllTasksForFwdNr(webSession, vEntry.getFwdNumber(), start, stop);
                    if (vCollection != null)
                    {
                        taskList.put(vEntry.getFwdNumber(), vCollection);
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
            entry.setIsInvoiced(rs.getBoolean(9));
            entry.setIsRecuring(rs.getBoolean(10));
            entry.setStartTime(rs.getLong(11));
            entry.setStopTime(rs.getLong(12));
            entry.setDoneBy(null2EmpthyString(rs.getString(13)));
            vVector.add(entry);
        }
        return vVector;
    }

}
