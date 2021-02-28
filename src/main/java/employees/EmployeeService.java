package employees;

import java.util.List;

public interface EmployeeService {

    List<Employee> findAll();

    void saveAll(List<Employee> employees);

    void deleteById(long id);

}
