package com.gmail.gpolomicz.boardgametracker;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class GetBggJsonData extends AsyncTask<String, Void, List<BGEntry>> implements GetRawData.OnDownloadComplete {
    private static final String TAG = "GPDEB";

    private List<BGEntry> boardGames = null;
    private String baseURL;

    private final OnDataAvailable mCallback;
    private  boolean runningOnSameThreat = false;

    interface OnDataAvailable {
        void onDataAvailable (List<BGEntry> data, DownloadStatus status);
    }

    public GetBggJsonData(String baseURL, OnDataAvailable mCallback) {
        this.baseURL = baseURL;
        this.mCallback = mCallback;
    }

    void executeOnSameThread (String searchUsername) {
        String destinationUri = baseURL+searchUsername;

        runningOnSameThreat = true;
        GetRawData getRawData = new GetRawData(this);
        getRawData.execute(destinationUri);
    }

    @Override
    protected void onPostExecute(List<BGEntry> boardGames) {
        if (mCallback != null) {
            mCallback.onDataAvailable(boardGames, DownloadStatus.OK);
        }
    }

    @Override
    protected List<BGEntry> doInBackground(String... params) {

        String destinationUri = baseURL+params[0];
        GetRawData getRawData = new GetRawData(this);
        getRawData.runInSameThread(destinationUri);
        return boardGames;
    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {

        if (status == DownloadStatus.OK) {
            boardGames = new ArrayList<>();

            try {
                JSONArray jsonArray = new JSONArray(data);

                for(int i = 0; i<jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int gameId = jsonObject.getInt("gameId");
                    String name = jsonObject.getString("name");
                    String image = jsonObject.getString("image");
                    String thumbnail = jsonObject.getString("thumbnail");
                    int yearPublished = jsonObject.getInt("yearPublished");
                    int myRating = jsonObject.getInt("rating");
                    boolean owned = jsonObject.getBoolean("owned");

                    BGEntry boardGame = new BGEntry(gameId, name, null, myRating, thumbnail, String.valueOf(yearPublished));
                    boardGames.add(boardGame);

                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "onDownloadComplete: ERROR " + e.getMessage());
                status = DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        if (runningOnSameThreat && mCallback != null) {
            mCallback.onDataAvailable(boardGames, status);
        }
    }
}
