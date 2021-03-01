package employees;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/employees")
public class EmployeesController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/oldemployees")
    @ResponseBody
    public String listEmployees() {
        List<Employee> employees = jdbcTemplate.query("select id, emp_name, address from employees",
                (resultSet, i) -> new Employee(resultSet.getLong("id"), resultSet.getString("emp_name"), resultSet.getString("address")));

        return employees.stream().map(Employee::getName).collect(Collectors.joining(", "));
    }

    @GetMapping("/all")
    public String showAll(Model model) {
        model.addAttribute("employees", employeeService.findAll());
        return "allEmployees";
    }

    @GetMapping(value = "/create")
    public String showCreateForm(Model model) {

        int numberOfNewEmployees = 1;
        EmployeesCreationDto employeesForm = new EmployeesCreationDto();

        for (int i = 1; i <= numberOfNewEmployees; i++) {
            employeesForm.addEmployee(new Employee());
        }

        model.addAttribute("form", employeesForm);

        return "createEmployeesForm";
    }

    @PostMapping(value = "/save")
    public String saveBooks(@ModelAttribute EmployeesCreationDto form, Model model) {
        employeeService.saveAll(form.getEmployees());

        model.addAttribute("employees", employeeService.findAll());

        return "redirect:/employees/all";
    }

    @GetMapping(value = "/edit")
    public String showEditForm(Model model) {
        List<Employee> employees = new ArrayList<>();
        employeeService.findAll()
                .iterator()
                .forEachRemaining(employees::add);

        model.addAttribute("form", new EmployeesCreationDto(employees));

        return "editEmployeesForm";
    }

    @GetMapping(value = "/delete")
    public String deleteEmployee(@RequestParam(required = true) String id) {
        employeeService.deleteById(Long.parseLong(id));
        return "redirect:/employees/all";
    }


    private final String UPLOAD_DIR = "./uploads/";

    @GetMapping("/file")
    public String fileUpload() {
        return "fileUpload";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {

        // check if file is empty
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/employees/file";
        }

        // normalize the file path
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // save the file on the local file system
        try {
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // return success response
        attributes.addFlashAttribute("message", "You successfully uploaded " + fileName + '!');

        return "redirect:/employees/file";
    }

}
