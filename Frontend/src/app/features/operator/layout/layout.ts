import { Component } from '@angular/core';
import { Navbar } from './components/navbar/navbar';
import { RouterOutlet } from '@angular/router';
import { Card } from 'primeng/card';

@Component({
  selector: 'app-layout',
  imports: [Navbar, RouterOutlet, Card],
  templateUrl: './layout.html',
  styleUrl: './layout.css',
})
export class Layout {}
