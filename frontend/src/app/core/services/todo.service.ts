import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  CreateTodoRequest,
  Todo,
  TodoFilter,
  TodoSort,
  UpdateTodoRequest
} from '../models/todo.model';

@Injectable({ providedIn: 'root' })
export class TodoService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/lists`;

  getTodos(listId: string, filter?: TodoFilter, sort?: TodoSort): Observable<Todo[]> {
    let params = new HttpParams();

    if (filter?.status) {
      params = params.set('status', filter.status);
    }

    if (sort?.sortBy) {
      params = params.set('sortBy', sort.sortBy);
    }

    if (sort?.sortDir) {
      params = params.set('sortDir', sort.sortDir);
    }

    return this.http.get<Todo[]>(`${this.baseUrl}/${listId}/todos`, { params });
  }

  getTodo(listId: string, todoId: string): Observable<Todo> {
    return this.http.get<Todo>(`${this.baseUrl}/${listId}/todos/${todoId}`);
  }

  createTodo(listId: string, req: CreateTodoRequest): Observable<Todo> {
    return this.http.post<Todo>(`${this.baseUrl}/${listId}/todos`, req);
  }

  updateTodo(listId: string, todoId: string, req: UpdateTodoRequest): Observable<Todo> {
    return this.http.put<Todo>(`${this.baseUrl}/${listId}/todos/${todoId}`, req);
  }

  deleteTodo(listId: string, todoId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${listId}/todos/${todoId}`);
  }

  completeTodo(listId: string, todoId: string): Observable<Todo> {
    return this.http.patch<Todo>(`${this.baseUrl}/${listId}/todos/${todoId}/complete`, {});
  }

  reopenTodo(listId: string, todoId: string): Observable<Todo> {
    return this.http.patch<Todo>(`${this.baseUrl}/${listId}/todos/${todoId}/reopen`, {});
  }
}
