package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Response.AdminDashboardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Services.AdminDashboardService;

@RestController
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboardData(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ){
        AdminDashboardResponse data = adminDashboardService.getDashboardData(startDate, endDate);
        return ResponseEntity.ok(data);
    }
}
