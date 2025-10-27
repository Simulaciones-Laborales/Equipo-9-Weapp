import { Component, inject } from '@angular/core';
import { NavbarLink } from '../navbar-link/navbar-link';
import { Logo } from '@components/logo/logo';
import { LogoutService } from '@core/services/logout-service';

@Component({
  selector: 'app-navbar',
  imports: [NavbarLink, Logo],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  private readonly _logoutService = inject(LogoutService);

  logout() {
    this._logoutService.logout();
  }
}
