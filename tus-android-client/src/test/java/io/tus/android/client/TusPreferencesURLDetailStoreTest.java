package io.tus.android.client;

import android.app.Activity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.net.URL;

import static org.junit.Assert.assertEquals;

import io.tus.java.client.URLDetail;

@RunWith(RobolectricTestRunner.class)
public class TusPreferencesURLDetailStoreTest {

    @Test
    public void shouldSetGetAndDeleteURLs() throws Exception {
        Activity activity = Robolectric.setupActivity(Activity.class);
        TusPreferencesURLDetailStore store = new TusPreferencesURLDetailStore(activity.getSharedPreferences("tus-test", 0));
        System.out.println("hello");
        URL url = new URL("https://tusd.tusdemo.net/files/hello");
        String fingerprint = "foo";
        store.set(fingerprint, new URLDetail(url));

        assertEquals(store.get(fingerprint), url);

        store.remove(fingerprint);

        assertEquals(store.get(fingerprint), null);
    }
}
