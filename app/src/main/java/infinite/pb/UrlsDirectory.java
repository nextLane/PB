package infinite.pb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class UrlsDirectory {

    HashMap<String, UrlData> urls;
    DatabaseHandler dh ;
//HashMap used for immediate reference of values and optimizing reads/writes on db, can improve
    public UrlsDirectory(Context c)
    {
        urls = new HashMap<>();
        dh= new DatabaseHandler(c);
    //    dh.onCreate(); //adding db
    }


    public void fillUrlsBag()
    {
        List<String> u=dh.getAllUrls();
        List<UrlData> ud = dh.getAllUrlsData();

        for(int index=0; index<u.size(); index++) {
            urls.put(u.get(index), ud.get(index));
        }
        //fetch urls from db
    }

    public List<UrlData> getData()
    {
        return dh.getAllUrlsData();
    }

    public void dropUrl(String url)
    {
        if(!urls.containsKey(url))
        {
            addUrl(url);
        }
        else
        {
            incrementReq(url);
        }


    }
    public void addUrl(String url)
    {
        UrlData newEntry= new UrlData(url);
        urls.put(url,newEntry);
        dh.addUrl(newEntry);
        //updated urls bag for immediate reference
        //add url in database
    }

    public boolean incrementReq(String url)
    {
            urls.get(url).incrementCount(); //update in hashmap
            dh.valueChange(url, urls.get(url).getCount());
            //update value in db
            return true;
    }

}