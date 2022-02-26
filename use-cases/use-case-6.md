# USE CASE: 6  Produce a Report on an Employee's details given an id for that employee.

## CHARACTERISTIC INFORMATION

### Goal in Context

As an *HR advisor* I want *to view an employee's details* so that *the employee's promotion request can be supported.*

### Scope

Company.

### Level

Primary task.

### Preconditions

We know the employee id. Database contains current employee details data.

### Success End Condition

A report is available for HR to use during promotion request process.

### Failed End Condition

No report is produced.

### Primary Actor

HR Advisor.

### Trigger

An employee requests a promotion.

## MAIN SUCCESS SCENARIO

1. An employee requests a promotion.
2. HR advisor captures id of the employee to get details for.
3. HR advisor extracts current employee details of that employee.
4. HR advisor uses report during promotion request process.

## EXTENSIONS

3. **Employee does not exist**:
    1. HR advisor contacts security to remove stranger requesting promotion from the premises.

## SUB-VARIATIONS

None.

## SCHEDULE

**DUE DATE**: Release 1.0