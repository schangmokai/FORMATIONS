package com.demo.demo_hibernate_search.mappers;

import com.demo.demo_hibernate_search.dto.CommissionDTO;
import com.demo.demo_hibernate_search.entity.Commission;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class Mappers {
    public CommissionDTO fromCommission(Commission commission){
        CommissionDTO commissionDTO = new CommissionDTO();
        BeanUtils.copyProperties(commission, commissionDTO);
        return commissionDTO;
    }
}
