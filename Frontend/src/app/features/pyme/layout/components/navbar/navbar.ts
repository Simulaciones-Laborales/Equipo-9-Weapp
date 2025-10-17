import { Component, signal } from '@angular/core';
import { NavbarLink } from '../navbar-link/navbar-link';
import { NgClass } from '@angular/common';
import { Logo } from '@components/logo/logo';

@Component({
  selector: 'app-navbar',
  imports: [NavbarLink, NgClass, Logo],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  readonly show = signal<boolean>(false);
}
