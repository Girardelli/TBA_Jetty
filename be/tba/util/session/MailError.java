/*mRawCollection =
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.session;

final public class MailError
{

    private String mError = null;

    private static MailError mInstance = null;

    public static MailError getInstance()
    {
        if (mInstance == null)
            mInstance = new MailError();
        return mInstance;
    }

    private MailError()
    {
        mError = null;
    }

    public String getError()
    {
        return mError;
    }

    public void setError(String vError)
    {
        mError = vError;
    }
}
