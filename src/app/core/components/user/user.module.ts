import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfileComponent } from './pages/profile/profile.component';
import { UserAccountComponent } from './pages/user-account/user-account.component';
import { UserRoutingModule } from './router/user-routing.module';
import { SharedModule } from '../../shared/shared.module';
import { TranslateModule } from '@ngx-translate/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ToastrModule } from 'ngx-toastr';


@NgModule({
  declarations: [
    ProfileComponent,
    UserAccountComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    UserRoutingModule,
    TranslateModule,
    SharedModule
  ]
})
export class UserModule { }
