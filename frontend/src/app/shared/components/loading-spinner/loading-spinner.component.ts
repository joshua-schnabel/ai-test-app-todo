import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  templateUrl: './loading-spinner.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [
    `
      .spinner-wrap {
        display: grid;
        place-items: center;
        gap: 0.75rem;
        padding: 2rem 1rem;
        color: var(--color-muted);
      }

      .spinner {
        width: 42px;
        height: 42px;
        border-radius: 50%;
        border: 4px solid rgba(25, 118, 210, 0.2);
        border-top-color: var(--color-primary);
        animation: spin 0.8s linear infinite;
      }

      @keyframes spin {
        to {
          transform: rotate(360deg);
        }
      }
    `
  ]
})
export class LoadingSpinnerComponent {}
