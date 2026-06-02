import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { ListOverviewComponent } from './features/lists/list-overview/list-overview.component';
import { TodoListViewComponent } from './features/todos/todo-list-view/todo-list-view.component';

export const routes: Routes = [
  { path: '', redirectTo: '/lists', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'lists',
    canActivate: [authGuard],
    component: ListOverviewComponent,
    children: [{ path: ':listId', component: TodoListViewComponent }]
  },
  { path: '**', redirectTo: '/lists' }
];
