import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MenubarModule } from 'primeng/menubar';
import { ButtonModule } from 'primeng/button';
import { RouterLink } from '@angular/router';
import { Logo } from '@components/logo/logo';
import { TokenStorage } from '@core/services/token-storage';
import { UserRole } from '@core/models/user-model';

@Component({
  selector: 'navbar',
  imports: [MenubarModule, ButtonModule, RouterLink, Logo],
  templateUrl: './navbar.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NavbarComponent {
  private readonly _tokenStorage = inject(TokenStorage);

  get actionCallText() {
    return this._isLogged ? 'Ir al Dashboard' : 'Iniciar Sesión';
  }

  get actionCallRoute() {
    return this._isLogged ? `/${this._loggedRoute}` : '/pyme/auth';
  }

  private get _loggedRoute() {
    const { role } = this._tokenStorage.user()!;

    return role === UserRole.OPERADOR ? 'operador' : 'pyme';
  }

  private get _isLogged() {
    return this._tokenStorage.token() !== null;
  }
}
