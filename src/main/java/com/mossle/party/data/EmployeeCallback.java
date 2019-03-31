package com.mossle.party.data;

import java.util.ArrayList;
import java.util.List;

import com.mossle.core.csv.CsvCallback;

public class EmployeeCallback implements CsvCallback {
    private List<EmployeeDTO> employeeDtos = new ArrayList<EmployeeDTO>();

    public void process(List<String> list, int lineNo) throws Exception {
        String username = list.get(0);
        String company = list.get(1);
        String department = list.get(2);
        String position = list.get(3);
        EmployeeDTO employeeDto = new EmployeeDTO();
        employeeDto.setUsername(username);
        employeeDto.setCompany(company);
        employeeDto.setDepartment(department);
        employeeDto.setPosition(position);
        employeeDtos.add(employeeDto);
    }

    public List<EmployeeDTO> getEmployeeDtos() {
        return employeeDtos;
    }
}
