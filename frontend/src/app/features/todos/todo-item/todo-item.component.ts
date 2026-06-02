import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

import { Todo } from '../../../core/models/todo.model';

@Component({
  selector: 'app-todo-item',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './todo-item.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [
    `
      .todo-item {
        display: grid;
        gap: 1rem;
        padding: 1rem;
        border: 1px solid var(--color-border);
      }

      .todo-item--done {
        opacity: 0.72;
      }

      .todo-item--overdue {
        border-color: rgba(211, 47, 47, 0.45);
        background: rgba(211, 47, 47, 0.04);
      }

      .todo-item__header {
        display: flex;
        gap: 0.75rem;
        align-items: flex-start;
      }

      .todo-item__title {
        display: grid;
        gap: 0.4rem;
        flex: 1;
      }

      .todo-item__title h3 {
        font-size: 1rem;
      }

      .todo-item__title h3.done {
        text-decoration: line-through;
      }

      .todo-item__description {
        color: var(--color-muted);
      }

      .todo-item__meta {
        display: flex;
        flex-wrap: wrap;
        gap: 0.5rem;
      }

      .badge {
        padding: 0.25rem 0.6rem;
        border-radius: 999px;
        font-size: 0.8rem;
        font-weight: 700;
      }

      .badge--priority-low {
        background: rgba(56, 142, 60, 0.12);
        color: var(--color-success);
      }

      .badge--priority-medium {
        background: rgba(25, 118, 210, 0.12);
        color: var(--color-primary);
      }

      .badge--priority-high,
      .badge--overdue {
        background: rgba(211, 47, 47, 0.12);
        color: var(--color-danger);
      }

      .todo-item__actions {
        display: flex;
        flex-wrap: wrap;
        gap: 0.5rem;
      }

      .todo-item__toggle {
        width: 24px;
        height: 24px;
        margin-top: 0.1rem;
      }
    `
  ]
})
export class TodoItemComponent {
  @Input({ required: true }) todo!: Todo;
  @Output() toggleStatus = new EventEmitter<Todo>();
  @Output() editTodo = new EventEmitter<Todo>();
  @Output() deleteTodo = new EventEmitter<Todo>();

  get isOverdue(): boolean {
    return !!this.todo.dueDate && this.todo.status === 'OPEN' && this.todo.dueDate < new Date().toISOString().slice(0, 10);
  }

  get dueDateLabel(): string | null {
    if (!this.todo.dueDate) {
      return null;
    }

    return new Intl.DateTimeFormat('de-DE').format(new Date(this.todo.dueDate));
  }

  get priorityClass(): string {
    return `badge--priority-${this.todo.priority.toLowerCase()}`;
  }
}
