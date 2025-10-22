import { Component, input, signal } from '@angular/core';
import { NavbarLink } from '../navbar-link/navbar-link';
import { NgClass } from '@angular/common';
import { Logo } from '@components/logo/logo';
import { DisplayKycStatus } from '@core/models/kyc-model';

@Component({
  selector: 'app-navbar',
  imports: [NavbarLink, NgClass, Logo],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  readonly kycStatus = input.required<DisplayKycStatus>();
  readonly show = signal<boolean>(false);
}
