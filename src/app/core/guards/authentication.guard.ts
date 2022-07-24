import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { GuardService } from '../shared/services/authentication/guard.service';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationGuard implements CanActivate {

  private readonly allowed_url: string[] = ["/login"];
  constructor(private guardService: GuardService) { }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    // Is allowed URL
    if (this.allowed_url.includes(state.url)) return true;

    // If token is valid
    // TODO: Criar refresh token method
    if (!this.guardService.isTokenExpired()) return true;

    if(this.guardService.existsToken()) this.guardService.alertExpiredToken();

    // Remove credentials
    this.guardService.removeCredentials().then(() => {
      //Go to login page
      this.guardService.navigate(`/login`, state.url);
    });

    return false;

  }
  
}
