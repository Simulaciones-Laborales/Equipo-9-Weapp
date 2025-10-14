import { Component } from '@angular/core';
import { Title } from '@components/title/title';
import { Card } from 'primeng/card';
import { TableModule } from 'primeng/table';

@Component({
  selector: 'app-credits',
  imports: [Title, Card, TableModule],
  templateUrl: './credits.html',
  styleUrl: './credits.css',
})
export default class Credits {}
