import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Logo } from '@components/logo/logo';
import { Card } from 'primeng/card';

@Component({
  selector: 'app-not-found',
  imports: [Logo, Card, RouterLink],
  templateUrl: './not-found.html',
  styleUrl: './not-found.css',
})
export default class NotFound {}
