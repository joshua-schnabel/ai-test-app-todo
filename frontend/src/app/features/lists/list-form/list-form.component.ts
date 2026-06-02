import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { TodoList } from '../../../core/models/todo-list.model';

@Component({
  selector: 'app-list-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './list-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [
    `
      .list-form {
        display: grid;
        gap: 1rem;
      }

      .list-form__actions {
        display: flex;
        flex-wrap: wrap;
        gap: 0.75rem;
      }
    `
  ]
})
export class ListFormComponent implements OnChanges {
  private readonly fb = inject(FormBuilder);

  @Input() existingList?: TodoList;
  @Output() submitName = new EventEmitter<string>();
  @Output() cancel = new EventEmitter<void>();

  readonly form = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(80)]]
  });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['existingList']) {
      this.form.reset({ name: this.existingList?.name ?? '' });
    }
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitName.emit(this.form.getRawValue().name.trim());
  }
}
