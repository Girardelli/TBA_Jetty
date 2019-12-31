package be.tba.util.data;

import java.util.Calendar;
import java.util.Collection;
import java.util.Vector;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.servlets.session.WebSession;

public abstract class AbstractSqlAdapter<T>
{
    // private static Log log = LogFactory.getLog(AbstractSqlAdapter.class);

    final static Logger sLogger = LoggerFactory.getLogger(AbstractSqlAdapter.class);

    public AbstractSqlAdapter(String tableName)
    {
        mTableName = tableName;
    }

    // -------------------------------------------------------------------------
    // Static
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Members
    // -------------------------------------------------------------------------

    private String mTableName;

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    abstract protected Vector<T> translateRsToValueObjects(ResultSet rs) throws SQLException;

    public T getRow(WebSession session, int key)
    {
        Collection<T> collection = executeSqlQuery(session, "SELECT * FROM " + mTableName + " WHERE Id=" + key);
        if (collection.size() == 1)
        {
            return collection.iterator().next();
        }
        return null;
    }

    public int addRow(WebSession session, AbstractData data)
    {
    	return executeInsertSql(session, "INSERT INTO " + mTableName + " VALUES (" + data.toValueString() + ")");
    }
    
    public void deleteRow(WebSession session, int key)
    {
        executeSqlQuery(session, "DELETE FROM " + mTableName + " WHERE Id=" + key);
    }

    public void updateRow(WebSession session, AbstractData data)
    {
        if (data != null)
        {
            T row = getRow(session, data.getId());
            if (row != null)
            {
                executeSqlQuery(session, "UPDATE " + mTableName + " SET " + data.toNameValueString() + " WHERE Id=" + data.getId());
            }
            else
            {
                addRow(session, data);
            }
        }
    }

    public Collection<T> getAllRows(WebSession session)
    {
        return executeSqlQuery(session, "SELECT * FROM " + mTableName);
    }

    protected String null2EmpthyString(String str)
    {
        if (str == null || str.equals("null"))
        {
            // System.out.println("converted 'null' string to empty one");
            return "";
        }
        return str;
    }

    protected int executeInsertSql(WebSession session, String queryStr)
    {
    	Statement stmt = null;
    	ResultSet rs = null;
        try
        {
            stmt = session.getConnection().createStatement();
            if (queryStr.startsWith("INSERT"))
            {
            	long startTime = Calendar.getInstance().getTimeInMillis();
            	int cnt = stmt.executeUpdate(queryStr, Statement.RETURN_GENERATED_KEYS);
                session.addSqlTimer(Calendar.getInstance().getTimeInMillis() - startTime);
                System.out.println(cnt + " new entry: SQL query: " + queryStr);
                rs = stmt.getGeneratedKeys();
                if(cnt == 1 && rs.next())
                {
    				return rs.getInt(1);
    			}
                //sLogger.info("{} entries: SQL querry: {}", col.size(), queryStr);
            }
            else
            {
                System.out.println("Error: expected INSERT statement: " + queryStr);
                //sLogger.info("{} entries: SQL querry: {}", cnt, queryStr);
            }
            return 0;
        }
        catch (SQLException ex)
        {
            // handle any errors
            System.out.println("FAILED SQL statement: " + queryStr);
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());

            sLogger.error("FAILED SQL statement: {}", queryStr);
            sLogger.error("", ex);
        }
        finally
        {
            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed

            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (SQLException sqlEx)
                {
                } // ignore

                rs = null;
            }

            if (stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch (SQLException sqlEx)
                {
                } // ignore

                stmt = null;
            }
        }
        return 0;
    	
    	
    }
    
    protected Collection<T> executeSqlQuery(WebSession session, String queryStr)
    {
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	stmt = session.getConnection().createStatement();
            if (queryStr.startsWith("SELECT"))
            {
            	long startTime = Calendar.getInstance().getTimeInMillis();
            	rs = stmt.executeQuery(queryStr);
            	session.addSqlTimer(Calendar.getInstance().getTimeInMillis() - startTime);
            	Collection<T> col = translateRsToValueObjects(rs);
                //System.out.println(col.size() + " entries: SQL query: " + queryStr);
                
                sLogger.info("{} entries: SQL query: {}", col.size(), queryStr);
                return col;
            }
            else
            {
            	long startTime = Calendar.getInstance().getTimeInMillis();
            	int cnt = stmt.executeUpdate(queryStr);
            	session.addSqlTimer(Calendar.getInstance().getTimeInMillis() - startTime);
                System.out.println(cnt + " entries: SQL query: " + queryStr);
                //sLogger.info("{} entries: SQL querry: {}", cnt, queryStr);
                return new Vector<T>(cnt);
            }
        }
        catch (SQLException ex)
        {
            // handle any errors
            System.out.println("FAILED SQL statement: " + queryStr);
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());

            sLogger.error("FAILED SQL statement: {}", queryStr);
            sLogger.error("", ex);
        }
        finally
        {
            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed

            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (SQLException sqlEx)
                {
                } // ignore

                rs = null;
            }

            if (stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch (SQLException sqlEx)
                {
                } // ignore

                stmt = null;
            }
        }
        return new Vector<T>();
    }

    static public String escapeQuotes(String in)
    {
        if (in.indexOf('\'') >= 0)
        {
            return in.replace("'", "''");
        }
        return in;
    }

}
