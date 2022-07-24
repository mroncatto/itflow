import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, Injector } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { ToastPosition } from '../../commons/enum/toastPosition.enum';
import { ToastType } from '../../commons/enum/toastType.enum';
import { AlertService } from '../alert/alert.service';
import { AuthenticationService } from '../authentication/authentication.service';
import { TranslateConfigService } from '../translate/translate-config.service';

@Injectable({
  providedIn: 'root'
})
export class AbstractService {

  protected translateService: TranslateConfigService;
  protected router: Router;
  protected authService: AuthenticationService;
  protected http: HttpClient;
  alertService: AlertService;
  formBuilder: FormBuilder;
  
  readonly API_URL = environment.API_URL;
  constructor(injector: Injector){
    this.alertService = injector.get(AlertService);
    this.translateService = injector.get(TranslateConfigService);
    this.router = injector.get(Router);
    this.authService = injector.get(AuthenticationService);
    this.http = injector.get(HttpClient);
    this.formBuilder = injector.get(FormBuilder);
  }

   // ------------------ Router --------------------------------
   //TODO: Criar interface para parametros dinamicos
  navigate(url: string, redirect: string='', ...params: string[]): void {
    if(redirect && redirect.length > 0 && redirect !== '/login') {
      this.router.navigate([url], {queryParams: {redirect: redirect.replace('/','')} });
    } else {
    this.router.navigate([url]);
    }
  }

  createDataForm(form: any, ...deleteParams: string[]): FormData {
    const formData: FormData = new FormData();
    deleteParams.forEach(p => {
      delete form[p]
    });
    for(var key in form){
      formData.append(key, form[key]);
    }
    return formData;
  }


  // ------------------ Toast Alerts --------------------------------
  onSuccess(t: string, m: string): void {
    this.showAlert(this.translate(`app.success.${t}`), `app.success.${m}`, ToastType.SUCCESS);
  }

  onInfo(t: string, m: string): void {
    const msg = this.translateService.instant(`app.info.${m}`);
    const title = this.translateService.instant(`app.info.${t}`);
    this.showAlert(title, msg, ToastType.INFO);
  }

  onError(t: string, m: string): void {
    const msg = this.translateService.instant(`app.info.${m}`);
    const title = this.translateService.instant(`app.info.${t}`);
    this.showAlert(title, msg, ToastType.ERROR);
  }

  onFail(err: HttpErrorResponse): void {
    this.processHttpError(err);
  }

  private processHttpError(err: HttpErrorResponse): void {
    console.log(err);
    
    switch(err.status){
      case 400:
        this.badRequestError(err.error);
        break;
      case 401:
          this.authenticationRequest(err.error);
          break;
      case 403:
          this.unauthorized(err.error);
          break;
      case 404:
          this.notFound(err.error);
          break;
      default:
          this.undefinedError();

    }
  }

  private badRequestError(err: any){
    const title = this.translateService.instant(`api.warning.${err?.error}`);
    const msg = this.translateService.instant(`api.warning.${err?.message}`);
    this.showAlert(title, msg, ToastType.WARNING);
  }

  private authenticationRequest(err: any){
    const title = this.translateService.instant(`api.error.${err?.error}`);
    const msg = this.translateService.instant(`api.error.${err?.message}`);
    this.showAlert(title, msg, ToastType.ERROR);
  }

  private unauthorized(err: any){
    const title = this.translateService.instant(`api.error.${err?.error}`);
    const msg = this.translateService.instant(`api.error.UNAUTHORIZED`);
    this.showAlert(title, msg, ToastType.ERROR);
  }

  private notFound(err: any){
    const title = this.translateService.instant(`api.warning.${err?.error}`);
    const msg = this.translateService.instant(`api.warning.${err?.message}`);
    this.showAlert(title, msg, ToastType.WARNING);
  }

  private undefinedError(){
    const title = this.translateService.instant(`api.error.unknown`);
    const msg = this.translateService.instant(`api.error.unknown`);
    this.showAlert(title, msg, ToastType.ERROR);
  }

  private showAlert(title: string, msg: string, type: ToastType, position: ToastPosition = ToastPosition.TOP_RIGHT): void {
    switch(type){
      case 'success':
        this.alertService.success(title, msg, position);
        break;
      case 'warning':
        this.alertService.warning(title, msg, position);
        break;
      case 'error':
        this.alertService.error(title, msg, position);
        break;
      case 'info':
        this.alertService.info(title, msg, position);
        break;
      default:
        this.alertService.info(title, msg, position);
    }
  }

  translate(value: string, param?: string): string {
    return this.translateService.instant(value, param)
  }
}
