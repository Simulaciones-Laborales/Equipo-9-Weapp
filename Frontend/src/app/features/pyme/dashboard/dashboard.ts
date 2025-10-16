import { Component, inject } from '@angular/core';
import { Title } from '@components/title/title';
import { TokenStorage } from '@core/services/token-storage';
import { Card } from 'primeng/card';
import { Subtitle } from '@components/subtitle/subtitle';
import { Button } from 'primeng/button';
import { Divider } from 'primeng/divider';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  imports: [Title, Card, Subtitle, Button, Divider],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export default class Dashboard {
  private readonly _tokenStorage = inject(TokenStorage);
  private readonly _router = inject(Router);

  get user() {
    return this._tokenStorage.user();
  }

  logout() {
    this._tokenStorage.clear();
    this._router.navigateByUrl('pyme/auth');
  }
}
