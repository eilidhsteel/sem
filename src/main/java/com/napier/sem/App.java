package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;


public class App
{
    public static void main(String[] args)
    {
        // Create new Application
        App a = new App();

        // Connect to database
        a.connect();

        Department dept = a.getDepartment("Sales");

        // Extract employee salary information
        ArrayList<Employee> employees = a.getSalariesByDepartment(dept);

        a.printSalaries(employees);

        // Disconnect from database
        a.disconnect();
    }

    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect()
    {
        try
        {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i)
        {
            System.out.println("Connecting to database...");
            try
            {
                // Wait a bit for db to start
                Thread.sleep(30000);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://db:3306/employees?useSSL=false", "root", "example");
                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie)
            {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                // Close connection
                con.close();
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }

    public Employee getEmployee(int ID)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT e.emp_no, e.first_name, e.last_name, t.title, s.salary, d.dept_name, m.emp_no AS manager "
                            + "FROM employees AS e "
                            + "LEFT JOIN titles AS t ON e.emp_no = t.emp_no "
                            + "LEFT JOIN salaries AS s ON e.emp_no = s.emp_no "
                            + "LEFT JOIN dept_emp AS de ON e.emp_no = de.emp_no "
                            + "LEFT JOIN departments AS d ON de.dept_no = d.dept_no "
                            + "LEFT JOIN dept_manager AS dm ON d.dept_no = dm.dept_no "
                            + "LEFT JOIN employees AS m ON dm.emp_no = m.emp_no "
                            + "WHERE e.emp_no = " + ID;

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            // Check one is returned
            if (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.title = rset.getString("title");
                emp.salary = rset.getInt("salary");
                //emp.dept = getDepartment(rset.getString("dept_name"));
                //emp.manager = getEmployee(rset.getString("manager");
                return emp;
            }
            else
                return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    public Employee getEmployee(String first_name, String last_name)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT e.emp_no, e.first_name, e.last_name, t.title, s.salary, d.dept_name, m.emp_no AS manager "
                            + "FROM employees AS e "
                            + "LEFT JOIN titles AS t ON e.emp_no = t.emp_no "
                            + "LEFT JOIN salaries AS s ON e.emp_no = s.emp_no "
                            + "LEFT JOIN dept_emp AS de ON e.emp_no = de.emp_no "
                            + "LEFT JOIN departments AS d ON de.dept_no = d.dept_no "
                            + "LEFT JOIN dept_manager AS dm ON d.dept_no = dm.dept_no "
                            + "LEFT JOIN employees AS m ON dm.emp_no = m.emp_no "
                            + "WHERE e.first_name = " + first_name
                            + " AND e.last_name = " + last_name;

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            // Check one is returned
            if (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.title = rset.getString("title");
                emp.salary = rset.getInt("salary");
                emp.dept = getDepartment(rset.getString("dept_name"));
                emp.manager = getEmployee(rset.getInt("emp_no"));
                return emp;
            }
            else
                return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    public void displayEmployee(Employee emp)
    {
        if (emp != null)
        {
            System.out.println(
                    emp.emp_no + " "
                            + emp.first_name + " "
                            + emp.last_name + "\n"
                            + emp.title + "\n"
                            + "Salary:" + emp.salary + "\n"
                            + emp.dept.dept_name + "\n"
                            + "Manager: " + emp.manager.first_name + " " + emp.manager.last_name + "\n");
        }
    }

    /**
     * Gets all the current employees and salaries.
     * @return A list of all employees and salaries, or null if there is an error.
     */
    public ArrayList<Employee> getAllSalaries()
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, salaries "
                            + "WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01' "
                            + "ORDER BY employees.emp_no ASC";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    /**
     * Prints a list of employees.
     * @param employees The list of employees to print.
     */
    public void printSalaries(ArrayList<Employee> employees)
    {
        // Check employees is not null
        if (employees == null)
        {
            System.out.println("No employees");
            return;
        }
        // Print header
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        // Loop over all employees in the list
        for (Employee emp : employees)
        {
            if (emp == null)
                continue;
            String emp_string =
                    String.format("%-10s %-15s %-20s %-8s",
                            emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(emp_string);
        }
    }

    /**
     * Gets all the current employees and salaries for a given role (title).
     * @return A list of all employees and salaries for a given role (title), or null if there is an error.
     */
    public ArrayList<Employee> getAllSalaries(String title)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, salaries, titles "
                            + "WHERE employees.emp_no = salaries.emp_no "
                            + "AND employees.emp_no = titles.emp_no "
                            + "AND salaries.to_date = '9999-01-01' "
                            + "AND titles.to_date = '9999-01-01' "
                            + "AND titles.title =  '" + title + "'"
                            + "ORDER BY employees.emp_no ASC";

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }
    public Department getDepartment(String dept_name)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT d.dept_no, d.dept_name, e.emp_no AS manager_no "
                            + "FROM departments AS d "
                            + "LEFT JOIN dept_manager AS m ON d.dept_no = m.dept_no "
                            + "LEFT JOIN employees AS e ON m.emp_no = e.emp_no "
                            + "WHERE d.dept_name = '" + dept_name + "'"
                            + "ORDER BY d.dept_no ASC";

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new department if valid.
            // Check one is returned
            if (rset.next())
            {
                Department dept = new Department();
                dept.dept_no = rset.getString("dept_no");
                dept.dept_name = rset.getString("dept_name");
                dept.manager = getEmployee(rset.getInt("manager_no"));
                return dept;
            }
            else
                return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get department details");
            return null;
        }

    }

    public ArrayList<Employee> getSalariesByDepartment(Department dept)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, salaries, dept_emp, departments "
                            + "WHERE employees.emp_no = salaries.emp_no "
                            + "AND employees.emp_no = dept_emp.emp_no "
                            + "AND dept_emp.dept_no = departments.dept_no "
                            + "AND salaries.to_date = '9999-01-01' "
                            + "AND departments.dept_no = '" + dept.dept_no + "' "
                            + "ORDER BY employees.emp_no ASC";

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }

    }
}