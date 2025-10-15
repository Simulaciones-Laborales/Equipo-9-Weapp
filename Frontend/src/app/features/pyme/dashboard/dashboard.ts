import { Component, inject } from '@angular/core';
import { Title } from '@components/title/title';
import { TokenStorage } from '@core/services/token-storage';
import { Card } from 'primeng/card';
import { Subtitle } from '@components/subtitle/subtitle';
import { Button } from 'primeng/button';
import { Divider } from 'primeng/divider';

@Component({
  selector: 'app-dashboard',
  imports: [Title, Card, Subtitle, Button, Divider],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export default class Dashboard {
  private readonly _tokenStorage = inject(TokenStorage);

  get user() {
    return this._tokenStorage.user();
  }
}
