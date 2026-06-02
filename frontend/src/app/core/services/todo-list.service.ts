import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { TodoList } from '../models/todo-list.model';

@Injectable({ providedIn: 'root' })
export class TodoListService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/lists`;

  getLists(): Observable<TodoList[]> {
    return this.http.get<TodoList[]>(this.baseUrl);
  }

  createList(name: string): Observable<TodoList> {
    return this.http.post<TodoList>(this.baseUrl, { name });
  }

  updateList(id: string, name: string): Observable<TodoList> {
    return this.http.put<TodoList>(`${this.baseUrl}/${id}`, { name });
  }

  deleteList(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
