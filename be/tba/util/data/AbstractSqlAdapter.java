package be.tba.util.data;

import java.util.Collection;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.tba.servlets.AdminDispatchServlet;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public T getRow(Connection con, int key)
    {
        Collection<T> collection = executeSqlQuery(con, "SELECT * FROM " + mTableName + " WHERE Id=" + key);
        if (collection.size() == 1)
        {
            return collection.iterator().next();
        }
        return null;
    }

    public void addRow(Connection con, AbstractData data)
    {
        executeSqlQuery(con, "INSERT INTO " + mTableName + " VALUES (" + data.toValueString() + ")");
    }

    public void deleteRow(Connection con, int key)
    {
        executeSqlQuery(con, "DELETE FROM " + mTableName + " WHERE Id=" + key);
    }

    public void updateRow(Connection con, AbstractData data)
    {
        if (data != null)
        {
            T row = getRow(con, data.getId());
            if (row != null)
            {
                executeSqlQuery(con, "UPDATE " + mTableName + " SET " + data.toNameValueString() + "WHERE Id=" + data.getId());
            }
            else
            {
                addRow(con, data);
            }
        }
    }

    public Collection<T> getAllRows(Connection con)
    {
        return executeSqlQuery(con, "SELECT * FROM " + mTableName);
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

    protected Collection<T> executeSqlQuery(Connection con, String queryStr)
    {
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            stmt = con.createStatement();
            if (queryStr.startsWith("SELECT"))
            {
                rs = stmt.executeQuery(queryStr);
                Collection<T> col = translateRsToValueObjects(rs);
                System.out.println(col.size() + " entries: SQL querry: " + queryStr);
                // log.info(col.size() + " entries: SQL querry: " + queryStr);
                sLogger.info("{} entries: SQL querry: {}", col.size(), queryStr);
                return col;
            }
            else
            {
                int cnt = stmt.executeUpdate(queryStr);
                System.out.println(cnt + " entries: SQL querry: " + queryStr);
                sLogger.info("{} entries: SQL querry: {}", cnt, queryStr);
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

    protected String escapeQuotes(String in)
    {
        if (in.indexOf('\'') >= 0)
        {
            return in.replace("'", "''");
        }
        return in;
    }

}
