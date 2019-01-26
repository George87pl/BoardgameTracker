package com.gmail.gpolomicz.boardgametracker;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

class ParseString {
//    private static final String TAG = "GPDEB";

    private ArrayList<BGEntry> informationArrayList;

    ParseString() {
        this.informationArrayList = new ArrayList<>();
    }

    void parseXML(String xmlData) {
        BGEntry currentRecord = null;
        boolean inItem = false;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT && informationArrayList.size() < 10) {

                String tagName = xpp.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
//                        Log.d(TAG, "parse: Starting tag for  " + tagName);
                        if ("item".equalsIgnoreCase(tagName)) {
                            inItem = true;
                            currentRecord = new BGEntry();
                            int id = Integer.parseInt(xpp.getAttributeValue(null, "id"));
                            currentRecord.setId(id);
//                            currentRecord.setLink("https://boardgamegeek.com/boardgame/"+id);
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
//                        Log.d(TAG, "parse: Ending tag for " + tagName);
                        if (inItem) {
                            if ("item".equalsIgnoreCase(tagName)) {
                                informationArrayList.add(currentRecord);
                                inItem = false;

                            } else if ("name".equalsIgnoreCase(tagName)) {
                                currentRecord.setName(xpp.getAttributeValue(null, "value"));

                            } else if ("yearpublished".equalsIgnoreCase(tagName)) {
                                currentRecord.setPubDate(xpp.getAttributeValue(null, "value"));
                            }
                        }
                        break;
                    default:
                        // Nothing else to do
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void parseXML(String xmlData, Integer id) {
        boolean inItem = false;
        String textValue = "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
//                        Log.d(TAG, "parse: Starting tag for  " + tagName);
                        if ("item".equalsIgnoreCase(tagName)) {
                            inItem = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
//                        Log.d(TAG, "parse: Ending tag for " + tagName);
                        if (inItem) {
                            if ("item".equalsIgnoreCase(tagName)) {
                                inItem = false;
                            } else if ("thumbnail".equalsIgnoreCase(tagName)) {
                               informationArrayList.get(id).setImage(textValue);
                            } else if ("owned".equalsIgnoreCase(tagName)) {
                                informationArrayList.get(id).setValue(Integer.parseInt(xpp.getAttributeValue(null, "value")));
                            }
                        }
                        break;
                    default:
                        // Nothing else to do
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ArrayList<BGEntry> getInformationArrayList() {
        return informationArrayList;
    }
}
