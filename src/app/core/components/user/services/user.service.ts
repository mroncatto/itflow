import { Injectable, Injector } from '@angular/core';
import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { Observable, Subject } from 'rxjs';
import { AbstractService } from 'src/app/core/shared/services/abstract/abstract.service';
import { IUser } from '../model/user';
import { UserAccountFormComponent } from '../pages/user-account/modal/user-account-form/user-account-form.component';
import { UserAccountShowComponent } from '../pages/user-account/modal/user-account-show/user-account-show.component';
import { UserValidation } from '../validation/user-validation';

@Injectable({
  providedIn: 'root'
})
export class UserService extends AbstractService {

  constructor(injector: Injector) { super(injector) }

  createUser(user: IUser): Observable<IUser> {
    return this.http.post<IUser>(`${this.API_URL}/user`, user);
  }

  updateUser(user: IUser): Observable<IUser> {
    return this.http.put<IUser>(`${this.API_URL}/user/${user.username}`, user);
  }

  getCurrentUser(): IUser | null {
    return this.authService.getUserFromSessionStorage();
  }

  getUserFromCache(): IUser {
    return this.authService.getUserFromSessionStorage() as IUser;
  }

  getUsers(): Observable<IUser[]> {
    return this.http.get<IUser[]>(`${this.API_URL}/user`);
  }

  resetPassword(form: FormData): Observable<any> {
    return this.http.post(`${this.API_URL}/user/resetpassword`, form);
  }

  disableUser(username: string): Observable<any> {
    return this.http.delete(`${this.API_URL}/user/${username}`);
  }

  lockUnlockUser(username: string): Observable<any> {
    return this.http.put(`${this.API_URL}/user/lockunlock/${username}`, null);
  }

  findUserByUsername(username: string): Observable<IUser> {
    return this.http.get<IUser>(`${this.API_URL}/user/${username}`);
  }

  updatePassword(form: FormData): Observable<any> {
    return this.http.put(`${this.API_URL}/user/updatepassword`, form);
  }

  updateProfile(user: IUser): Observable<IUser> {
    return this.http.put<IUser>(`${this.API_URL}/user/profile`, user);
  }

  showCreateUser(): BsModalRef {
    const modal: BsModalRef = this.modalService.show(UserAccountFormComponent, { backdrop: 'static' });
    return modal;
  }


  // ===================== FormGroups ======================
  getUserForm(): FormGroup {
    return this.formBuilder.group({
      fullName: ['', UserValidation.fullName()],
      email: ['', UserValidation.email()],
      username: ['', UserValidation.username()],
      active: [true],
      nonLocked: [true]
    })
  }

  getProfileForm(user: IUser): FormGroup {
    return this.formBuilder.group({
      fullName: [user.fullName, UserValidation.fullName()],
      email: [user.email, UserValidation.email()]
    })
  }

  getPasswordForm(): FormGroup {
    return this.formBuilder.group({
      oldPassword: ['', UserValidation.required()],
      newPassword: ['', UserValidation.password()],
      repeatpassword: ['', UserValidation.required()]
    }, { validators: this.checkPasswords });
  }

  private checkPasswords: ValidatorFn = (group: AbstractControl): ValidationErrors | null => {
    let pass = group.get('newPassword')?.value;
    let confirmPass = group.get('repeatpassword')?.value
    return pass === confirmPass ? null : { notSame: true }
  }


  // ===================== FormGroups ======================

  onCreateUser(): Subject<IUser> {
    const bsModalRef: BsModalRef = this.modalService.show(UserAccountFormComponent);
    return (<UserAccountFormComponent>bsModalRef.content).userResult;
  }

  onUpdateUser(user: IUser): Subject<IUser> {
    const bsModalRef: BsModalRef = this.modalService.show(UserAccountFormComponent);
    bsModalRef.content.loadForm(user);
    return (<UserAccountFormComponent>bsModalRef.content).userResult;
  }

  onShowUser(user: IUser): void {
    const bsModalRef: BsModalRef = this.modalService.show(UserAccountShowComponent, {
      class: 'modal-dialog-centered'
    });
    bsModalRef.content.loadUser(user);
  }

}
