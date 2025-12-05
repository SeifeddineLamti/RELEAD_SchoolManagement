package relead.relead_schoolmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "relead.relead_schoolmanagement.entities")
public class ReleadSchoolManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReleadSchoolManagementApplication.class, args);
    }

}
