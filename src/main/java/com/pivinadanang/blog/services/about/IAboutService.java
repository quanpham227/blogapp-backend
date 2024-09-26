package com.pivinadanang.blog.services.about;

import com.pivinadanang.blog.dtos.AboutDTO;
import com.pivinadanang.blog.dtos.CategoryDTO;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.about.AboutResponse;
import com.pivinadanang.blog.responses.category.CategoryResponse;

import java.util.List;
import java.util.Optional;

public interface IAboutService {
    AboutResponse getAbout() throws Exception;
    AboutResponse updateAbout(long aboutId, AboutDTO about) throws Exception;

}
