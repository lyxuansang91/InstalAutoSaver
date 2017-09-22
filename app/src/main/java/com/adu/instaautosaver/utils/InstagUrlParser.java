package com.adu.instaautosaver.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.adu.instaautosaver.entity.InstagMedia;
import com.adu.instaautosaver.entity.InstagUser;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Thomc on 16/02/2016.
 */
public class InstagUrlParser {
    private static final String TAG = "InstaUrlParser";
/*
                                   {"username":"nasonphoto",
                                   "is_unpublished":false,
                                   "requested_by_viewer":false,
                                   "followed_by_viewer":false,
                                   "blocked_by_viewer":false,
                                   "profile_pic_url":"https:\/\/scontent-hkg3-1.cdninstagram.com\/t51.2885-19\/s150x150\/12142579_834647593310304_1989152287_a.jpg",
                                   "full_name":"Na Son Nguyen",
                                   "has_blocked_viewer":false,
                                   "id":"200041145",
                                   "is_private":false},

                                   "is_video":false,
                                    "is_ad":false,
                                    "display_src":"https:\/\/scontent-hkg3-1.cdninstagram.com\/t51.2885-15\/e35\/12748299_218325881848143_1374753068_n.jpg?ig_cache_key=MTE4NTQ2NTAyMjA5NDEwOTU1OA%3D%3D.2",
*/

    public static void parseUrl(final String url, final InstagUrlParserCallback callback) {

        new AsyncTask<Void, Void, InstagMedia>() {
            @Override
            protected InstagMedia doInBackground(Void... params) {
                InstagMedia media = null;
                try {
                    Document document = Jsoup.connect(url).ignoreContentType(false).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").timeout(10000).followRedirects(true).get();
                    Elements photo = document.select("meta[property=og:image]");
                    Elements video = document.select("meta[property=og:video]");
                    Elements caption = document.select("meta[property=og:description]");
                    String photoUrl = photo.attr("content");
                    String videoUrl = video.attr("content");
                    String captionContent = caption.attr("content");
                    Elements user = document.select("script");
                    for (Element element : user) {
                        String jsonStr = element.html();
                        if (jsonStr.startsWith("window._sharedData")) {
                            jsonStr = jsonStr.substring(jsonStr.indexOf('{'), jsonStr.lastIndexOf('}')) + "}";
                            JSONObject jsonObject = new JSONObject(jsonStr);
                            JSONObject mediaObject = jsonObject.getJSONObject("entry_data").getJSONArray("PostPage").getJSONObject(0).getJSONObject("graphql").getJSONObject("shortcode_media");
                            JSONObject jsonOwner = mediaObject.getJSONObject("owner");
                            String id = jsonOwner.getString("id");
                            String username = jsonOwner.getString("username");
                            String profile = jsonOwner.getString("profile_pic_url");
                            InstagUser owner = new InstagUser(id, username, profile);
                            boolean isPhoto = !mediaObject.getBoolean("is_video");
                            String mediaId = mediaObject.getString("shortcode");
                            if (isPhoto) {
                                media = new InstagMedia(mediaId, photoUrl, captionContent, owner, isPhoto);
                            } else {
                                media = new InstagMedia(mediaId, videoUrl, captionContent, owner, isPhoto);
                            }
                            media.setThumbUrl(photoUrl);
                            Log.d(TAG, media.toString());
                            break;
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return media;
            }

            @Override
            protected void onPostExecute(InstagMedia result) {
                callback.onResults(result);
            }
        }.execute();
    }

    public interface InstagUrlParserCallback {
        public void onResults(InstagMedia result);
    }
}
