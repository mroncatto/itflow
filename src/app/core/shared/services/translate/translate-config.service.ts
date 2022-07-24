import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ILanguage } from '../../commons/interface/language';

@Injectable({
  providedIn: 'root'
})
export class TranslateConfigService {

  private languages : ILanguage[] = [
    { id: "en", name: "English" },
    { id: "es", name: "Español" },
    { id: "pt", name: "Português" }
  ];

  private currentLang: string = "en";
  private browserLang: string = window.navigator.language.substring(0, 2);

  constructor(private translate: TranslateService, private http: HttpClient) {
    this.translate.use('en');
    // Load language from browser language if exists
    this.languages.forEach(l => {
      if (l.id === this.browserLang) this.currentLang = this.browserLang
    })

    // If exists language on localStorage then overwrite it
    let localStorageLang = this.getLangFromLocalStorage();
    if (localStorageLang) this.currentLang = localStorageLang;

    //Update the service language
    this.onChangeLanguage();
  }

  private setLanguage(type: string) {
    this.translate.use(type);
  }

  onChangeLanguage(): void {
    this.setLanguage(this.currentLang);
    localStorage.setItem("lang", this.currentLang);
  }

  private getLangFromLocalStorage(): string | null {
    return localStorage.getItem("lang");
  }

  getAvailableLanguages(): any {
    return this.languages;
  }

  instant(value: string, param?: any): string {
    return this.translate.instant(value, param);
  }

  get currentLanguage(): string {
    return this.currentLang;
  }
}
