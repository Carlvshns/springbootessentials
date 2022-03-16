package br.com.devdojo.javaCliente;

import java.util.List;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;

import br.com.devdojo.handler.RestResponseHandler;
import br.com.devdojo.model.Student;

public class JavaClienteDAO {

    private RestTemplate restTemplate = new RestTemplateBuilder()
    .rootUri("http://localhost:8080/v1/protected/students").basicAuthentication("isa", "rockblin0123")
    .errorHandler(new RestResponseHandler()).build();
    private	RestTemplate restTemplateAdmin = new RestTemplateBuilder()
    .rootUri("http://localhost:8080/v1/admin/students").basicAuthentication("carl", "rockblin0123")
    .errorHandler(new RestResponseHandler()).build();

    public Student findById(long id){
        return restTemplate.getForObject("/{id}", Student.class, 1);
    }

    public List<Student>listAll(){
        ResponseEntity<List<Student>> exchange = restTemplate
		.exchange("/", HttpMethod.GET, null, new ParameterizedTypeReference<List<Student>>() {});
        return exchange.getBody();
    }
    public Student save(Student student){
        ResponseEntity<Student> exchangePost = restTemplateAdmin
		.exchange("/", HttpMethod.POST, new HttpEntity<>(student, createJSONHeader()), Student.class);
        return exchangePost.getBody();
    }

    public void update(Student student){
        restTemplateAdmin.put("/", student);
    }
    public void delete(long id){
        restTemplateAdmin.delete("/{id}", id);
    }
    
    private static HttpHeaders createJSONHeader(){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}
}
