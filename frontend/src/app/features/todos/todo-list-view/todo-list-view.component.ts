import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs';

import { TodoList } from '../../../core/models/todo-list.model';
import { CreateTodoRequest, Todo, TodoFilter, TodoSort, UpdateTodoRequest } from '../../../core/models/todo.model';
import { TodoListService } from '../../../core/services/todo-list.service';
import { TodoService } from '../../../core/services/todo.service';
import { ErrorMessageComponent } from '../../../shared/components/error-message/error-message.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { TodoFilterComponent } from '../todo-filter/todo-filter.component';
import { TodoFormComponent } from '../todo-form/todo-form.component';
import { TodoItemComponent } from '../todo-item/todo-item.component';
import { TodoSortComponent } from '../todo-sort/todo-sort.component';

@Component({
  selector: 'app-todo-list-view',
  standalone: true,
  imports: [
    CommonModule,
    TodoFilterComponent,
    TodoSortComponent,
    TodoFormComponent,
    TodoItemComponent,
    LoadingSpinnerComponent,
    ErrorMessageComponent,
    EmptyStateComponent
  ],
  templateUrl: './todo-list-view.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [
    `
      .todo-view {
        display: grid;
        gap: 1rem;
      }

      .todo-view__header {
        display: flex;
        justify-content: space-between;
        gap: 1rem;
        flex-wrap: wrap;
        align-items: center;
      }

      .todo-view__header p {
        color: var(--color-muted);
      }

      .todo-view__controls {
        display: grid;
        gap: 1rem;
      }

      .todo-view__form {
        padding: 1rem;
        display: grid;
        gap: 0.75rem;
      }

      .todo-view__list {
        display: grid;
        gap: 0.75rem;
      }

      @media (min-width: 768px) {
        .todo-view__controls {
          grid-template-columns: minmax(0, 1fr) 220px;
          align-items: end;
        }
      }
    `
  ]
})
export class TodoListViewComponent implements OnInit {
  private readonly destroyRef = inject(DestroyRef);
  private readonly route = inject(ActivatedRoute);
  private readonly todoService = inject(TodoService);
  private readonly todoListService = inject(TodoListService);

  listId = '';
  listName = 'Aufgaben';
  todos: Todo[] = [];
  isLoading = true;
  errorMessage = '';
  showForm = false;
  editingTodo: Todo | undefined;
  currentFilter: TodoFilter = {};
  currentSort: TodoSort = { sortBy: 'createdAt', sortDir: 'desc' };

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params) => {
      const nextListId = params.get('listId');

      if (!nextListId) {
        return;
      }

      this.listId = nextListId;
      this.showForm = false;
      this.editingTodo = undefined;
      this.loadListName();
      this.loadTodos();
    });
  }

  updateFilter(filter: TodoFilter): void {
    this.currentFilter = filter;
    this.loadTodos();
  }

  updateSort(sort: TodoSort): void {
    this.currentSort = sort;
    this.loadTodos();
  }

  startCreate(): void {
    this.editingTodo = undefined;
    this.showForm = true;
  }

  startEdit(todo: Todo): void {
    this.editingTodo = todo;
    this.showForm = true;
  }

  cancelEdit(): void {
    this.editingTodo = undefined;
    this.showForm = false;
  }

  saveTodo(request: CreateTodoRequest | UpdateTodoRequest): void {
    const request$ = this.editingTodo
      ? this.todoService.updateTodo(this.listId, this.editingTodo.id, request)
      : this.todoService.createTodo(this.listId, request as CreateTodoRequest);

    request$.subscribe({
      next: () => {
        this.cancelEdit();
        this.loadTodos();
      },
      error: () => {
        this.errorMessage = 'Daten konnten nicht geladen werden';
      }
    });
  }

  toggleTodo(todo: Todo): void {
    const request$ = todo.status === 'DONE'
      ? this.todoService.reopenTodo(this.listId, todo.id)
      : this.todoService.completeTodo(this.listId, todo.id);

    request$.subscribe({
      next: () => this.loadTodos(),
      error: () => {
        this.errorMessage = 'Daten konnten nicht geladen werden';
      }
    });
  }

  deleteTodo(todo: Todo): void {
    if (!window.confirm(`Aufgabe „${todo.title}“ wirklich löschen?`)) {
      return;
    }

    this.todoService.deleteTodo(this.listId, todo.id).subscribe({
      next: () => this.loadTodos(),
      error: () => {
        this.errorMessage = 'Daten konnten nicht geladen werden';
      }
    });
  }

  hasActiveFilter(): boolean {
    return !!this.currentFilter.status;
  }

  trackByTodoId(_: number, todo: Todo): string {
    return todo.id;
  }

  private loadTodos(): void {
    if (!this.listId) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.todoService
      .getTodos(this.listId, this.currentFilter, this.currentSort)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (todos) => {
          this.todos = todos;
        },
        error: () => {
          this.errorMessage = 'Daten konnten nicht geladen werden';
        }
      });
  }

  private loadListName(): void {
    this.todoListService.getLists().subscribe({
      next: (lists: TodoList[]) => {
        this.listName = lists.find((list) => list.id === this.listId)?.name ?? 'Aufgaben';
      }
    });
  }
}
