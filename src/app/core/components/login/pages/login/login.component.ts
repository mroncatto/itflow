import { Component, OnInit } from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { IAuthResponse } from '../../../user/model/auth-response';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginForm!: FormGroup;
  authentication!: IAuthResponse;
  private redirect!: string;
  constructor(
    private loginService: LoginService,
    private activatedRouter: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.loadForm();
    this.readUrlParam();
  }

  onLogin(): void {
    if (this.loginForm.valid) {
      this.loginService.login(this.loginService.createDataForm(this.loginForm.value)).subscribe({
        next: (data) => this.authentication = data,
        error: (err) => {
          this.loginService.onFail(err),
            this.loginForm.reset()
        },
        complete: () => this.onAuthenticate(),
      })
    }
  }

  private readUrlParam(): void {
    this.activatedRouter.queryParams.subscribe(
      query => {
        if (query['redirect']) {
          this.redirect = query['redirect'];
        }
      }
    ).unsubscribe();
  }

  private loadForm(): void {
    this.loginForm = this.loginService.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }
  private onAuthenticate(): void {
    this.loginService.saveCredentials(this.authentication);
    this.loginService.navigate(this.redirect ? this.redirect : 'dashboard');
  }

}
