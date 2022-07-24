import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { EMPTY, Subscription, switchMap, take } from 'rxjs';
import { ModalService } from 'src/app/core/shared/services/modal/modal.service';
import { IUser } from '../../model/user';
import { UserService } from '../../services/user.service';

@Component({
  templateUrl: './user-account.component.html',
  styleUrls: ['./user-account.component.css']
})
export class UserAccountComponent implements OnInit, OnDestroy {

  private sub: Subscription[] = [];
  users: IUser[] = [];
  errorResponse!: HttpErrorResponse;
  bsModalRef!: BsModalRef;

  constructor(
    private userService: UserService,
    private modalService: ModalService
  ) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  ngOnDestroy(): void {
    this.sub.forEach(sub => sub.unsubscribe());
  }

  private loadUsers(): void {
    this.userService.getUsers().subscribe({
      next: (data) => this.users = data,
      error: (err) => {
        this.userService.onFail(err);
        this.errorResponse = err;
      }
    })
  }

  onResetPassword(user: IUser): void {
    this.sub.push(
      this.modalService.showConfirm('warning', 'user.resetPassword', user.fullName)
        .asObservable()
        .pipe(
          take(1),
          switchMap(async (result) => result ? this.resetUserPassword(user) : EMPTY)
        )
        .subscribe({
          error: (err) => this.userService.onFail(err)
        })
    )
  }

  private resetUserPassword(user: IUser): void {

  }


  // ============================= LOCKUNLOCK USER ============================
  beforeLockUnLockUser(user: IUser): void {
    if (user.nonLocked) {
      this.sub.push(
        this.modalService.showConfirm('warning', 'user.lock', user.fullName)
          .asObservable()
          .pipe(
            take(1),
            switchMap(async (result) => result ? this.onLockUnLockUser(user) : EMPTY)
          )
          .subscribe({
            error: (err) => this.userService.onFail(err)
          })
      )
    } else {
      this.onLockUnLockUser(user)
    }
  }

  private onLockUnLockUser(user: IUser): void {
    this.sub.push(
      this.userService.lockUnlockUser(user.username).subscribe({
        next: () => this.afterLockUnLockUser(user),
        error: (err) => this.userService.onFail(err)
      })
    )
  }


  private afterLockUnLockUser(user: IUser): void {
    user.nonLocked = !user.nonLocked;
    this.users.forEach((u) => {
      if (u.username === user.username) u = user
    })
    if (user.nonLocked) {
      this.userService.onInfo("successfully", "userUnBlocked");
    } else {
      this.userService.onInfo("successfully", "userBlocked");
    }

  }




  // ============================= DISABLE USER ============================

  beforeDisable(user: IUser): void {
    this.sub.push(
      this.modalService.showConfirm('warning', 'user.disable', user.fullName)
        .asObservable()
        .pipe(
          take(1),
          switchMap(async (result) => result ? this.onDisableUser(user) : EMPTY)
        )
        .subscribe({
          error: (err) => this.userService.onFail(err)
        })
    )
  }

  private onDisableUser(user: IUser): void {
    this.sub.push(
      this.userService.disableUser(user.username).subscribe({
        next: () => this.afterDisableUser(user),
        error: (err) => this.userService.onFail(err)
      })
    )
  }

  private afterDisableUser(user: IUser): void {
    user.active = false;
    this.users.forEach((u) => {
      if (u.username === user.username) u = user
    })
    this.userService.onInfo("successfully", "userDisabled");
  }

}
