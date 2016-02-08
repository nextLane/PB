package infinite.pb;

import android.content.Context;
import android.util.Log;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrlsDirectory {

    static HashMap<String, UrlData> urls;
    DatabaseHandler dh ;
//HashMap used for immediate reference of values and optimizing reads/writes on db, can improve
    public UrlsDirectory(Context c)
    {
        urls = new HashMap<>();
        dh= DatabaseHandler.getInstance(c);
    }

//TODO:Batchwise read/writes in DB
    public void fillUrlsBag()
    {   if(dh!=null) {
        List<String> u = dh.getAllUrls();
        List<UrlData> ud = dh.getAllUrlsData();
        if (u.size() > 0 && ud.size() > 0) {
            for (int index = 0; index < u.size(); index++) {
                urls.put(u.get(index), ud.get(index));
            }
        }

    }

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
            Log.d("###:","supposed to changed, the count");
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

    public void incrementReq(String url)
    {
            urls.get(url).incrementCount(); //update in hashmap
            dh.valueChange(url, urls.get(url).getCount());

    }

    public void printOutBag()
    {
        Log.d("BAG SIZE",urls.size()+"");
        for (Map.Entry<String,UrlData> entry : urls.entrySet()) {
            String key = entry.getKey();
            UrlData value = entry.getValue();
            Log.d("URL:::",key);
            Log.d("COUNT:::", ""+value.getCount());
            // do stuff
        }

        Log.d("~~~","~~~~");


    }

}