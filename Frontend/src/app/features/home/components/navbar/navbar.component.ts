import { ChangeDetectionStrategy, Component } from '@angular/core';
import { MenubarModule } from 'primeng/menubar';
import { ButtonModule } from 'primeng/button';
import { RouterLink } from '@angular/router';
import { Logo } from '@components/logo/logo';

@Component({
  selector: 'navbar',
  imports: [MenubarModule, ButtonModule, RouterLink, Logo],
  templateUrl: './navbar.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NavbarComponent {}
