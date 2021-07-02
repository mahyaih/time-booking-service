package com.fantasy.tbs.service.impl;

import com.fantasy.tbs.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * This service provides Employee related functionality
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Override
    public Set<String> findAllEmployeesWorkingToday() {
        // TODO: Implementation is needed
        return new HashSet<>();
    }
}
