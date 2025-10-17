import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './components/navbar/navbar';
import { Card } from 'primeng/card';

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, Navbar, Card],
  templateUrl: './layout.html',
  styleUrl: './layout.css',
})
export default class Layout {}
