package com.pivinadanang.blog.ultils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
public class HtmlSanitizer {
    public static String sanitize(String html) {
        return Jsoup.clean(html, Safelist.basicWithImages());
    }
}
