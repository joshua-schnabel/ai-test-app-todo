import { ChangeDetectionStrategy, Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TodoFilter } from '../../../core/models/todo.model';

type FilterKey = 'all' | 'open' | 'done' | 'today' | 'overdue';

@Component({
  selector: 'app-todo-filter',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './todo-filter.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [
    `
      .filter-group {
        display: flex;
        gap: 0.5rem;
        overflow-x: auto;
        padding-bottom: 0.25rem;
      }

      .filter-button {
        min-height: 44px;
        padding: 0.7rem 1rem;
        border-radius: 999px;
        border: 1px solid var(--color-border);
        white-space: nowrap;
        background: var(--color-surface);
      }

      .filter-button--active {
        background: var(--color-primary);
        border-color: var(--color-primary);
        color: #ffffff;
      }
    `
  ]
})
export class TodoFilterComponent {
  @Output() filterChange = new EventEmitter<TodoFilter>();

  readonly filters: Array<{ key: FilterKey; label: string }> = [
    { key: 'all', label: 'Alle' },
    { key: 'open', label: 'Offen' },
    { key: 'done', label: 'Erledigt' },
    { key: 'today', label: 'Heute fällig' },
    { key: 'overdue', label: 'Überfällig' }
  ];

  selectedKey: FilterKey = 'all';

  applyFilter(key: FilterKey): void {
    this.selectedKey = key;
    this.filterChange.emit(key === 'all' ? {} : { status: key });
  }
}
