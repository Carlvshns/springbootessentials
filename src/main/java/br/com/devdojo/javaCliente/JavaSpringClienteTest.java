package br.com.devdojo.javaCliente;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import br.com.devdojo.model.PageableResponse;
import br.com.devdojo.model.Student;

public class JavaSpringClienteTest {
    public static void main(String [] args){

//class JavaSpringClienteTest
		RestTemplate restTemplate = new RestTemplateBuilder()
        .rootUri("http://localhost:8080/v1/protected/students").basicAuthentication("isa", "rockblin0123").build();

		RestTemplate restTemplateAdmin = new RestTemplateBuilder()
        .rootUri("http://localhost:8080/v1/admin/students").basicAuthentication("carl", "rockblin0123").build();
        
		Student student = restTemplate.getForObject("/{id}", Student.class, 1);
        System.out.println(student);

		ResponseEntity<Student> forEntity = restTemplate.getForEntity("/{id}", Student.class, 2);
		System.out.println("forEntity: "+forEntity.getBody());

		/*Student[] students = restTemplate.getForObject("/", Student[].class);
		System.out.print("Arrays: "+Arrays.toString(students));
		
		ResponseEntity<List<Student>> exchange = restTemplate
		.exchange("/", HttpMethod.GET, null, new ParameterizedTypeReference<List<Student>>() {});
		System.out.println("ResponseEntity exchange: "+exchange.getBody());*/

		ResponseEntity<PageableResponse<Student>> exchange = restTemplate
		.exchange("/?sort=id,desc&sort=name,asc", HttpMethod.GET, null, new ParameterizedTypeReference<PageableResponse<Student>>() {});
		System.out.println("exchange 2: "+exchange);

		Student studentPost = new Student();
		studentPost.setName("Neo");
		studentPost.setEmail("matrix@pencil.com");
		ResponseEntity<Student> exchangePost = restTemplateAdmin
		.exchange("/", HttpMethod.POST, new HttpEntity<>(studentPost, createJSONHeader()), Student.class);
		System.out.println(exchangePost);

		Student studentPostForObject = restTemplateAdmin.postForObject("/", studentPost, Student.class);
		System.out.println(studentPostForObject);

		ResponseEntity<Student> studentPostForEntity = restTemplateAdmin.postForEntity("/", studentPost, Student.class);
		System.out.println(studentPostForEntity);

	}
	private static HttpHeaders createJSONHeader(){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}
}