import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ValidationFieldComponent } from './components/forms/validation-field/validation-field.component';
import { MenuComponent } from './components/menu/menu/menu.component';
import { FormsModule } from '@angular/forms';
import { AppRoutingModule } from 'src/app/app-routing.module';
import { TranslateModule } from '@ngx-translate/core';
import { ConfirmComponent } from './components/modal/confirm/confirm.component';


@NgModule({
  declarations: [
    ValidationFieldComponent,
    MenuComponent,
    ConfirmComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    TranslateModule,
    AppRoutingModule,
    
  ],
  exports: [
    ValidationFieldComponent,
    MenuComponent,
    ConfirmComponent
  ]
})
export class SharedModule { }
