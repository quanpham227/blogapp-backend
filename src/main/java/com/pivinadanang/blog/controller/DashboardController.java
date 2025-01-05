package com.pivinadanang.blog.controller;

import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.about.AboutResponse;
import com.pivinadanang.blog.responses.dashboard.DashboardResponse;
import com.pivinadanang.blog.services.dashboard.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final IDashboardService dashboardService;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getDashboardData() {
        DashboardResponse dashboardResponse = dashboardService.getDashboardData();
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(dashboardResponse)
                .message("Get dashboard data successfully")
                .build());
    }
}
