import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/auth/auth.service';
import { ErrorMessageComponent } from '../../../shared/components/error-message/error-message.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, ErrorMessageComponent],
  templateUrl: './login.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [
    `
      .auth-page {
        min-height: 100vh;
        display: grid;
        place-items: center;
        padding: 1rem;
      }

      .auth-card {
        width: min(100%, 420px);
        padding: 1.5rem;
        display: grid;
        gap: 1rem;
      }

      .auth-card__header {
        display: grid;
        gap: 0.35rem;
      }

      .auth-card__subtitle {
        color: var(--color-muted);
      }

      .auth-form {
        display: grid;
        gap: 1rem;
      }

      .auth-footer {
        text-align: center;
        color: var(--color-muted);
      }
    `
  ]
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  isSubmitting = false;
  errorMessage = '';

  submit(): void {
    if (this.form.invalid || this.isSubmitting) {
      this.form.markAllAsTouched();
      return;
    }

    const { email, password } = this.form.getRawValue();
    this.isSubmitting = true;
    this.errorMessage = '';

    this.authService.login(email, password).subscribe({
      next: () => {
        this.isSubmitting = false;
        void this.router.navigate(['/lists']);
      },
      error: () => {
        this.isSubmitting = false;
        this.errorMessage = 'E-Mail-Adresse oder Passwort ist falsch';
      }
    });
  }
}
