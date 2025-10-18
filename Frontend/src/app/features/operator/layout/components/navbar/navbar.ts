import { Component } from '@angular/core';
import { NavbarLink } from '../navbar-link/navbar-link';
import { Logo } from '@components/logo/logo';

@Component({
  selector: 'app-navbar',
  imports: [NavbarLink, Logo],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {}
