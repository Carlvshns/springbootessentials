package br.com.devdojo.springbootessentials;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.com.devdojo.model.Student;
import br.com.devdojo.repository.StudentRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class StudentEndpointTokenTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;
    @MockBean
    private StudentRepository studentRepository;
    @Autowired
    private MockMvc mockMvc;
    
    private HttpEntity<Void> protectedHeader;
    private HttpEntity<Void> adminHeader;
    private HttpEntity<Void> wrongHeader;

    @BeforeEach
    public void configProtectedHeaders(){
        String str = "{\"username\":\"isa\",\"password\":\"rockblin0123\"}";
        HttpHeaders headers = restTemplate.postForEntity("/login", str, String.class).getHeaders();
        this.protectedHeader = new HttpEntity<>(headers);
    }

    @BeforeEach
    public void configAdminHeaders(){
        String str = "{\"username\":\"carl\",\"password\":\"rockblin0123\"}";
        HttpHeaders headers = restTemplate.postForEntity("/login", str, String.class).getHeaders();
        this.adminHeader = new HttpEntity<>(headers);
    }

    @BeforeEach
    public void configWrongHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "111111");
        this.wrongHeader = new HttpEntity<>(headers);
    }

    @BeforeEach
    public void setup(){
       Student student = new Student(1L, "Legolas", "legolas@lotr.com");
       BDDMockito.when(studentRepository.findById(1L)).thenReturn(java.util.Optional.of(student));
    }

    @Test
    public void listStudentsWhenTokenIsIncorrectShouldReturnStatusCode403(){
        ResponseEntity<String> response = restTemplate.exchange("/v1/protected/students/", HttpMethod.GET, wrongHeader, String.class);
        Assertions.assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    public void getStudentsByIdWhenTokenIsIncorrectShouldReturnStatusCode401(){
        
        ResponseEntity<String> response = restTemplate.exchange("/v1/protected/students/1", HttpMethod.GET, wrongHeader, String.class);
        Assertions.assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    public void listStudentsWhenTokenIsCorrectShouldReturnStatusCode200(){
        ResponseEntity<String> response = restTemplate.exchange("/v1/protected/students/1", HttpMethod.GET, protectedHeader, String.class);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void getStudentsByIdWhenTokenIsCorrectShouldReturnStatusCode200(){
        ResponseEntity<Student> response = restTemplate.exchange("/v1/protected/students/1", HttpMethod.GET, protectedHeader, Student.class);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void getStudentsByIdTokenIsCorrectANdStudentDoesNotExistsShouldReturnStatusCode404(){
        ResponseEntity<Student> response = restTemplate.getForEntity("/v1/protected/students/1", Student.class);
        Assertions.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void deleteWhenUserHasRoleAdminAndStudentExistsShouldReturnStatusCode200(){
        BDDMockito.doNothing().when(studentRepository).deleteById(1L);
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/admin/students/1", HttpMethod.DELETE, adminHeader, String.class, 1L);
        Assertions.assertEquals(200, exchange.getStatusCodeValue());
    }

    @Test
    public void deleteWhenUserHasRoleAdminAndStudentDoesNotExistsShouldReturnStatusCode404() throws Exception{
        String token = adminHeader.getHeaders().get("Authorization").get(0);
        BDDMockito.doNothing().when(studentRepository).deleteById(1L);
        mockMvc.perform((RequestBuilder) ((ResultActions) MockMvcRequestBuilders.delete("/v1/admin/students/{id}", -1)
        .header("Authorization", token))
        .andExpect(MockMvcResultMatchers.status().isNotFound()));
    }

    @Test
    public void deleteWhenDoesNotHaveRoleAdminShouldReturnStatusCode403() throws Exception{
        String token = protectedHeader.getHeaders().get("Authorization").get(0);
        BDDMockito.doNothing().when(studentRepository).deleteById(1L);
        mockMvc.perform((RequestBuilder) ((ResultActions) MockMvcRequestBuilders.delete("/v1/admin/students/{id}", 1)
        .header("Authorization", token))
        .andExpect(MockMvcResultMatchers.status().isForbidden()));
    }

    @Test
    public void createWhenNameIsNullShouldReturnStatusCode400BadRequest() throws Exception{
    Student student = new Student(3L, null, "sam@lotr.com");
    BDDMockito.when(studentRepository.save(student)).thenReturn(student);
    ResponseEntity<String> response = restTemplate.exchange("/v1/admin/students/", HttpMethod.POST,new HttpEntity<>(student, adminHeader.getHeaders()), String.class);
    Assertions.assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createShouldPersistDatareturnStatusCode201() throws Exception{
    Student student = new Student(3L, null, "sam@lotr.com");
    BDDMockito.when(studentRepository.save(student)).thenReturn(student);
    ResponseEntity<Student> response = restTemplate.exchange("/v1/admin/students/", HttpMethod.POST,new HttpEntity<>(student, adminHeader.getHeaders()), Student.class);
    Assertions.assertEquals(201, response.getStatusCodeValue());
    Assertions.assertFalse(response.getBody().getId().equals(null));
    }
}