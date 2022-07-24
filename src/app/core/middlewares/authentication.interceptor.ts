import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { InterceptorService } from '../shared/services/authentication/interceptor.service';
import { AuthenticationService } from '../shared/services/authentication/authentication.service';
import { environment } from 'src/environments/environment';

@Injectable()
export class AuthenticationInterceptor implements HttpInterceptor {

  private readonly API_URL = environment.API_URL;
  constructor(private authService: AuthenticationService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    
    // Do not intercept if is log in
    if (request.url.includes(`${this.API_URL}/auth/login`)) return next.handle(request);

    // Do not intercept if comes from translate service
    if (request.url.includes(`/assets/i18n`)) return next.handle(request);

    // If token is expired then remove the credentials from session storage
    if (this.authService.isTokenExpired()) {
      this.authService.removeCredentials().then(() => {
        return next.handle(request);
      })
    }

    // Get the token
    const token = this.authService.getTokenFromSessionStorage();

    // Inject the token in the header
    if (token) {
      const authenticatedRequest = request.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
      return next.handle(authenticatedRequest);
    }

    return next.handle(request);
  }
}
