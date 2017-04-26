package dlp.bluelupin.dlp.Models;

/**
 * Created by subod on 19-Jul-16.
 */
public class ContentData {
    private int to;

    private String next_page_url;

    private int last_page;

    private int total;

    private int per_page;

    private Data[] data;

    private int from;

    private String prev_page_url;

    private int current_page;

    public int getTo ()
    {
        return to;
    }

    public void setTo (int to)
    {
        this.to = to;
    }

    public String getNext_page_url ()
    {
        return next_page_url;
    }

    public void setNext_page_url (String next_page_url)
    {
        this.next_page_url = next_page_url;
    }

    public int getLast_page ()
    {
        return last_page;
    }

    public void setLast_page (int last_page)
    {
        this.last_page = last_page;
    }

    public int getTotal ()
    {
        return total;
    }

    public void setTotal (int total)
    {
        this.total = total;
    }

    public int getPer_page ()
    {
        return per_page;
    }

    public void setPer_page (int per_page)
    {
        this.per_page = per_page;
    }

    public Data[] getData ()
    {
        return data;
    }

    public void setData (Data[] data)
    {
        this.data = data;
    }

    public int getFrom ()
    {
        return from;
    }

    public void setFrom (int from)
    {
        this.from = from;
    }

    public String getPrev_page_url ()
    {
        return prev_page_url;
    }

    public void setPrev_page_url (String prev_page_url)
    {
        this.prev_page_url = prev_page_url;
    }

    public int getCurrent_page ()
    {
        return current_page;
    }

    public void setCurrent_page (int current_page)
    {
        this.current_page = current_page;
    }


    private Pivot pivot;

    @Override
    public String toString()
    {
        return "ClassPojo [to = "+to+", next_page_url = "+next_page_url+", last_page = "+last_page+", total = "+total+", per_page = "+per_page+", data_item = "+data+", from = "+from+", prev_page_url = "+prev_page_url+", current_page = "+current_page+"]";
    }
}
