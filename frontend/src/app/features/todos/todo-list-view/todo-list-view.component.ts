import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { finalize, timeout } from 'rxjs';

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
  private readonly cdr = inject(ChangeDetectorRef);

  listId = '';
  listName = 'Aufgaben';
  todos: Todo[] = [];
  isLoading = true;
  isSaving = false;
  errorMessage = '';
  showForm = false;
  editingTodo: Todo | undefined;
  currentFilter: TodoFilter = {};
  currentSort: TodoSort = { sortBy: 'createdAt', sortDir: 'desc' };
  private todosLoadRequestId = 0;

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params) => {
      const nextListId = params.get('listId');

      if (!nextListId) {
        this.isLoading = false;
        this.cdr.markForCheck();
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
    if (this.isSaving) {
      return;
    }

    const request$ = this.editingTodo
      ? this.todoService.updateTodo(this.listId, this.editingTodo.id, request)
      : this.todoService.createTodo(this.listId, request as CreateTodoRequest);

    this.isSaving = true;
    request$
      .pipe(finalize(() => { this.isSaving = false; this.cdr.markForCheck(); }))
      .subscribe({
        next: () => {
          this.cancelEdit();
          this.loadTodos();
        },
        error: () => {
          this.errorMessage = 'Daten konnten nicht geladen werden';
          this.cdr.markForCheck();
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
        this.cdr.markForCheck();
      }
    });
  }

  deleteTodo(todo: Todo): void {
    if (!window.confirm(`Aufgabe „${todo.title}" wirklich löschen?`)) {
      return;
    }

    this.todoService.deleteTodo(this.listId, todo.id).subscribe({
      next: () => this.loadTodos(),
      error: () => {
        this.errorMessage = 'Daten konnten nicht geladen werden';
        this.cdr.markForCheck();
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

    const requestId = ++this.todosLoadRequestId;
    this.isLoading = true;
    this.errorMessage = '';

    this.todoService
      .getTodos(this.listId, this.currentFilter, this.currentSort)
      .pipe(timeout(10000))
      .pipe(
        finalize(() => {
          if (requestId === this.todosLoadRequestId) {
            this.isLoading = false;
            this.cdr.markForCheck();
          }
        })
      )
      .subscribe({
        next: (todos) => {
          if (requestId !== this.todosLoadRequestId) {
            return;
          }
          this.todos = todos;
          this.cdr.markForCheck();
        },
        error: () => {
          if (requestId !== this.todosLoadRequestId) {
            return;
          }
          this.errorMessage = 'Daten konnten nicht geladen werden';
          this.cdr.markForCheck();
        }
      });
  }

  private loadListName(): void {
    this.todoListService
      .getLists()
      .pipe(timeout(10000))
      .subscribe({
      next: (lists: TodoList[]) => {
        this.listName = lists.find((list) => list.id === this.listId)?.name ?? 'Aufgaben';
        this.cdr.markForCheck();
      },
      error: () => {
        this.listName = 'Aufgaben';
        this.cdr.markForCheck();
      }
    });
  }
}
