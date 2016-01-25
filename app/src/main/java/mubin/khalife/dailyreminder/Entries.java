package mubin.khalife.dailyreminder;

/**
 * Created by developer on 8/28/2015.
 */
public class Entries {
    public int entryId;
    public String entryTitle;
    public String entryDescription;
    public String entryDate;
    public String entryPriority;

    public Entries(Integer entryId, String entryTitle,String entryDescription,String entryDate, String entryPriority)
    {
        this.entryId = entryId;
        this.entryTitle = entryTitle;
        this.entryDescription = entryDescription;
        this.entryDate = entryDate;
        this.entryPriority = entryPriority;
    }

}
