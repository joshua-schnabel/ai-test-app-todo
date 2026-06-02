package de.joshuaschnabel.todo.infrastruktur.config;

import de.joshuaschnabel.todo.application.port.out.DeleteTodoListPort;
import de.joshuaschnabel.todo.application.port.out.DeleteTodoPort;
import de.joshuaschnabel.todo.application.port.out.LoadTodoListPort;
import de.joshuaschnabel.todo.application.port.out.LoadTodoPort;
import de.joshuaschnabel.todo.application.port.out.LoadUserPort;
import de.joshuaschnabel.todo.application.port.out.PasswordHashingPort;
import de.joshuaschnabel.todo.application.port.out.SaveTodoListPort;
import de.joshuaschnabel.todo.application.port.out.SaveTodoPort;
import de.joshuaschnabel.todo.application.port.out.SaveUserPort;
import de.joshuaschnabel.todo.application.port.out.TokenServicePort;
import de.joshuaschnabel.todo.application.service.TodoApplicationService;
import de.joshuaschnabel.todo.application.service.TodoListApplicationService;
import de.joshuaschnabel.todo.application.service.UserApplicationService;
import de.joshuaschnabel.todo.application.usecase.CreateTodoListUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBeanConfig {

    @Bean
    public TodoListApplicationService todoListApplicationService(LoadTodoListPort loadTodoListPort,
                                                                 SaveTodoListPort saveTodoListPort,
                                                                 DeleteTodoListPort deleteTodoListPort) {
        return new TodoListApplicationService(loadTodoListPort, saveTodoListPort, deleteTodoListPort);
    }

    @Bean
    public TodoApplicationService todoApplicationService(LoadTodoListPort loadTodoListPort,
                                                         LoadTodoPort loadTodoPort,
                                                         SaveTodoPort saveTodoPort,
                                                         DeleteTodoPort deleteTodoPort) {
        return new TodoApplicationService(loadTodoListPort, loadTodoPort, saveTodoPort, deleteTodoPort);
    }

    @Bean
    public UserApplicationService userApplicationService(LoadUserPort loadUserPort,
                                                         SaveUserPort saveUserPort,
                                                         PasswordHashingPort passwordHashingPort,
                                                         TokenServicePort tokenServicePort,
                                                         CreateTodoListUseCase createTodoListUseCase) {
        return new UserApplicationService(
                loadUserPort,
                saveUserPort,
                passwordHashingPort,
                tokenServicePort,
                createTodoListUseCase
        );
    }
}
