package dlp.bluelupin.dlp.Models;

/**
 * Created by subod on 12-Aug-16.
 */
public class ServiceDate {
    private String Timestamp;

    public String getTimestamp ()
    {
        return Timestamp;
    }

    public void setTimestamp (String Timestamp)
    {
        this.Timestamp = Timestamp;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [Timestamp = "+Timestamp+"]";
    }
}
