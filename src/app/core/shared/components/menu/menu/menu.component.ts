import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { IUser } from 'src/app/core/components/user/model/user';
import { ILanguage } from '../../../commons/interface/language';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { MenuService } from '../../../services/menu/menu.service';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {

  user!: IUser | null;
  currentLang: string = '';
  languages: ILanguage[] = [];

  constructor(
    private router: Router,
    private authService: AuthenticationService,
    private menuService: MenuService
  ) {
    this.authService.getLoggedInName.subscribe(user => {
      this.user = user;
    });
    this.loadLang();
  }

  isLoggedIn(): Observable<boolean> {
    return this.authService.isLoggedIn;
  }

  ngOnInit(): void {
    this.menuService.onLoadTheme();
    if (!this.user) this.loadUser();
  }

  onChangeTheme(): void {
    this.menuService.onChangeTheme();
  }

  onChangeLanguage(): void {
    this.menuService.onChangeLanguage();
  }

  onLogout(): void {
    this.authService.removeCredentials().then(() => {
      this.router.navigate(['/login']);
    });
  }

  private loadUser(): void {
    this.user = this.authService.getUserFromSessionStorage();
  }

  private loadLang(): void {
    this.currentLang = this.menuService.getCurrentLang();
    this.languages = this.menuService.getLanguages();
  }

}
