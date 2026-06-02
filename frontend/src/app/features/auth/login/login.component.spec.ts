import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

import { AuthService } from '../../../core/auth/auth.service';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  const authServiceMock = {
    login: vi.fn(),
    register: vi.fn(),
    logout: vi.fn(),
    getToken: vi.fn(),
    isLoggedIn: vi.fn(),
    getCurrentUser: vi.fn()
  };

  beforeEach(async () => {
    authServiceMock.login.mockReset();
    authServiceMock.login.mockReturnValue(of(void 0));

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [provideRouter([]), { provide: AuthService, useValue: authServiceMock }]
    }).compileComponents();
  });

  it('validates empty form fields', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;

    component.submit();

    expect(component.form.controls.email.hasError('required')).toBe(true);
    expect(component.form.controls.password.hasError('required')).toBe(true);
  });

  it('validates email format', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;

    component.form.controls.email.setValue('ungueltig');
    component.form.controls.email.markAsTouched();

    expect(component.form.controls.email.hasError('email')).toBe(true);
  });

  it('validates password minimum length', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;

    component.form.controls.password.setValue('1234567');
    component.form.controls.password.markAsTouched();

    expect(component.form.controls.password.hasError('minlength')).toBe(true);
  });

  it('shows an error message on login failure', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;
    authServiceMock.login.mockReturnValue(throwError(() => new Error('invalid credentials')));

    component.form.setValue({ email: 'test@example.com', password: 'password123' });
    component.submit();
    fixture.detectChanges();

    expect(component.errorMessage).toBe('E-Mail-Adresse oder Passwort ist falsch');
    expect(fixture.nativeElement.textContent).toContain('E-Mail-Adresse oder Passwort ist falsch');
  });
});
