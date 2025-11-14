package com.demo.demo_hibernate_search.service;

import com.demo.demo_hibernate_search.dto.CommissionDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface IService {
    public List<CommissionDTO> globalSearchByCommission(String searchTerm);
}
