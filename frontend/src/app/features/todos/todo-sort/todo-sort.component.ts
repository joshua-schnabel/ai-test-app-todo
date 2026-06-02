import { ChangeDetectionStrategy, Component, EventEmitter, Output } from '@angular/core';

import { TodoSort } from '../../../core/models/todo.model';

@Component({
  selector: 'app-todo-sort',
  standalone: true,
  templateUrl: './todo-sort.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [
    `
      .sort-select {
        min-height: 44px;
      }
    `
  ]
})
export class TodoSortComponent {
  @Output() sortChange = new EventEmitter<TodoSort>();

  selectedValue = 'createdAt-desc';

  onSortChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.selectedValue = value;

    const [sortBy, sortDir] = value.split('-') as [TodoSort['sortBy'], TodoSort['sortDir']];
    this.sortChange.emit({ sortBy, sortDir });
  }
}
