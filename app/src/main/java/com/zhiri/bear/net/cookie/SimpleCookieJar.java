package com.zhiri.bear.net.cookie;

import com.zhiri.bear.App;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public final class SimpleCookieJar implements CookieJar {
    private final PersistentCookieStore cookieStore = new PersistentCookieStore(App.getInst());
    private final List<Cookie> allCookies = new ArrayList<>();

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        allCookies.addAll(cookies);
        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
                cookieStore.add(url, item);
            }
        }
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> result = new ArrayList<>();
        for (Cookie cookie : allCookies) {
            if (cookie.matches(url)) {
                result.add(cookie);
            }
        }
//        List<Cookie> cookies = cookieStore.get(url);
        return result;
    }
}
