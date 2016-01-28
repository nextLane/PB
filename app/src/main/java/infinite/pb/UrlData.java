package infinite.pb;

/**
 * Created by .hp on 16-01-2016.
 */
public class UrlData {

    final int UNBLOCKED=0;
    final int BLOCKED=1;

    private int count;
    private int status;
    private String url;

    public UrlData(String urlRequest)
    {
        count=1;
        status=UNBLOCKED;
        url=urlRequest;

    }

    public UrlData(String u, int c, int s)
    {
        this.url=u;
        this.count=c;
        this.status=s;
    }

    public String getURL()
    {
        return this.url;
    }

    public void setURL(String u)
    {
        this.url=u;
    }

    public int getStatus()
    {
        return this.status;
    }

    public void setStatus(int s)
    {
        this.status=s;
    }

    public int getCount()
    {
        return this.count;
    }

    public void setCount(int value)
    {
        this.count=value;
    }

    public void incrementCount()
    {
        this.count++;
    }
    //Check if needed



    //status check

}
