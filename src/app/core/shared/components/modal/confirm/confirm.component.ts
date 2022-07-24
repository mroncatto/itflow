import { Component, OnInit, ChangeDetectionStrategy, Input } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-confirm',
  templateUrl: './confirm.component.html',
  styleUrls: ['./confirm.component.css']
})
export class ConfirmComponent implements OnInit {

  @Input() title: string = "";
  @Input() message: string = "";
  @Input() param: string = "";

  confirmResult!: Subject<boolean>;

   constructor(public bsModalRef: BsModalRef) {}

  ngOnInit(): void {
    this.confirmResult = new Subject();
  }

  onConfirm(): void {
    this.confirmAndClose(true);
  }

  onDecline(): void {
    this.confirmAndClose(false);
  }

  private confirmAndClose(res : boolean){
    this.confirmResult.next(res);
    this.bsModalRef.hide();
  }

}
