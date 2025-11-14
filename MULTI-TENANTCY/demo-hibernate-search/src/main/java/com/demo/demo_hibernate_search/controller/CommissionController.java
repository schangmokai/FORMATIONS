package com.demo.demo_hibernate_search.controller;

import com.demo.demo_hibernate_search.dto.CommissionDTO;
import com.demo.demo_hibernate_search.service.IService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CommissionController {

    private final IService iService;

    public CommissionController(IService iService) {
        this.iService = iService;
    }

    @GetMapping("/globalSearchByCommission/{searchTerm}")
    public List<CommissionDTO> globalSearchByCommission(@PathVariable String searchTerm){
        return iService.globalSearchByCommission(searchTerm);
    }
}
