package br.com.devdojo;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.devdojo.model.Student;
import br.com.devdojo.javaCliente.JavaClienteDAO;


@SpringBootApplication
public class SpringBootEssentialsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootEssentialsApplication.class, args);

		Student studentPost = new Student();
		studentPost.setName("Poco M3");
		studentPost.setEmail("pocoM3@xiaomi.com");
		studentPost.setId(8L);
		JavaClienteDAO dao = new JavaClienteDAO();
		//System.out.println(dao.findById(1));
		//System.out.println(dao.save(studentPost));
		List<Student> student = dao.listAll();
		System.out.println(student);
		//dao.update(studentPost);
		//dao.delete(4L);

	}
}
