import { Injectable, Injector } from '@angular/core';
import { Observable } from 'rxjs';
import { AbstractService } from 'src/app/core/shared/services/abstract/abstract.service';
import { IAuthResponse } from '../../user/model/auth-response';

@Injectable({
  providedIn: 'root'
})
export class LoginService extends AbstractService {

  constructor(injector: Injector) { super(injector) }

  login(login: FormData): Observable<IAuthResponse> {
    return this.http.post<IAuthResponse>(`${this.API_URL}/auth/login`, login);
  }

  saveCredentials(auth: IAuthResponse): void {
    this.authService.saveCredentials(auth);
    this.onInfo('welcome', 'authenticated');
  }

}
