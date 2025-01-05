package com.pivinadanang.blog.services.about;

import com.pivinadanang.blog.dtos.AboutDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.AboutEntity;
import com.pivinadanang.blog.repositories.AboutRepository;
import com.pivinadanang.blog.responses.about.AboutResponse;
import com.pivinadanang.blog.responses.category.CategoryResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AboutService implements IAboutService{
    private final AboutRepository aboutRepository;

    @PostConstruct
    public void init() {
        if (aboutRepository.findByUniqueKey("about_page").isEmpty()) {
            AboutEntity about = AboutEntity.builder()
                    .uniqueKey("about_page")
                    .title("PI VINA DANANG")
                    .content("PI Vina Danang là doanh nghiệp FDI ( Hàn Quốc) chuyên sản xuất & gia công hàng May mặc Xuất khẩu sang thị trường Mỹ, Châu Âu cho các khách hàng lớn J.C.Penny, Express, Ann Taylor,.... Và là Doanh nghiệp chăm lo tốt đời sống cho Người lao động..")
                    .imageUrl("http://example.com/image.jpg")
                    .address("Lô P2, Đường số 6, KCN Hòa Khánh, Đà Nẵng,  Vietnam")
                    .phoneNumber("+84 236 3 757 757")
                    .email("pidn-hr@poongin.co.kr")
                    .workingHours("7 AM - 04:30 PM")
                    .facebookLink("http://facebook.com/pivinadanang")
                    .youtube("http://youtube.com/pivinadanang")
                    .visionStatement("Trở thành thương hiệu dẫn đầu trong ngành may mặc, mang lại những sản phẩm chất lượng cao, bền vững và phong cách, đáp ứng mọi nhu cầu của khách hàng trên toàn cầu. Chúng tôi cam kết tạo dựng giá trị lâu dài, góp phần phát triển cộng đồng và thúc đẩy sự đổi mới trong lĩnh vực thời trang.")
                    .foundingDate("2016-01-01")
                    .ceoName("John Doe")
                    .build();
            aboutRepository.save(about);
        }
    }
    @Override
    public AboutResponse getAbout() throws Exception {
        AboutEntity about = aboutRepository.findByUniqueKey("about_page")
                .orElseThrow(() -> new DataNotFoundException("Cannot find about page"));
        return AboutResponse.fromAbout(about);
    }


    @Override
    @Transactional
    public AboutResponse updateAbout(long aboutId, AboutDTO about) throws Exception {
        AboutEntity aboutExisting = aboutRepository.findById(aboutId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find about with id " + aboutId));
        if (about.getTitle() != null && !about.getTitle().isEmpty()) {
            aboutExisting.setTitle(about.getTitle());
        }
        if (about.getContent() != null && !about.getContent().isEmpty()) {
            aboutExisting.setContent(about.getContent());
        }
        if (about.getImageUrl() != null && !about.getImageUrl().isEmpty()) {
            aboutExisting.setImageUrl(about.getImageUrl());
        }
        if (about.getAddress() != null && !about.getAddress().isEmpty()) {
            aboutExisting.setAddress(about.getAddress());
        }
        if (about.getPhoneNumber() != null && !about.getPhoneNumber().isEmpty()) {
            aboutExisting.setPhoneNumber(about.getPhoneNumber());
        }
        if (about.getEmail() != null && !about.getEmail().isEmpty()) {
            aboutExisting.setEmail(about.getEmail());
        }
        if (about.getWorkingHours() != null && !about.getWorkingHours().isEmpty()) {
            aboutExisting.setWorkingHours(about.getWorkingHours());
        }
        if (about.getFacebookLink() != null && !about.getFacebookLink().isEmpty()) {
            aboutExisting.setFacebookLink(about.getFacebookLink());
        }
        if (about.getYoutube() != null && !about.getYoutube().isEmpty()) {
            aboutExisting.setYoutube(about.getYoutube());
        }
        if (about.getVisionStatement() != null && !about.getVisionStatement().isEmpty()) {
            aboutExisting.setVisionStatement(about.getVisionStatement());
        }
        if (about.getFoundingDate() != null && !about.getFoundingDate().isEmpty()) {
            aboutExisting.setFoundingDate(about.getFoundingDate());
        }
        if (about.getCeoName() != null && !about.getCeoName().isEmpty()) {
            aboutExisting.setCeoName(about.getCeoName());
        }
        return AboutResponse.fromAbout(aboutRepository.save(aboutExisting));
    }

}
