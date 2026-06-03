import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterLink, RouterOutlet } from '@angular/router';
import { finalize, filter, startWith, timeout } from 'rxjs';

import { AuthService } from '../../../core/auth/auth.service';
import { TodoList } from '../../../core/models/todo-list.model';
import { TodoListService } from '../../../core/services/todo-list.service';
import { ErrorMessageComponent } from '../../../shared/components/error-message/error-message.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { ListFormComponent } from '../list-form/list-form.component';

@Component({
  selector: 'app-list-overview',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterOutlet,
    ListFormComponent,
    LoadingSpinnerComponent,
    ErrorMessageComponent,
    EmptyStateComponent
  ],
  templateUrl: './list-overview.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [
    `
      .list-page {
        padding: 1rem;
        display: grid;
        gap: 1rem;
      }

      .page-header {
        padding: 1rem;
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 1rem;
        flex-wrap: wrap;
      }

      .page-header__copy p {
        color: var(--color-muted);
      }

      .workspace {
        display: grid;
        gap: 1rem;
      }

      .sidebar,
      .content-panel {
        padding: 1rem;
      }

      .sidebar {
        display: grid;
        gap: 1rem;
        align-content: start;
      }

      .sidebar__header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 0.75rem;
      }

      .sidebar__form {
        display: grid;
        gap: 0.75rem;
        padding: 1rem;
        background: #f8fafc;
        border-radius: 16px;
      }

      .sidebar__form p {
        color: var(--color-muted);
      }

      .list-collection {
        display: flex;
        gap: 0.75rem;
        overflow-x: auto;
        padding-bottom: 0.25rem;
      }

      .list-card {
        min-width: 220px;
        border: 1px solid var(--color-border);
        border-radius: 16px;
        background: var(--color-surface);
        display: grid;
        gap: 0.75rem;
        padding: 0.9rem;
      }

      .list-card--active {
        border-color: var(--color-primary);
        box-shadow: inset 0 0 0 1px var(--color-primary);
      }

      .list-card__main {
        display: grid;
        gap: 0.3rem;
        text-align: left;
      }

      .list-card__main small {
        color: var(--color-muted);
      }

      .list-card__actions {
        display: flex;
        gap: 0.5rem;
        flex-wrap: wrap;
      }

      .content-panel {
        min-height: 320px;
      }

      .content-panel__placeholder {
        display: grid;
        gap: 1rem;
      }

      @media (min-width: 768px) {
        .workspace {
          grid-template-columns: minmax(260px, 280px) minmax(0, 1fr);
          align-items: start;
        }

        .sidebar {
          position: sticky;
          top: 1rem;
          max-height: calc(100vh - 2rem);
          overflow: auto;
        }

        .list-collection {
          display: grid;
          overflow: visible;
        }

        .list-card {
          min-width: 0;
        }
      }
    `
  ]
})
export class ListOverviewComponent implements OnInit {
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  private readonly todoListService = inject(TodoListService);
  private readonly cdr = inject(ChangeDetectorRef);

  lists: TodoList[] = [];
  selectedListId: string | null = null;
  isLoading = true;
  isSaving = false;
  errorMessage = '';
  showForm = false;
  editingList: TodoList | undefined;
  private listsLoadRequestId = 0;

  ngOnInit(): void {
    this.router.events
      .pipe(
        filter((event): event is NavigationEnd => event instanceof NavigationEnd),
        startWith(new NavigationEnd(0, this.router.url, this.router.url)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(() => this.updateSelectedListId());

    this.loadLists();
  }

  openCreateForm(): void {
    this.editingList = undefined;
    this.showForm = true;
  }

  openEditForm(list: TodoList): void {
    this.editingList = list;
    this.showForm = true;
  }

  cancelForm(): void {
    this.showForm = false;
    this.editingList = undefined;
  }

  saveList(name: string): void {
    if (this.isSaving) {
      return;
    }

    const request$ = this.editingList
      ? this.todoListService.updateList(this.editingList.id, name)
      : this.todoListService.createList(name);

    this.isSaving = true;
    request$
      .pipe(finalize(() => { this.isSaving = false; this.cdr.markForCheck(); }))
      .subscribe({
        next: (list) => {
          this.cancelForm();
          this.loadLists(list.id);
        },
        error: () => {
          this.errorMessage = 'Daten konnten nicht geladen werden';
          this.cdr.markForCheck();
        }
      });
  }

  deleteList(list: TodoList): void {
    if (!window.confirm(`Liste „${list.name}“ wirklich löschen?`) || this.isSaving) {
      return;
    }

    const wasSelected = this.selectedListId === list.id;
    const nextLists = this.lists.filter((item) => item.id !== list.id);
    const nextSelectedId = wasSelected ? nextLists[0]?.id ?? null : this.selectedListId;

    this.isSaving = true;
    this.todoListService
      .deleteList(list.id)
      .pipe(finalize(() => { this.isSaving = false; this.cdr.markForCheck(); }))
      .subscribe({
        next: () => {
          this.lists = nextLists;
          this.selectedListId = nextSelectedId;
          this.cdr.markForCheck();

          if (!this.lists.length) {
            void this.router.navigate(['/lists']);
            return;
          }

          if (wasSelected && nextSelectedId) {
            void this.router.navigate(['/lists', nextSelectedId]);
          }

          this.loadLists(nextSelectedId ?? undefined);
        },
        error: () => {
          this.errorMessage = 'Daten konnten nicht geladen werden';
          this.cdr.markForCheck();
        }
      });
  }

  logout(): void {
    this.authService.logout();
  }

  trackByListId(_: number, list: TodoList): string {
    return list.id;
  }

  private loadLists(preferredListId?: string): void {
    const requestId = ++this.listsLoadRequestId;
    this.isLoading = true;
    this.errorMessage = '';

    this.todoListService
      .getLists()
      .pipe(timeout(10000))
      .pipe(
        finalize(() => {
          if (requestId === this.listsLoadRequestId) {
            this.isLoading = false;
            this.cdr.markForCheck();
          }
        })
      )
      .subscribe({
        next: (lists) => {
          if (requestId !== this.listsLoadRequestId) {
            return;
          }
          this.lists = lists;
          this.syncSelection(preferredListId);
          this.cdr.markForCheck();
        },
        error: () => {
          if (requestId !== this.listsLoadRequestId) {
            return;
          }
          this.errorMessage = 'Daten konnten nicht geladen werden';
          this.cdr.markForCheck();
        }
      });
  }

  private syncSelection(preferredListId?: string): void {
    if (!this.lists.length) {
      if (this.router.url !== '/lists') {
        void this.router.navigate(['/lists']);
      }
      return;
    }

    const nextId =
      preferredListId && this.lists.some((list) => list.id === preferredListId)
        ? preferredListId
        : this.selectedListId && this.lists.some((list) => list.id === this.selectedListId)
          ? this.selectedListId
          : this.lists[0].id;

    if (nextId && nextId !== this.selectedListId) {
      void this.router.navigate(['/lists', nextId]);
    }
  }

  private updateSelectedListId(): void {
    const matches = this.router.url.match(/^\/lists\/([^/?#]+)/);
    this.selectedListId = matches?.[1] ?? null;
  }
}
