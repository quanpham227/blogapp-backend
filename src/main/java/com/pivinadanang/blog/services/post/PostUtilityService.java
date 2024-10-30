package com.pivinadanang.blog.services.post;

import com.github.slugify.Slugify;
import com.pivinadanang.blog.ultils.SlugUtil;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.jsoup.safety.Safelist;


@Service
public class PostUtilityService {
    public String generateSlug(String title) {
        return SlugUtil.generateSlug(title);
    }

    public String generateExcerpt(String content) {
        String cleanContent = Jsoup.clean(content, Safelist.none());
        return cleanContent.length() > 100 ? cleanContent.substring(0, 200) + "..." : cleanContent;
    }

    public String generateMetaDescription(String content) {
        // Tạo meta description với độ dài tối đa 160 ký tự
        return generateExcerpt(content);
    }

    public String generateOgDescription(String content) {
        // Tạo og description với độ dài tối đa 200 ký tự
        return generateExcerpt(content);
    }

}
