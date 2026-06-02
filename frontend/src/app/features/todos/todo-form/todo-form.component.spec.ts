import { TestBed } from '@angular/core/testing';

import { TodoFormComponent } from './todo-form.component';

describe('TodoFormComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TodoFormComponent]
    }).compileComponents();
  });

  it('requires a title', () => {
    const fixture = TestBed.createComponent(TodoFormComponent);
    const component = fixture.componentInstance;

    component.submit();

    expect(component.form.controls.title.hasError('required')).toBe(true);
  });

  it('enforces the maximum title length', () => {
    const fixture = TestBed.createComponent(TodoFormComponent);
    const component = fixture.componentInstance;

    component.form.controls.title.setValue('a'.repeat(121));
    component.form.controls.title.markAsTouched();

    expect(component.form.controls.title.hasError('maxlength')).toBe(true);
  });

  it('enforces the maximum description length', () => {
    const fixture = TestBed.createComponent(TodoFormComponent);
    const component = fixture.componentInstance;

    component.form.controls.description.setValue('a'.repeat(1001));
    component.form.controls.description.markAsTouched();

    expect(component.form.controls.description.hasError('maxlength')).toBe(true);
  });
});
