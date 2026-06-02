export type TodoStatus = 'OPEN' | 'DONE';
export type TodoPriority = 'LOW' | 'MEDIUM' | 'HIGH';

export interface Todo {
  id: string;
  listId: string;
  title: string;
  description?: string;
  status: TodoStatus;
  priority: TodoPriority;
  dueDate?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateTodoRequest {
  title: string;
  description?: string;
  priority: TodoPriority;
  dueDate?: string;
}

export interface UpdateTodoRequest {
  title: string;
  description?: string;
  priority: TodoPriority;
  dueDate?: string;
}

export interface TodoFilter {
  status?: 'open' | 'done' | 'today' | 'overdue';
}

export interface TodoSort {
  sortBy?: 'dueDate' | 'priority' | 'createdAt' | 'status';
  sortDir?: 'asc' | 'desc';
}
