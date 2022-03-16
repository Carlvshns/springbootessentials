package br.com.devdojo.springbootessentials;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.devdojo.model.Student;
import br.com.devdojo.repository.StudentRepository;

@ExtendWith(SpringExtension.class)
@DataJpaTest
//@AutoConfigureTestDatabase(replace = Replace.NONE)
public class StudentRepositoryTest {
    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void createShouldPersistData(){
        Student student = new Student("william", "william@devdojo.com.br");
        this.studentRepository.save(student);
        Assertions.assertNotNull(student.getId());
        Assertions.assertEquals("william", student.getName());
        Assertions.assertEquals("william@devdojo.com.br", student.getEmail());
    }

    @Test
    public void deleteShouldRemoveData(){
        Student student = new Student("william", "william@devdojo.com.br");
        this.studentRepository.save(student);
        studentRepository.delete(student);
        Assertions.assertTrue(studentRepository.findById(student.getId()).isEmpty());
    }

    @Test
    public void updateShouldChangeAndPersistData(){
        Student student = new Student("william", "william@devdojo.com.br");
        this.studentRepository.save(student);
        student.setName("william222");
        student.setEmail("william222@devdojo.com.br");
        student = this.studentRepository.save(student);
        Assertions.assertEquals("william222", student.getName());
        Assertions.assertEquals("william222@devdojo.com.br", student.getEmail());
    }

    @Test
    public void findByNameIgnoreCaseContainingShouldIgnoreCase(){
        Student student = new Student("William", "william@devdojo.com.br");
        Student student2 = new Student("william", "william222@devdojo.com.br");
        this.studentRepository.save(student);
        this.studentRepository.save(student2);
        List<Student> studentList = studentRepository.findByNameIgnoreCaseContaining("william");
        Assertions.assertEquals(2, studentList.size());
    }

    @Test
    public void createWhenNameIsEmptyShouldConstraintViolationException(){
        Exception exception = assertThrows(ConstraintViolationException.class,
        () -> studentRepository.save(new Student("", "email@gmail.com")));
        assertTrue(exception.getMessage().contains("O campo nome é obrigatorio"));
    }

    @Test
    public void createWhenEmailIsNullShouldConstraintViolationException(){
        Exception exception = assertThrows(ConstraintViolationException.class,
        () -> studentRepository.save(new Student("Carlos", "")));
        assertTrue(exception.getMessage().contains("O campo email é obrigatorio"));
    }

    @Test
    public void createWhenEmailIsNotValidShouldConstraintViolationException(){
    Exception exception = assertThrows(ConstraintViolationException.class,
    () -> studentRepository.save(new Student("Carlos", "wrongemail.email")));
    assertTrue(exception.getMessage().contains("O email deve ser valido"));
    }
}
