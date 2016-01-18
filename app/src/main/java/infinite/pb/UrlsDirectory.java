package infinite.pb;

import java.util.HashMap;
import java.util.HashSet;

public class UrlsDirectory {

    HashSet<String> urls = new HashSet<>();

    public void fillUrlsBag()
    {
        //fetch urls from db
    }

    public void addUrl(String url)
    {
        urls.add(url);
        //updated urls bag for immediate reference

        //add url in database
    }

    public boolean incrementReq(String url)
    {
        if(!urls.contains(url))
            return false;
        else
        {
            //update value in db
            return true;
        }

    }





}


