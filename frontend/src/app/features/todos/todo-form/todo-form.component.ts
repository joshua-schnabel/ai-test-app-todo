import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { CreateTodoRequest, Todo, TodoPriority, UpdateTodoRequest } from '../../../core/models/todo.model';

@Component({
  selector: 'app-todo-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './todo-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [
    `
      .todo-form {
        display: grid;
        gap: 1rem;
      }

      .todo-form__grid {
        display: grid;
        gap: 1rem;
      }

      .todo-form__actions {
        display: flex;
        flex-wrap: wrap;
        gap: 0.75rem;
      }

      @media (min-width: 640px) {
        .todo-form__grid {
          grid-template-columns: repeat(2, minmax(0, 1fr));
        }

        .todo-form__grid .form-field:first-child,
        .todo-form__grid .form-field:nth-child(2) {
          grid-column: 1 / -1;
        }
      }
    `
  ]
})
export class TodoFormComponent implements OnChanges {
  private readonly fb = inject(FormBuilder);

  @Input() existingTodo?: Todo;
  @Output() save = new EventEmitter<CreateTodoRequest | UpdateTodoRequest>();
  @Output() cancel = new EventEmitter<void>();

  readonly priorities: TodoPriority[] = ['LOW', 'MEDIUM', 'HIGH'];

  readonly form = this.fb.nonNullable.group({
    title: ['', [Validators.required, Validators.maxLength(120)]],
    description: ['', [Validators.maxLength(1000)]],
    priority: ['MEDIUM' as TodoPriority, [Validators.required]],
    dueDate: ['']
  });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['existingTodo']) {
      this.form.reset({
        title: this.existingTodo?.title ?? '',
        description: this.existingTodo?.description ?? '',
        priority: this.existingTodo?.priority ?? 'MEDIUM',
        dueDate: this.existingTodo?.dueDate ?? ''
      });
    }
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    this.save.emit({
      title: value.title.trim(),
      description: value.description.trim() || undefined,
      priority: value.priority,
      dueDate: value.dueDate || undefined
    });
  }
}
