import { Injectable, Injector } from '@angular/core';
import { AbstractService } from '../abstract/abstract.service';

@Injectable({
  providedIn: 'root'
})
export class GuardService extends AbstractService {

  constructor(injector: Injector) {
    super(injector);
  }

  alertExpiredToken() {
    //const title = this.translateService.instant('app.warning.sessionExpired');
    //const msg = this.translateService.instant('app.warning.loginRequired');
    const title = this.translateService.instant('Session Expired');
    const msg = this.translateService.instant('Login is Required');
    //this.toastService.warning(title, msg, ToastPosition.TOP_RIGHT);
  }

  isTokenExpired(): boolean {
    return this.authService.isTokenExpired();
  }

  existsToken(): boolean {
    return this.authService.existsToken();
  }

  async removeCredentials(): Promise<void> {
    this.removeCredentials();
  }
}
