package io.tus.android.client;

import android.content.SharedPreferences;

import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.tus.java.client.TusURLDetailStore;
import io.tus.java.client.URLDetail;

public class TusPreferencesURLDetailStore implements TusURLDetailStore {
    private SharedPreferences preferences;

    public TusPreferencesURLDetailStore(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public URLDetail get(String fingerprint) {
        // Ignore empty fingerprints
        if(fingerprint.length() == 0) {
            return null;
        }

        String urlStr = preferences.getString(fingerprint, "");

        // No entry was found
        if(urlStr.length() == 0) {
            return null;
        }

        // Ignore invalid URLs
        try {
            URL url = new URL(urlStr);
            Set<String> cookiesSet = preferences.getStringSet(fingerprint + "-cookies", Collections.emptySet());
            Set<HttpCookie> cookies = new HashSet<>();
            for (String cookieStr : cookiesSet) {
                try {
                    cookies.addAll(HttpCookie.parse(cookieStr));
                } catch (Exception ignored) {
                }
            }
            return new URLDetail(url, cookies);
        } catch(MalformedURLException e) {
            remove(fingerprint);
            return null;
        }
    }

    public void set(String fingerprint, URLDetail urlDetail) {
        String urlStr = urlDetail.getUrl().toString();

        // Ignore empty fingerprints
        if(fingerprint.length() == 0) {
            return;
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(fingerprint, urlStr);
        if (!urlDetail.getCookies().isEmpty()) {
            Set<String> cookiesStrSet = new HashSet<>();
            for (HttpCookie cookie : urlDetail.getCookies()) {
                cookiesStrSet.add(cookie.toString());
            }
            editor.putStringSet(fingerprint + "-cookies", cookiesStrSet);
        }
        editor.apply();
    }

    public void updateCookies(String fingerprint, Set<HttpCookie> cookies) {
        // Ignore empty fingerprints
        if(fingerprint.length() == 0) {
            return;
        }
        if (!cookies.isEmpty()) {
            SharedPreferences.Editor editor = preferences.edit();
            Set<HttpCookie> existingCookies = get(fingerprint).getCookies();
            existingCookies.addAll(cookies);
            Set<String> cookiesStrSet = new HashSet<>();
            for (HttpCookie cookie : existingCookies) {
                cookiesStrSet.add(cookie.toString());
            }
            editor.putStringSet(fingerprint + "-cookies", cookiesStrSet);
            editor.apply();
        }
    }

    public void remove(String fingerprint) {
        // Ignore empty fingerprints
        if(fingerprint.length() == 0) {
            return;
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(fingerprint);
        editor.remove(fingerprint + "-cookies");
        editor.apply();
    }
}
