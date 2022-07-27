import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { ToastPosition } from '../../commons/enum/toastPosition.enum';

@Injectable({
  providedIn: 'root'
})
export class AlertService {

  constructor(private toast: ToastrService) { }

  success(title:string, msg: string, positionClass: ToastPosition): void {
    this.toast.success(msg, title, {
      positionClass: positionClass,
      disableTimeOut: false
    });
   }

   error(title:string, msg: string, positionClass: ToastPosition): void {
    this.toast.error(msg, title, {
      positionClass: positionClass,
      disableTimeOut: false,
      progressBar: true
    });
   }

   warning(title:string, msg: string, positionClass: ToastPosition): void {
    this.toast.warning(msg, title, {
      positionClass: positionClass,
      disableTimeOut: false
    });
   }

   info(title:string, msg: string, positionClass: ToastPosition): void {
    this.toast.info(msg, title, {
      positionClass: positionClass,
      disableTimeOut: false
    });
   }
}
