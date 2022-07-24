import { Injectable, Injector } from '@angular/core';
import { AbstractService } from '../abstract/abstract.service';

@Injectable({
  providedIn: 'root'
})
export class InterceptorService extends AbstractService {

  constructor(injector: Injector) { super(injector) }

  getToken(): string | null {
    return this.authService.getTokenFromSessionStorage();
  }

  isTokenExpired(): boolean {
    return this.authService.isTokenExpired();
  }

  async removeCredentials(): Promise<void> {
    this.authService.removeCredentials();
  }
}
