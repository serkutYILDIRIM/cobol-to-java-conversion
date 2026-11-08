package com.example.payroll;

import com.example.payroll.controller.PayrollReportController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PayrollReportControllerTests {

    @Autowired
    private PayrollReportController controller;
    private MockMvc mockMvc;

    @BeforeEach
    void configureMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void returnsTheCompletePayrollReport() throws Exception {
        mockMvc.perform(get("/api/payroll/report"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(
                        "\"report\":\"--------------------------------------------------\\n"
                                + "MONTHLY SALARY AND BONUS REPORT\\n"
                                + "Currency: TRY\\n"
                                + "Sample rates: insurance 14%, tax 15%")))
                .andExpect(content().string(containsString(
                        "Employee ID: 1001\\n"
                                + "Employee: Sample Employee A\\n"
                                + "Department: Software\\n"
                                + "Performance (1-5): 5\\n"
                                + "Overtime (hours): 008\\n"
                                + "Base salary: 45000.00\\n"
                                + "Bonus:       6750.00\\n"
                                + "Overtime:    3375.00\\n"
                                + "Gross salary:55125.00\\n"
                                + "Insurance:   7717.50\\n"
                                + "Tax:         7111.13\\n"
                                + "Net salary:  40296.37")))
                .andExpect(content().string(containsString(
                        "PAYROLL SUMMARY\\n"
                                + "Employee count:   3\\n"
                                + "Total gross:      130350.00\\n"
                                + "Total deductions: 35064.16\\n"
                                + "Total net:        95285.84\\n"
                                + "Average net:      31761.95\\n"
                                + "Employee with highest net salary: Sample Employee A\\n"
                                + "--------------------------------------------------\\n\"")));
    }

    @Test
    void unknownPathsReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/payroll/unknown"))
                .andExpect(status().isNotFound());
    }


}
