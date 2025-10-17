import { ChangeDetectionStrategy, Component } from '@angular/core';
import {MenubarModule} from 'primeng/menubar';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-navbar',
  imports: [MenubarModule, ButtonModule, RouterLink],
  templateUrl: './navbar.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NavbarComponent{   
 }
