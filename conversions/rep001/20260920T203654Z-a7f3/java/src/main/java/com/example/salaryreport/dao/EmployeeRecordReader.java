package com.example.salaryreport.dao;

import com.example.salaryreport.model.Employee;
import java.io.InputStream;
import java.util.List;

public interface EmployeeRecordReader {

    List<Employee> read(InputStream input);
}
