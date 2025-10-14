import { Component } from '@angular/core';
import { Title } from '@components/title/title';
import { Card } from 'primeng/card';

@Component({
  selector: 'app-credits',
  imports: [Title, Card],
  templateUrl: './credits.html',
  styleUrl: './credits.css',
})
export default class Credits {}
