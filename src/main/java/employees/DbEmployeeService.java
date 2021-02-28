package employees;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbEmployeeService implements EmployeeService{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Employee> findAll() {
        return jdbcTemplate.query("select id, emp_name, address from employees",
                (resultSet, i) -> new Employee(resultSet.getLong("id"),
                        resultSet.getString("emp_name"),
                        resultSet.getString("address")));
    }

    @Override
    public void saveAll(List<Employee> employees) {
        for (Employee employee:employees) {
            if (employee.getId() == null) {
                jdbcTemplate.update("insert into employees(emp_name, address) values (?, ?)", employee.getName(), employee.getAddress());
            }
            else {
                jdbcTemplate.update("update employees set emp_name = ?, address = ? where id = ?", employee.getName(), employee.getAddress(), employee.getId());
            }
        }
    }

    @Override
    public void deleteById(long id) {
        jdbcTemplate.update("delete from employees where id=?", id);
    }
}
