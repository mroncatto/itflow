import { EventEmitter, Injectable, Output } from '@angular/core';
import { JwtHelperService } from "@auth0/angular-jwt";
import { BehaviorSubject } from 'rxjs';
import { IAuthResponse } from 'src/app/core/components/user/model/auth-response';
import { IUser } from 'src/app/core/components/user/model/user';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private tokenHelper = new JwtHelperService();
  private loggedIn = new BehaviorSubject<boolean>(false);
  @Output() getLoggedInName: EventEmitter<IUser> = new EventEmitter();

  get isLoggedIn() {
    if (this.getUserFromSessionStorage() && !this.isTokenExpired()) this.loggedIn.next(true);
    return this.loggedIn.asObservable();
  }

  saveCredentials(auth: IAuthResponse): void {
    this.setUserToSessionStorage(auth.user);
    this.setTokenToSessionStorage(auth.access_token);
    this.getLoggedInName.emit(auth.user);
  }

  async removeCredentials(): Promise<void> {
    sessionStorage.clear();
    this.loggedIn.next(false); 
  }

  getUserFromSessionStorage(): IUser | null {
    const user = sessionStorage.getItem('user') as string;
    return JSON.parse(user);
  }

  isTokenExpired(): boolean {
    const token = this.getTokenFromSessionStorage();
    if (token != null) return this.tokenHelper.isTokenExpired(token);
    return true;
  }

  existsToken(): boolean {
    return this.getTokenFromSessionStorage() !== null; 
  }

  getTokenFromSessionStorage(): string | null {
    return sessionStorage.getItem('token');
  }

  private setTokenToSessionStorage(token: string): void {
    sessionStorage.setItem('token', token);
  }

  private setUserToSessionStorage(user: IUser): void {
    sessionStorage.setItem('user', JSON.stringify(user));
  }
}
