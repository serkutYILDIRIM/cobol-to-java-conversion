package com.example.salaryreport.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.salaryreport.TestRecords;
import com.example.salaryreport.exception.InvalidEmployeeDataException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class EmployeeRecordMapperTest {

    private final EmployeeRecordMapper mapper = new EmployeeRecordMapper();

    @Test
    void mapsFixedPositionsAndImpliedDecimalSalary() {
        var employee = mapper.map(
                TestRecords.employee("A1234", "ÇAĞRI😊", "ÖZTÜRK", "AR-GE", "00123456"), 1);

        assertThat(employee.id()).isEqualTo("A1234");
        assertThat(employee.firstName()).startsWith("ÇAĞRI😊");
        assertThat(employee.lastName()).startsWith("ÖZTÜRK");
        assertThat(employee.department()).startsWith("AR-GE");
        assertThat(employee.salary()).isEqualByComparingTo(new BigDecimal("1234.56"));
    }


}
