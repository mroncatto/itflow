import { Injectable } from '@angular/core';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
import { Subject } from 'rxjs';
import { BootstrapTheme } from '../../commons/enum/bootstrap-theme.enum';
import { ConfirmComponent } from '../../components/modal/confirm/confirm.component';

@Injectable({
  providedIn: 'root'
})
export class ModalService {

  constructor(private modalService: BsModalService) { }

  showAlertSuccess(message: string) {
    //this.showAlert(message, BootstrapTheme.SUCCESS);
  }

  showAlertDanger(message: string) {
    //this.showAlert(message, BootstrapTheme.DANGER);
  }

  showConfirm(title: string, message: string, param?:string): Subject<boolean>{
    const bsModalRef: BsModalRef = this.modalService.show(ConfirmComponent);
    bsModalRef.content.title = title;
    bsModalRef.content.message = message;
    if(param) bsModalRef.content.param = param;

    return (<ConfirmComponent>bsModalRef.content).confirmResult;
  }

  /*private showAlert(message: string, type: BootstrapTheme) {
    const bsModalRef: BsModalRef = this.modalService.show(AlertComponent);
    bsModalRef.content.type = type;
    bsModalRef.content.message = message;
  }*/
}
