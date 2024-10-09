package com.pivinadanang.blog.services.post;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Service
public class PostUtilityService {
    public String generateSlug(String title) {
        // Bước 1: Chuẩn hóa chuỗi, loại bỏ dấu và chuyển thành chữ thường
        String normalizedTitle = Normalizer.normalize(title, Normalizer.Form.NFD);
        String slug = Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalizedTitle).replaceAll("");
        // Bước 2: Chuyển đổi thành chữ thường, loại bỏ ký tự đặc biệt, và thay thế khoảng trắng bằng dấu gạch ngang
        slug = slug.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        return slug;
    }

    public String generateExcerpt(String content) {
        // Loại bỏ URL hình ảnh hoặc các URL khác
        String plainTextContent = content.replaceAll("http[s]?://\\S+\\.(png|jpg|jpeg|gif|svg)", "");
        // Cắt đoạn trích theo độ dài
        return plainTextContent.length() > 200 ? plainTextContent.substring(0, 200) + "..." : plainTextContent;
    }

    public String generateKeywords(String title) {
        // Chuẩn hóa chuỗi, loại bỏ dấu
        String normalizedTitle = Normalizer.normalize(title, Normalizer.Form.NFD);
        String keywords = Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalizedTitle).replaceAll("");

        // Loại bỏ ký tự không phải chữ cái và số, thay thế khoảng trắng bằng dấu cách
        keywords = keywords.replaceAll("[^a-zA-Z0-9\\s]", "").trim();

        // Thay thế khoảng trắng bằng dấu cách để tạo từ khóa
        return keywords.replaceAll("\\s+", " ");
    }
}
