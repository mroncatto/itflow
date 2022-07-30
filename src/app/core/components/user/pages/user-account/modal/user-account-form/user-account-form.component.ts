import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, AfterContentChecked, OnInit, AfterViewInit, AfterContentInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { Subject, Subscription } from 'rxjs';
import { ToastPosition } from 'src/app/core/shared/commons/enum/toastPosition.enum';
import { IUser } from '../../../../model/user';
import { UserService } from '../../../../services/user.service';

@Component({
  selector: 'app-user-account-form',
  templateUrl: './user-account-form.component.html',
  styleUrls: ['./user-account-form.component.css']
})
export class UserAccountFormComponent implements OnInit {

  user!: IUser;
  userResult!: Subject<IUser>;
  userForm!: FormGroup;
  loading: boolean = false;

  private sub: Subscription[] = [];

  constructor(
    private modal: BsModalRef,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.userResult = new Subject();
    this.userForm = this.userService.getUserForm();
  }

  loadForm(user: IUser): void {
    this.user = user;
    if (user) {
      this.userForm.setValue({
        fullName: user.fullName,
        email: user.email,
        username: user.username,
        active: true,
        nonLocked: user.nonLocked,
      });
      this.userForm.controls['username'].disable();
      this.userForm.controls['active'].disable();
      this.userForm.controls['nonLocked'].disable();
    }
  }

  beforeSave(): void {
    if (this.userForm.valid) {
      this.loading = true;
      if (this.user && this.user.username) {
        this.sub.push(
          this.userService.updateUser(this.userForm.getRawValue()).subscribe({
            next: (data) => this.onUpdate(data),
            error: (err) => this.onFail(err)
          })
        )
      } else {
        this.sub.push(
          this.userService.createUser(this.userForm.value as IUser).subscribe({
            next: (data) => this.onSave(data),
            error: (err) => this.onFail(err)
          })
        )
      }
    } else {
      //this.userForm.markAsTouched(); FIXME: Works only on Angular 14
      this.userService.alertService.warning("Bad Request", "All fiedls are required!", ToastPosition.TOP_RIGHT);
    }
  }

  onSave(user: IUser): void {
    this.userResult.next(user);
    this.userService.onSuccess("created", "userCreated");
    this.closeModal();
  }

  onUpdate(user: IUser): void {
    this.userResult.next(user);
    this.userService.onSuccess("updated", "userUpdated");
    this.closeModal();
  }

  onFail(err: HttpErrorResponse): void {
    this.loading = false;
    this.userService.onFail(err)
  }

  closeModal() {
    this.modal.hide();
  }

}
