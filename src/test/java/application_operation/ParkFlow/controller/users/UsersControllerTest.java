package application_operation.ParkFlow.controller.users;

import application_operation.ParkFlow.ParkFlowApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest(classes = ParkFlowApplication.class)
public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;
}
