import { Injectable, Injector } from '@angular/core';
import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { Observable } from 'rxjs';
import { AbstractService } from 'src/app/core/shared/services/abstract/abstract.service';
import { IUser } from '../model/user';
import { UserValidation } from '../validation/user-validation';

@Injectable({
  providedIn: 'root'
})
export class UserService extends AbstractService {

  constructor(injector: Injector) { super(injector) }

  getUserFromCache(): IUser {
    return this.authService.getUserFromSessionStorage() as IUser;
  }

  getUsers(): Observable<IUser[]>{
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

  findUserByUsername(username: string): Observable<IUser>{
    return this.http.get<IUser>(`${this.API_URL}/user/${username}`);
  }

  updatePassword(form: FormData): Observable<any> {
    return this.http.put(`${this.API_URL}/user/updatepassword`, form);
  }

  updateProfile(user: IUser): Observable<IUser>{
    return this.http.put<IUser>(`${this.API_URL}/user/profile`, user);
  }




  // ===================== FormGroups ======================
  getProfileForm(user: IUser): FormGroup {
    return this.formBuilder.group({
      fullName: [user.fullName, UserValidation.fullName()],
      email: [user.email, UserValidation.email()]
    })
  }

  getPasswordForm(): FormGroup{
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

}
