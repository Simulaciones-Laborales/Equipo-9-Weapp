import { Component } from '@angular/core';
import { Title } from '@components/title/title';
import { Subtitle } from '@components/subtitle/subtitle';
import { TableModule } from 'primeng/table';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Button } from 'primeng/button';

@Component({
  selector: 'app-credits',
  imports: [Title, Subtitle, TableModule, CurrencyPipe, DatePipe, Button],
  templateUrl: './credits.html',
  styleUrl: './credits.css',
})
export default class Credits {
  today = new Date();
}
