package com.gmail.gpolomicz.boardgametracker;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class DownloadXML extends AsyncTask<String, Void, List<BGEntry>> {
    private static final String TAG = "GPDEB";

    private OnDataAvailable callback;
    private List<BGEntry> searchList;
    private String baseURL;

    interface OnDataAvailable {
        void onDataAvailable(List<BGEntry> result);
    }

    DownloadXML(OnDataAvailable callback, String baseURL) {
        this.callback = callback;
        this.baseURL = baseURL;
    }

    @Override
    protected List<BGEntry> doInBackground(String... strings) {
        String xmlFeed = downloadXML(baseURL+strings[0]);

        if (xmlFeed == null) {
            Log.e(TAG, "doInBackground: Error downloading");
        } else {
            ParseString parseString = new ParseString();
            parseString.parseXML(xmlFeed);
            searchList = parseString.getInformationArrayList();

            String xmlImage;
            for (int i=0; i<searchList.size(); i++) {
                xmlImage = downloadXML("https://www.boardgamegeek.com/xmlapi2/thing?stats=1&id=" + searchList.get(i).getId());
                parseString.parseXML(xmlImage, i);
            }
        }
        return searchList;
    }

    @Override
    protected void onPostExecute(List<BGEntry> searchList) {
        super.onPostExecute(searchList);

        if(callback != null) {
                callback.onDataAvailable(searchList);
        }
    }

    private String downloadXML(String urlPath) {

        StringBuilder xmlResult = new StringBuilder();
        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                xmlResult.append(line);
            }
            reader.close();

            return xmlResult.toString();

        } catch (MalformedURLException e) {
            Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "downloadXML: IO Exeption reading data " + e.getMessage());
        }
        return null;
    }
}
