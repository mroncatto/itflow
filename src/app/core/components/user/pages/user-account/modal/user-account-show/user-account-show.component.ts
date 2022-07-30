import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { IUser, User } from '../../../../model/user';

@Component({
  selector: 'app-user-account-show',
  templateUrl: './user-account-show.component.html',
  styleUrls: ['./user-account-show.component.css']
})
export class UserAccountShowComponent implements OnInit {

  user!: User;
  loading: boolean = false;
  private sub: Subscription[] = [];

  constructor(
    private modal: BsModalRef
  ) { }

  ngOnInit(): void {
  }

  loadUser(user: IUser): void {
    this.user = user;
  }

  getFirstName(): string {
    if (this.user.fullName?.length > 0) {
      return this.user.fullName.split(" ")[0];
    }
    return '';
  }

  getUserRole(): string {
    const roles = this.user.role;
    if (roles.length > 0) {
      roles.sort();
      return roles[0].role;
    }
    return '';
  }

  closeModal() {
    this.modal.hide();
  }

}
