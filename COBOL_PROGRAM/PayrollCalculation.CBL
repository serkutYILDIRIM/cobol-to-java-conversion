       >>SOURCE FORMAT FREE
identification division.
program-id. PayrollCalculation.

*> Educational payroll example using fixed sample data.
*> Deduction rates are illustrative, not actual payroll rules.
*> Compile with GnuCOBOL:
*> cobc -x -free -o PayrollCalculation PayrollCalculation.txt

environment division.
configuration section.
repository.
    function all intrinsic.

data division.
working-storage section.
78 employee-count       value 3.
78 monthly-work-hours   value 160.
78 overtime-multiplier  value 1.50.
78 insurance-rate       value 0.14.
78 tax-rate             value 0.15.

01 employee-table.
   05 employee occurs employee-count times.
      10 employee-id       pic 9(4).
      10 employee-name     pic x(30).
      10 department        pic x(20).
      10 base-salary       pic 9(7)v99.
      10 overtime-hours    pic 9(3).
      10 performance-score pic 9.
      10 bonus-amount      pic 9(7)v99.
      10 overtime-pay      pic 9(7)v99.
      10 gross-salary      pic 9(8)v99.
      10 insurance-amount  pic 9(8)v99.
      10 tax-amount        pic 9(8)v99.
      10 net-salary        pic 9(8)v99.

01 employee-index        binary-long.
01 highest-paid-index    binary-long value 1.
01 bonus-rate            pic 9v99.
01 taxable-income        pic 9(8)v99.
01 total-gross           pic 9(10)v99 value zero.
01 total-deductions      pic 9(10)v99 value zero.
01 total-net             pic 9(10)v99 value zero.
01 average-net           pic 9(10)v99 value zero.
01 formatted-amount      pic Z(9)9.99.
01 report-separator      pic x(50) value all "-".

procedure division.
main-program.
    perform prepare-sample-data
    perform print-report-header
    perform varying employee-index from 1 by 1
        until employee-index > employee-count
        perform calculate-salary
        perform update-totals
        perform print-employee-report
    end-perform
    perform print-summary-report
    stop run.

prepare-sample-data.
    initialize employee-table
    move 1001 to employee-id(1)
    move "Sample Employee A" to employee-name(1)
    move "Software" to department(1)
    move 45000 to base-salary(1)
    move 8 to overtime-hours(1)
    move 5 to performance-score(1)

    move 1002 to employee-id(2)
    move "Sample Employee B" to employee-name(2)
    move "Accounting" to department(2)
    move 38000 to base-salary(2)
    move 4 to overtime-hours(2)
    move 4 to performance-score(2)

    move 1003 to employee-id(3)
    move "Sample Employee C" to employee-name(3)
    move "Operations" to department(3)
    move 32000 to base-salary(3)
    move 0 to overtime-hours(3)
    move 3 to performance-score(3).

print-report-header.
    display report-separator
    display "MONTHLY SALARY AND BONUS REPORT"
    display "Currency: TRY"
    display "Sample rates: insurance 14%, tax 15%"
    display report-separator.

calculate-salary.
    evaluate performance-score(employee-index)
        when 5 move 0.15 to bonus-rate
        when 4 move 0.10 to bonus-rate
        when other move 0 to bonus-rate
    end-evaluate
    compute bonus-amount(employee-index) rounded =
        base-salary(employee-index) * bonus-rate
    compute overtime-pay(employee-index) rounded =
        base-salary(employee-index) / monthly-work-hours
        * overtime-hours(employee-index) * overtime-multiplier
    compute gross-salary(employee-index) =
        base-salary(employee-index) + bonus-amount(employee-index)
        + overtime-pay(employee-index)
    compute insurance-amount(employee-index) rounded =
        gross-salary(employee-index) * insurance-rate
    compute taxable-income =
        gross-salary(employee-index) - insurance-amount(employee-index)
    compute tax-amount(employee-index) rounded =
        taxable-income * tax-rate
    compute net-salary(employee-index) =
        taxable-income - tax-amount(employee-index).

update-totals.
    add gross-salary(employee-index) to total-gross
    add insurance-amount(employee-index) tax-amount(employee-index)
        to total-deductions
    add net-salary(employee-index) to total-net
    if net-salary(employee-index) > net-salary(highest-paid-index)
        move employee-index to highest-paid-index
    end-if.

print-employee-report.
    display "Employee ID: " employee-id(employee-index)
    display "Employee: " function trim(employee-name(employee-index))
    display "Department: " function trim(department(employee-index))
    display "Performance (1-5): " performance-score(employee-index)
    display "Overtime (hours): " overtime-hours(employee-index)
    move base-salary(employee-index) to formatted-amount
    display "Base salary: " function trim(formatted-amount)
    move bonus-amount(employee-index) to formatted-amount
    display "Bonus:       " function trim(formatted-amount)
    move overtime-pay(employee-index) to formatted-amount
    display "Overtime:    " function trim(formatted-amount)
    move gross-salary(employee-index) to formatted-amount
    display "Gross salary:" function trim(formatted-amount)
    move insurance-amount(employee-index) to formatted-amount
    display "Insurance:   " function trim(formatted-amount)
    move tax-amount(employee-index) to formatted-amount
    display "Tax:         " function trim(formatted-amount)
    move net-salary(employee-index) to formatted-amount
    display "Net salary:  " function trim(formatted-amount)
    display report-separator.

print-summary-report.
    compute average-net rounded = total-net / employee-count
    display "PAYROLL SUMMARY"
    display "Employee count:   " employee-count
    move total-gross to formatted-amount
    display "Total gross:      " function trim(formatted-amount)
    move total-deductions to formatted-amount
    display "Total deductions: " function trim(formatted-amount)
    move total-net to formatted-amount
    display "Total net:        " function trim(formatted-amount)
    move average-net to formatted-amount
    display "Average net:      " function trim(formatted-amount)
    display "Employee with highest net salary: "
        function trim(employee-name(highest-paid-index))
    display report-separator.

end program PayrollCalculation.
