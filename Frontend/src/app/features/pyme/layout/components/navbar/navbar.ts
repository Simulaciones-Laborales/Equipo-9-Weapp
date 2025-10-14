import { Component } from '@angular/core';
import { NavbarLink } from '../navbar-link/navbar-link';

@Component({
  selector: 'app-navbar',
  imports: [NavbarLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {}
