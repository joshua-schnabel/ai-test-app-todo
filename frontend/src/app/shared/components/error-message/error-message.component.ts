import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-error-message',
  standalone: true,
  templateUrl: './error-message.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [
    `
      .error-banner {
        background: rgba(211, 47, 47, 0.12);
        color: var(--color-danger);
        border-left: 4px solid var(--color-danger);
        padding: 0.85rem 1rem;
        border-radius: 12px;
        font-weight: 600;
      }
    `
  ]
})
export class ErrorMessageComponent {
  @Input({ required: true }) message = '';
}
