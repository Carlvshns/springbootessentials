package br.com.devdojo.springbootessentials;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.com.devdojo.model.Student;
import br.com.devdojo.repository.StudentRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class StudentEndpointTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;
    @MockBean
    private StudentRepository studentRepository;
    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class Config{
        @Bean
        public RestTemplateBuilder restTemplateBuilder(){
            return new RestTemplateBuilder().basicAuthentication("carl", "rockblin0123");
        }
    }
    @BeforeEach
    public void setup(){
        Student student = new Student(1L, "Legolas", "legolas@lotr.com");
        BDDMockito.when(studentRepository.findById(1L)).thenReturn(java.util.Optional.of(student));

    }

    @Test
    public void listStudentsWhenUsernameAndPasswordAreIncorrectShouldReturnStatusCode401(){
        restTemplate = restTemplate.withBasicAuth("1", "1");
        ResponseEntity<String> response = restTemplate.getForEntity("/v1/protected/students/", String.class);
        Assertions.assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    public void getStudentsByIdWhenUsernameAndPasswordAreIncorrectShouldReturnStatusCode401(){
        restTemplate = restTemplate.withBasicAuth("1", "1");
        ResponseEntity<String> response = restTemplate.getForEntity("/v1/protected/students/1", String.class);
        Assertions.assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    public void listStudentsWhenUsernameAndPasswordAreCorrectShouldReturnStatusCode200(){
        List<Student> students = Arrays.asList(new Student(1L, "Legolas", "legolas@lotr.com"),
        new Student(1L, "Aragorn", "aragorn@lotr.com"));
        BDDMockito.when(studentRepository.findAll()).thenReturn(students);
        ResponseEntity<String> response = restTemplate.getForEntity("/v1/protected/students/", String.class);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void getStudentsByIdWhenUsernameAndPasswordAreCorrectShouldReturnStatusCode200(){
        ResponseEntity<Student> response = restTemplate.getForEntity("/v1/protected/students/{id}", Student.class, 1L);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void getStudentsByIdWhenUsernameAndPasswordAreCorrectANdStudentDoesNotExistsShouldReturnStatusCode404(){
        ResponseEntity<Student> response = restTemplate.getForEntity("/v1/protected/students/{id}", Student.class, -1);
        Assertions.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void deleteWhenUserHasRoleAdminAndStudentExistsShouldReturnStatusCode200(){
        BDDMockito.doNothing().when(studentRepository).deleteById(1L);
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/admin/students/{id}", HttpMethod.DELETE, null, String.class, 1L);
        Assertions.assertEquals(200, exchange.getStatusCodeValue());
    }

    @Test
    @WithMockUser(username = "carl", password = "rockblin0123", roles = {"USER", "ADMIN"})
    public void deleteWhenUserHasRoleAdminAndStudentDoesNotExistsShouldReturnStatusCode404() throws Exception{
        BDDMockito.doNothing().when(studentRepository).deleteById(1L);
        //ResponseEntity<String> exchange = restTemplate.exchange("/v1/admin/students/{id}", HttpMethod.DELETE, null, String.class, -1L);
        //Assertions.assertEquals(404, exchange.getStatusCodeValue());
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/admin/students/{id}", -1))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "carl", password = "rockblin0123", roles = {"USER"})
    public void deleteWhenDoesNotHaveRoleAdminShouldReturnStatusCode403() throws Exception{
        BDDMockito.doNothing().when(studentRepository).deleteById(1L);
        //ResponseEntity<String> exchange = restTemplate.exchange("/v1/admin/students/{id}", HttpMethod.DELETE, null, String.class, -1L);
        //Assertions.assertEquals(404, exchange.getStatusCodeValue());
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/admin/students/{id}", 1))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void createWhenNameIsNullShouldReturnStatusCode400BadRequest() throws Exception{
    Student student = new Student(3L, null, "sam@lotr.com");
    BDDMockito.when(studentRepository.save(student)).thenReturn(student);
    ResponseEntity<String> response =  restTemplate.postForEntity("/v1/admin/students/", student, String.class);
    Assertions.assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createShouldPersistDatareturnStatusCode201() throws Exception{
    Student student = new Student(3L, null, "sam@lotr.com");
    BDDMockito.when(studentRepository.save(student)).thenReturn(student);
    ResponseEntity<Student> response =  restTemplate.postForEntity("/v1/admin/students/", student, Student.class);
    Assertions.assertEquals(201, response.getStatusCodeValue());
    Assertions.assertFalse(response.getBody().getId().equals(null));
    }
}