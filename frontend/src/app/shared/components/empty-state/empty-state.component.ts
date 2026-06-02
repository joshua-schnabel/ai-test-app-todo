import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  templateUrl: './empty-state.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [
    `
      :host {
        display: block;
      }

      .empty-state {
        display: grid;
        gap: 0.85rem;
        place-items: center;
        text-align: center;
        padding: 2rem 1.5rem;
        color: var(--color-muted);
        border: 1px dashed var(--color-border);
      }

      .empty-state__icon {
        width: 56px;
        height: 56px;
        border-radius: 50%;
        display: grid;
        place-items: center;
        background: rgba(25, 118, 210, 0.1);
        color: var(--color-primary);
        font-size: 1.6rem;
      }
    `
  ]
})
export class EmptyStateComponent {
  @Input({ required: true }) message = '';
}
