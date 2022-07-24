import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthenticationGuard } from 'src/app/core/guards/authentication.guard';
import { DashboardComponent } from '../../pages/dashboard/dashboard.component';

const routes: Routes = [
  {
    path: '', canActivate: [AuthenticationGuard], children: [
      { path: 'dashboard', component: DashboardComponent }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule { }
