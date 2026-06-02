import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

import { AuthService } from '../../../core/auth/auth.service';
import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  const authServiceMock = {
    login: vi.fn(),
    register: vi.fn(),
    logout: vi.fn(),
    getToken: vi.fn(),
    isLoggedIn: vi.fn(),
    getCurrentUser: vi.fn()
  };

  beforeEach(async () => {
    authServiceMock.register.mockReset();
    authServiceMock.register.mockReturnValue(of(void 0));

    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [provideRouter([]), { provide: AuthService, useValue: authServiceMock }]
    }).compileComponents();
  });

  it('validates the registration form', () => {
    const fixture = TestBed.createComponent(RegisterComponent);
    const component = fixture.componentInstance;

    component.submit();

    expect(component.form.controls.email.hasError('required')).toBe(true);
    expect(component.form.controls.password.hasError('required')).toBe(true);
  });

  it('shows a duplicate email error', () => {
    const fixture = TestBed.createComponent(RegisterComponent);
    const component = fixture.componentInstance;
    authServiceMock.register.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 409, statusText: 'Conflict' }))
    );

    component.form.setValue({
      email: 'existing@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });
    component.submit();
    fixture.detectChanges();

    expect(component.errorMessage).toBe('E-Mail-Adresse ist bereits registriert');
    expect(fixture.nativeElement.textContent).toContain('E-Mail-Adresse ist bereits registriert');
  });
});
