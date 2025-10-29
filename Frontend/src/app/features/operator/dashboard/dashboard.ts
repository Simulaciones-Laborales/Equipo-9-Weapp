import { Component, inject } from '@angular/core';
import { ChartModule } from 'primeng/chart';
import { ChartItem } from './chart-item/chart-item';
import { Header } from '@components/header/header';
import { MenuItem } from 'primeng/api';
import { Card } from 'primeng/card';
import { Store } from './store';
import { LoadingSpinner } from '@components/loading-spinner/loading-spinner';

@Component({
  selector: 'app-dashboard',
  imports: [ChartModule, ChartItem, Header, Card, LoadingSpinner],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
  providers: [Store],
})
export default class Dashboard {
  readonly store = inject(Store);

  readonly menu: MenuItem[] = [
    { icon: 'pi pi-home', routerLink: './' },
    { label: 'Estadísticas', routerLink: './' },
  ];

  readonly STATUS_COLORS: { [key: string]: string } = {
    APPROVED: '#00B894',
    PENDING: '#FFD740',
    REJECTED: '#D63031',
    UNDER_REVIEW: '#53A9DC',
    VERIFIED: '#00C853',
  };

  getHoverColor = (baseColor: string): string => {
    const hoverMap: { [key: string]: string } = {
      '#00B894': '#55C6B0',
      '#FFD740': '#FFE082',
      '#D63031': '#FF6B6B',
    };
    return hoverMap[baseColor] || baseColor;
  };

  getBackgroundColors = (labels: any) => {
    return labels.map((label: any) => this.STATUS_COLORS[label] || '#CCCCCC');
  };

  getHoverBackgroundColors = (labels: any) => {
    return this.getBackgroundColors(labels).map((color: any) => this.getHoverColor(color));
  };

  async ngOnInit() {
    await this.store.fetch();
  }
}
