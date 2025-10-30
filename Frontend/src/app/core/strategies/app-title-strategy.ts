import { inject, Injectable } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { RouterStateSnapshot, TitleStrategy } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AppTitleStrategy extends TitleStrategy {
  private readonly _title = inject(Title);

  override updateTitle(snapshot: RouterStateSnapshot): void {
    const prefix = 'CreditFlow';
    const pageTitle = this.buildTitle(snapshot) || this._title.getTitle();
    const fullTitle = `${prefix} - ${pageTitle}`;

    if (snapshot.url.includes('operador')) {
      return this._title.setTitle(`${fullTitle} [Operador]`);
    }

    this._title.setTitle(fullTitle);
  }
}
