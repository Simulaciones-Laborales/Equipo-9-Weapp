import { Component } from '@angular/core';
import { Navbar } from './components/navbar/navbar';

@Component({
  selector: 'app-layout',
  imports: [Navbar],
  templateUrl: './layout.html',
  styleUrl: './layout.css',
})
export class Layout {}
